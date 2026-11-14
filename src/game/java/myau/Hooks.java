package myau;

import myau.event.EventManager;
import myau.event.types.EventType;
import myau.events.*;
import myau.management.RotationState;
import myau.module.modules.AntiDebuff;
import myau.module.modules.AutoBlockIn;
import myau.module.modules.GhostHand;
import myau.module.modules.HitBox;
import myau.module.modules.Scaffold;
import myau.module.modules.Jesus;
import myau.module.modules.KeepSprint;
import myau.module.modules.NoSlow;
import myau.module.modules.Sprint;
import myau.module.modules.KillAura;
import myau.module.modules.NoHitDelay;
import myau.util.Acc;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import java.util.List;
import myau.util.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

/**
 * Every engine hook in ONE place. The Eaglercraft source is edited so that each vanilla method calls the matching
 * Hooks.xxx method (see ENGINE_HOOKS.md for the exact spots). All logic lives here, not in the engine classes.
 */
public final class Hooks {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static float overrideYaw = Float.NaN, overridePitch = Float.NaN, pendingYaw = Float.NaN, pendingPitch = Float.NaN;

    private Hooks() {}

    // ---- Minecraft.runTick ----
    public static void runTickHead() {
        if (mc.theWorld != null && mc.thePlayer != null) EventManager.call(new TickEvent(EventType.PRE));
    }

    public static void runTickReturn() {
        if (mc.theWorld != null && mc.thePlayer != null) EventManager.call(new TickEvent(EventType.POST));
    }

    // ---- Minecraft.loadWorld(WorldClient, String) head ----
    public static void loadWorld() { EventManager.call(new LoadWorldEvent()); }

    // ---- Minecraft.clickMouse head: returns true -> cancel the click ----
    public static boolean leftClick() {
        LeftClickMouseEvent e = new LeftClickMouseEvent();
        EventManager.call(e);
        return e.isCancelled();
    }

    /** NoHitDelay: call at the head of Minecraft.clickMouse; if true set leftClickCounter = 0. */
    public static boolean noHitDelay() {
        return Myau.moduleManager != null && Myau.moduleManager.modules.get(NoHitDelay.class).isEnabled();
    }

    public static boolean rightClick() {
        RightClickMouseEvent e = new RightClickMouseEvent();
        EventManager.call(e);
        return e.isCancelled();
    }

    /** Minecraft.sendClickBlockToController head: true -> cancel (and call playerController.resetBlockRemoving()). */
    public static boolean hitBlock() {
        HitBlockEvent e = new HitBlockEvent();
        EventManager.call(e);
        return e.isCancelled();
    }

    /** Minecraft.runTick keyboard loop: key pressed while no screen is open. */
    public static void key(int key) {
        Myau.onKeyPress(key);
        EventManager.call(new KeyEvent(key));
    }

    // ---- EntityPlayerSP.onUpdate ----
    private static boolean loaded(EntityPlayerSP p) { return p.worldObj.isBlockLoaded(new BlockPos(p.posX, 0.0, p.posZ)); }

    /** Head of EntityPlayerSP.onUpdate. */
    public static void playerUpdateHead(EntityPlayerSP p) {
        if (!loaded(p)) return;
        UpdateEvent event = new UpdateEvent(EventType.PRE, p.lastReportedYaw, p.lastReportedPitch, p.rotationYaw, p.rotationPitch);
        EventManager.call(event);
        RotationState.applyState(event.isRotated() && !p.isRiding(), event.getNewYaw(), event.getNewPitch(), event.getPreYaw(), event.isRotating());
        if (event.isRotated()) {
            pendingYaw = p.rotationYaw;
            pendingPitch = p.rotationPitch;
            overrideYaw = event.getNewYaw();
            overridePitch = event.getNewPitch();
        } else {
            pendingYaw = pendingPitch = overrideYaw = overridePitch = Float.NaN;
        }
    }

    /** In EntityPlayerSP.onUpdate, immediately before `if (this.isRiding())`. */
    public static void applyRotationOverride(EntityPlayerSP p) {
        if (!Float.isNaN(overrideYaw) && !Float.isNaN(overridePitch)) {
            p.rotationYaw = overrideYaw;
            p.rotationPitch = overridePitch;
        }
    }

    /** In EntityPlayerSP.onUpdate, immediately before `this.onUpdateWalkingPlayer();`. */
    public static void motionUpdate() { EventManager.call(new PlayerUpdateEvent()); }

    /** End of EntityPlayerSP.onUpdate (inside the isBlockLoaded block). */
    public static void playerUpdateReturn(EntityPlayerSP p) {
        if (!loaded(p)) return;
        if (!Float.isNaN(pendingYaw) && !Float.isNaN(pendingPitch)) {
            p.lastReportedYaw = p.rotationYaw;
            p.lastReportedPitch = p.rotationPitch;
            p.rotationYaw = p.rotationYaw + MathHelper.wrapAngleTo180_float(pendingYaw - p.rotationYaw);
            p.rotationPitch = pendingPitch;
            p.prevRotationYaw = p.rotationYaw;
            p.prevRotationPitch = p.rotationPitch;
            p.prevRenderArmYaw = p.rotationYaw - (p.renderArmYaw - p.prevRenderArmYaw) * 2.0F;
            p.renderArmYaw = p.rotationYaw;
        }
        EventManager.call(new UpdateEvent(EventType.POST, p.lastReportedYaw, p.lastReportedPitch, p.rotationYaw, p.rotationPitch));
    }

    // ---- EntityPlayerSP.onLivingUpdate ----
    /** Just before `super.onLivingUpdate()` at the end of EntityPlayerSP.onLivingUpdate. */
    public static void livingUpdate() { EventManager.call(new LivingUpdateEvent()); }

    /** Right after `this.movementInput.updatePlayerMoveState();` in EntityPlayerSP.onLivingUpdate. */
    public static void moveInput() { EventManager.call(new MoveInputEvent()); }

    // ---- EntityLivingBase.moveEntityWithHeading: replace every `this.moveFlying(a, b, c)` with Hooks.moveFlying(this, a, b, c) ----
    public static void moveFlying(EntityLivingBase e, float strafe, float forward, float friction) {
        if (e instanceof EntityPlayerSP) {
            StrafeEvent event = new StrafeEvent(strafe, forward, friction);
            EventManager.call(event);
            boolean actived = RotationState.isActived();
            float yaw = e.rotationYaw;
            if (actived) e.rotationYaw = RotationState.getSmoothedYaw();
            e.moveFlying(event.getStrafe(), event.getForward(), event.getFriction());
            if (actived) e.rotationYaw = yaw;
        } else {
            e.moveFlying(strafe, forward, friction);
        }
    }

    // ---- EntityLivingBase.jump: yaw used for the sprint-jump boost ----
    public static float jumpYawRadians(EntityLivingBase e, float original) {
        return e instanceof EntityPlayerSP && RotationState.isActived() ? RotationState.getSmoothedYaw() * (float) (Math.PI / 180.0) : original;
    }

    // ---- Entity.setAngles head: true -> cancel (mouse look is locked while a module rotates) ----
    public static boolean cancelSetAngles(Entity e) {
        return e instanceof EntityPlayerSP && Myau.rotationManager != null && Myau.rotationManager.isRotated();
    }

    // ---- Entity.setVelocity head: if result != null && result.isCancelled() -> set motion to result and return ----
    public static KnockbackEvent knockback(Entity e, double x, double y, double z) {
        if (!(e instanceof EntityPlayerSP)) return null;
        KnockbackEvent event = new KnockbackEvent(x, y, z);
        EventManager.call(event);
        return event;
    }

    // ---- PlayerControllerMP.attackEntity: before this.syncCurrentPlayItem() ----
    public static void attack(Entity target) { EventManager.call(new AttackEvent(target)); }

    // ---- PlayerControllerMP.onStoppedUsingItem head: true -> return ----
    public static boolean cancelUse() {
        CancelUseEvent e = new CancelUseEvent();
        EventManager.call(e);
        return e.isCancelled();
    }

    // ---- NetworkManager: outgoing (before the packet is queued/sent). true -> do not send ----
    public static boolean packetSend(Packet<?> packet) {
        if (PacketUtil.suppressSendEvents || Myau.moduleManager == null) return false;
        PacketEvent event = new PacketEvent(EventType.SEND, packet);
        EventManager.call(event);
        if (event.isCancelled()) return true;
        if (!Myau.lagManager.isFlushing()) {
            Myau.playerStateManager.handlePacket(packet);
            if (Myau.blinkManager.isBlinking() && Myau.blinkManager.offerPacket(packet)) return true;
            if (Myau.lagManager.handlePacket(packet)) return true;
        }
        return false;
    }

    // ---- NetworkManager: incoming, before packet.processPacket(...). true -> drop it ----
    public static boolean packetReceive(Packet<?> packet) {
        if (Myau.moduleManager == null) return false;
        PacketEvent event = new PacketEvent(EventType.RECEIVE, packet);
        EventManager.call(event);
        return event.isCancelled();
    }

    // ---- EntityRenderer.renderWorldPass: right before `renderHand` (world finished, hand not yet drawn) ----
    public static void render3D(float partialTicks) { EventManager.call(new Render3DEvent(partialTicks)); }

    // ---- EntityRenderer.getMouseOver ----
    /** After `double d0 = (double)this.mc.playerController.getBlockReachDistance();` -> d0 = Hooks.raytraceRange(d0); */
    public static double raytraceRange(double range) {
        RaytraceEvent e = new RaytraceEvent(range);
        EventManager.call(e);
        return e.getRange();
    }

    /** Replace the SECOND `3.0D` constant in getMouseOver (`vec3.distanceTo(vec33) > 3.0D`) with Hooks.pickRange(3.0D). */
    public static double pickRange(double range) {
        PickEvent e = new PickEvent(range);
        EventManager.call(e);
        return e.getRange();
    }

    // ---- Entity.getCollisionBorderSize(): `return Hooks.collisionBorderSize(this, 0.1F);` (HitBox expansion) ----
    public static float collisionBorderSize(Entity e, float original) {
        return original * HitBox.getExpansion(e);
    }

    // ---- EntityRenderer.updateCameraAndRender: call renderFrameHead() at the head and renderFrameReturn() at every return ----
    // (KillAura auto-block: makes the held sword render in the blocking pose without actually using the item)
    private static ItemStack savedUsing;
    private static boolean usingSaved, countSaved;
    private static int savedCount;

    private static boolean slotSaved;
    private static int savedSlotIndex;

    private static void spoofSlot(boolean includeAutoBlockIn) {
        Scaffold sc = (Scaffold) Myau.moduleManager.modules.get(Scaffold.class);
        if (sc != null && sc.isEnabled() && sc.itemSpoof.getValue()) {
            int slot = sc.getSlot();
            if (slot >= 0) {
                if (!slotSaved) savedSlotIndex = mc.thePlayer.inventory.currentItem;
                slotSaved = true;
                mc.thePlayer.inventory.currentItem = slot;
            }
        }
        if (includeAutoBlockIn) {
            AutoBlockIn ab = (AutoBlockIn) Myau.moduleManager.modules.get(AutoBlockIn.class);
            if (ab != null && ab.isEnabled() && ab.itemSpoof.getValue()) {
                int slot = ab.getSlot();
                if (slot >= 0) {
                    if (!slotSaved) savedSlotIndex = mc.thePlayer.inventory.currentItem;
                    slotSaved = true;
                    mc.thePlayer.inventory.currentItem = slot;
                }
            }
        }
    }

    private static void restoreSlot() {
        if (slotSaved && mc.thePlayer != null) { mc.thePlayer.inventory.currentItem = savedSlotIndex; }
        slotSaved = false;
    }

    public static void updateRendererHead() {
        if (mc.thePlayer == null || Myau.moduleManager == null) return;
        spoofSlot(true);
    }

    public static void updateRendererReturn() { restoreSlot(); }

    public static ItemStack updateTickItem(InventoryPlayer inv) {
        if (Myau.moduleManager != null) {
            Scaffold sc = (Scaffold) Myau.moduleManager.modules.get(Scaffold.class);
            if (sc != null && sc.isEnabled() && sc.itemSpoof.getValue()) {
                int slot = sc.getSlot();
                if (slot >= 0) return inv.getStackInSlot(slot);
            }
            AutoBlockIn ab = (AutoBlockIn) Myau.moduleManager.modules.get(AutoBlockIn.class);
            if (ab != null && ab.isEnabled() && ab.itemSpoof.getValue()) {
                int slot = ab.getSlot();
                if (slot >= 0) return inv.getStackInSlot(slot);
            }
        }
        return inv.getCurrentItem();
    }

    public static void ghostHand(List<Entity> list) {
        if (Myau.moduleManager == null) return;
        GhostHand gh = (GhostHand) Myau.moduleManager.modules.get(GhostHand.class);
        if (gh != null && gh.isEnabled()) {
            for (int i = list.size() - 1; i >= 0; i--) if (gh.shouldSkip(list.get(i))) list.remove(i);
        }
    }

    public static boolean isPotionActive(EntityLivingBase e, Potion potion) {
        if (Myau.moduleManager != null && (potion == Potion.confusion || potion == Potion.blindness)) {
            AntiDebuff ad = (AntiDebuff) Myau.moduleManager.modules.get(AntiDebuff.class);
            if (ad != null && ad.isEnabled()) {
                if (potion == Potion.confusion && ad.nausea.getValue()) return false;
                if (potion == Potion.blindness && ad.blindness.getValue()) return false;
            }
        }
        return e.isPotionActive(potion);
    }

    public static void renderFrameHead() {
        if (mc.thePlayer == null || Myau.moduleManager == null) return;
        spoofSlot(false);
        KillAura ka = (KillAura) Myau.moduleManager.modules.get(KillAura.class);
        if (ka != null && ka.isEnabled() && ka.isBlocking()) {
            savedUsing = Acc.getItemInUse(mc.thePlayer);
            usingSaved = true;
            Acc.setItemInUse(mc.thePlayer, mc.thePlayer.inventory.getCurrentItem());
            savedCount = Acc.getItemInUseCount(mc.thePlayer);
            countSaved = true;
            Acc.setItemInUseCount(mc.thePlayer, 69000);
        }
    }

    public static void renderFrameReturn() {
        restoreSlot();
        if (mc.thePlayer == null) return;
        if (usingSaved) { Acc.setItemInUse(mc.thePlayer, savedUsing); usingSaved = false; savedUsing = null; }
        if (countSaved) { Acc.setItemInUseCount(mc.thePlayer, savedCount); countSaved = false; }
    }

    // ================================================================== Movement hooks
    // ---- EntityPlayerSP.onLivingUpdate: replace the `this.isUsingItem()` call with Hooks.isUsingItem(this) (NoSlow) ----
    public static boolean isUsingItem(EntityPlayerSP p) {
        NoSlow ns = (NoSlow) Myau.moduleManager.modules.get(NoSlow.class);
        if (ns != null && ns.isEnabled()) {
            if (ns.isMiauAnyActive()) return !ns.shouldCancelMiauSlowdown();
            if (ns.isAnyActive()) return false;
        }
        return p.isUsingItem();
    }

    // ---- EntityPlayer.attackTargetEntityWithCurrentItem: BOTH `0.6D` constants -> Hooks.keepSprintMotion(0.6D) ----
    public static double keepSprintMotion(double speed) {
        if (Myau.moduleManager == null) return speed;
        KeepSprint ks = (KeepSprint) Myau.moduleManager.modules.get(KeepSprint.class);
        return ks != null && ks.isEnabled() && ks.shouldKeepSprint()
                ? speed + (1.0 - speed) * (1.0 - (double) ks.slowdown.getValue() / 100.0)
                : speed;
    }

    // ---- same method: replace `this.setSprinting(false)` with Hooks.attackSetSprinting(this, false) ----
    public static void attackSetSprinting(EntityPlayer p, boolean value) {
        if (Myau.moduleManager != null) {
            KeepSprint ks = (KeepSprint) Myau.moduleManager.modules.get(KeepSprint.class);
            if (ks != null && ks.isEnabled() && ks.shouldKeepSprint()) return;
        }
        p.setSprinting(value);
    }

    // ---- Entity.moveEntity: `boolean flag = this.onGround && this.isSneaking() && this instanceof EntityPlayer;` -> wrap with Hooks.safeWalk(this, ...) ----
    public static boolean safeWalk(Entity e, boolean flag) {
        if (e instanceof EntityPlayerSP) {
            SafeWalkEvent event = new SafeWalkEvent(flag);
            EventManager.call(event);
            return event.isSafeWalk();
        }
        return flag;
    }

    // ---- EntityLivingBase.moveEntityWithHeading (water branch): `float f3 = (float)EnchantmentHelper.getDepthStriderModifier(this);`
    //      becomes `float f3 = Hooks.jesusSpeed(this, (float)EnchantmentHelper.getDepthStriderModifier(this));` ----
    public static float jesusSpeed(EntityLivingBase e, float f3) {
        if (e instanceof EntityPlayerSP && f3 == (float) EnchantmentHelper.getDepthStriderModifier(e) && Myau.moduleManager != null) {
            Jesus j = (Jesus) Myau.moduleManager.modules.get(Jesus.class);
            if (j != null && j.isEnabled() && (!j.groundOnly.getValue() || e.onGround)) return Math.max(f3, j.speed.getValue());
        }
        return f3;
    }

    // ---- World.handleMaterialAcceleration: replace `entity.isPushedByWater()` with Hooks.isPushedByWater(entity) ----
    public static boolean isPushedByWater(Entity e) {
        if (e instanceof EntityPlayerSP && Myau.moduleManager != null) {
            Jesus j = (Jesus) Myau.moduleManager.modules.get(Jesus.class);
            if (j != null && j.isEnabled() && j.noPush.getValue()) return false;
        }
        return e.isPushedByWater();
    }

    // ---- AbstractClientPlayer.getFovModifier: replace `iattributeinstance.getAttributeValue()` with Hooks.fovAttribute(this, iattributeinstance) ----
    public static double fovAttribute(AbstractClientPlayer p, IAttributeInstance inst) {
        double v = inst.getAttributeValue();
        if (p instanceof EntityPlayerSP && Myau.moduleManager != null) {
            Sprint s = (Sprint) Myau.moduleManager.modules.get(Sprint.class);
            return s != null && s.isEnabled() && s.shouldApplyFovFix(inst) ? v * 1.300000011920929 : v;
        }
        return v;
    }
}
