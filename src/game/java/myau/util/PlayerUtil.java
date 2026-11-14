package myau.util;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class PlayerUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isJumping() { return mc.currentScreen == null && KeyBindUtil.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()); }
    public static boolean isSneaking() { return mc.currentScreen == null && KeyBindUtil.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()); }
    public static boolean isMovingLeft() { return mc.currentScreen == null && KeyBindUtil.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()); }
    public static boolean isMovingRight() { return mc.currentScreen == null && KeyBindUtil.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()); }
    public static boolean isAttacking() { return mc.currentScreen == null && KeyBindUtil.isKeyDown(mc.gameSettings.keyBindAttack.getKeyCode()); }
    public static boolean isUsingItem() { return mc.currentScreen == null && KeyBindUtil.isKeyDown(mc.gameSettings.keyBindUseItem.getKeyCode()); }

    public static boolean isInWater() {
        return checkInWater(mc.thePlayer.getEntityBoundingBox().expand(-1.0E-6, 0.0, -1.0E-6));
    }

    public static boolean checkInWater(AxisAlignedBB boundingBox) {
        if (!mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
            int minY = MathHelper.floor_double(boundingBox.minY);
            if (minY < 0) return true;
            int minX = MathHelper.floor_double(boundingBox.minX);
            int maxX = MathHelper.floor_double(boundingBox.maxX + 1.0);
            int minZ = MathHelper.floor_double(boundingBox.minZ);
            int maxZ = MathHelper.floor_double(boundingBox.maxZ + 1.0);
            for (int x = minX; x < maxX; ++x) {
                for (int z = minZ; z < maxZ; ++z) {
                    for (int y = minY; y >= 0; --y) {
                        if (!BlockUtil.isReplaceable(new BlockPos(x, y, z))) return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static boolean canMove(double x, double z) { return canMove(x, z, -1.0); }

    public static boolean canMove(double x, double z, double y) {
        AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(x, y, z);
        return mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty();
    }

    public static boolean isAirAbove() {
        AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0.0, 1.0, 0.0);
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty();
    }

    public static boolean isAirBelow() {
        AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0.0, -1.0, 0.0);
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty();
    }

    public static boolean canFly(float fallThreshold) {
        if (!mc.thePlayer.capabilities.allowFlying && !mc.thePlayer.capabilities.disableDamage) {
            PotionEffect jumpEffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
            float jumpBoost = jumpEffect != null ? (float) (jumpEffect.getAmplifier() + 1) : 0.0F;
            float fallDistance = mc.thePlayer.fallDistance;
            if (mc.thePlayer.motionY < -0.67 || !isAirBelow()) {
                fallDistance -= (float) mc.thePlayer.motionY;
            }
            return MathHelper.ceiling_float_int(fallDistance - fallThreshold - jumpBoost) > 0;
        }
        return false;
    }

    /** Client-side attack without sending anything extra: vanilla attack logic (KeepSprint handled by its own hook). */
    public static void attackEntity(Entity target) {
        mc.thePlayer.attackTargetEntityWithCurrentItem(target);
    }
}
