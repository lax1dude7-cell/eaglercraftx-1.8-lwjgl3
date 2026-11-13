package myau;

import myau.event.EventManager;
import myau.event.types.EventType;
import myau.events.*;
import myau.management.RotationState;
import myau.module.modules.HitBox;
import myau.module.modules.KillAura;
import myau.module.modules.NoHitDelay;
import myau.util.Acc;
import net.minecraft.item.ItemStack;
import myau.util.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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

    public static void renderFrameHead() {
        if (mc.thePlayer == null || Myau.moduleManager == null) return;
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
        if (mc.thePlayer == null) return;
        if (usingSaved) { Acc.setItemInUse(mc.thePlayer, savedUsing); usingSaved = false; savedUsing = null; }
        if (countSaved) { Acc.setItemInUseCount(mc.thePlayer, savedCount); countSaved = false; }
    }
}
