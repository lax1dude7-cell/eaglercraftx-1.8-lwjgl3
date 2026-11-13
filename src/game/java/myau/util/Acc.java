package myau.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.Timer;

/**
 * Replacement for the OpenMyau+ Mixin accessors (IAccessorXxx). Eaglercraft ships the full vanilla source, so
 * instead of mixins we make the private engine fields PUBLIC (see ENGINE_HOOKS.md, section "Make public") and
 * read them here. The ported modules call Acc.method(target, ...) wherever the original called ((IAccessorX) target).method(...).
 */
public final class Acc {
    private Acc() {}

    // Minecraft
    public static Timer getTimer(Minecraft mc) { return mc.timer; }
    public static int getRightClickDelayTimer(Minecraft mc) { return mc.rightClickDelayTimer; }
    public static void setRightClickDelayTimer(Minecraft mc, int v) { mc.rightClickDelayTimer = v; }

    // KeyBinding
    public static void setPressed(KeyBinding kb, boolean v) { kb.pressed = v; }

    // Entity / EntityLivingBase / EntityPlayer
    public static boolean getIsInWeb(Entity e) { return e.isInWeb; }
    public static int getJumpTicks(EntityLivingBase e) { return e.jumpTicks; }
    public static ItemStack getItemInUse(EntityPlayer p) { return p.itemInUse; }
    public static void setItemInUse(EntityPlayer p, ItemStack s) { p.itemInUse = s; }
    public static int getItemInUseCount(EntityPlayer p) { return p.itemInUseCount; }
    public static void setItemInUseCount(EntityPlayer p, int v) { p.itemInUseCount = v; }

    // PlayerControllerMP
    public static void callSyncCurrentPlayItem(PlayerControllerMP c) { c.syncCurrentPlayItem(); }
    public static int getBlockHitDelay(PlayerControllerMP c) { return c.blockHitDelay; }
    public static float getCurBlockDamageMP(PlayerControllerMP c) { return c.curBlockDamageMP; }
    public static void setCurBlockDamageMP(PlayerControllerMP c, float v) { c.curBlockDamageMP = v; }
    public static int getCurrentPlayerItem(PlayerControllerMP c) { return c.currentPlayerItem; }
    public static void setCurrentPlayerItem(PlayerControllerMP c, int v) { c.currentPlayerItem = v; }
    public static boolean getIsHittingBlock(PlayerControllerMP c) { return c.isHittingBlock; }

    // EntityRenderer
    public static void callSetupCameraTransform(EntityRenderer r, float partialTicks, int pass) { r.setupCameraTransform(partialTicks, pass); }

    // Packets
    public static void setOnGround(C03PacketPlayer p, boolean v) { p.onGround = v; }
    public static int getWindowId(C0DPacketCloseWindow p) { return p.windowId; }
    public static void setData(C17PacketCustomPayload p, PacketBuffer b) { p.data = b; }
    public static void setMotionX(S12PacketEntityVelocity p, int v) { p.motionX = v; }
    public static void setMotionY(S12PacketEntityVelocity p, int v) { p.motionY = v; }
    public static void setMotionZ(S12PacketEntityVelocity p, int v) { p.motionZ = v; }
}
