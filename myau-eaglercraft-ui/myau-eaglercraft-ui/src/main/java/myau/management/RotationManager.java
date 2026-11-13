package myau.management;

import myau.event.types.EventType;
import myau.events.Render3DEvent;
import myau.events.TickEvent;
import myau.util.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class RotationManager {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private float lastUpdate = Float.NaN;
    private float yawDelta = Float.NaN;
    private float pitchDelta = Float.NaN;
    private int priority = Integer.MIN_VALUE;
    private boolean rotated = false;

    private void applyRotation(float partialTicks) {
        if (mc.thePlayer != null && !Float.isNaN(yawDelta) && !Float.isNaN(pitchDelta) && !Float.isNaN(lastUpdate)) {
            float yaw = yawDelta * (partialTicks - lastUpdate);
            if (yaw != 0.0F) {
                mc.thePlayer.prevRotationYaw = mc.thePlayer.rotationYaw;
                mc.thePlayer.rotationYaw += yaw;
            }
            float pitch = pitchDelta * (partialTicks - lastUpdate);
            if (pitch != 0.0F) {
                mc.thePlayer.prevRotationPitch = mc.thePlayer.rotationPitch;
                mc.thePlayer.rotationPitch += pitch;
                mc.thePlayer.rotationPitch = MathHelper.clamp_float(mc.thePlayer.rotationPitch, -90.0F, 90.0F);
            }
            lastUpdate = partialTicks;
        }
    }

    private void resetRotationState() {
        lastUpdate = Float.NaN; yawDelta = Float.NaN; pitchDelta = Float.NaN;
        priority = Integer.MIN_VALUE; rotated = false;
    }

    public void setRotation(float yaw, float pitch, int priority, boolean force) {
        if (this.priority <= priority) {
            this.priority = priority;
            float[] patched = RotationUtil.gcd(yaw, pitch, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            yawDelta = MathHelper.wrapAngleTo180_float(patched[0] - mc.thePlayer.rotationYaw);
            pitchDelta = MathHelper.clamp_float(patched[1] - mc.thePlayer.rotationPitch, -90.0F, 90.0F);
            lastUpdate = 0.0F;
            rotated = force;
            applyRotation(0.0F);
        }
    }

    public boolean isRotated() { return rotated; }

    public void onTick(TickEvent event) {
        if (event.getType() != EventType.PRE) return;
        applyRotation(1.0F);
        resetRotationState();
    }

    public void onRender3D(Render3DEvent event) { applyRotation(event.getPartialTicks()); }
}
