package myau.management;

import myau.enums.FloatModules;
import myau.events.PlayerUpdateEvent;
import net.minecraft.client.Minecraft;

public class FloatManager {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final boolean[] active = new boolean[FloatModules.values().length];
    private boolean floating = false;

    public boolean isPredicted() { return this.floating; }

    public boolean isFalling() {
        return mc.thePlayer.onGround && mc.thePlayer.posY - mc.thePlayer.lastTickPosY < 0.0 && mc.thePlayer.motionY < 0.0;
    }

    public boolean hasActiveModule() {
        for (int i = 0; i < active.length; i++) if (active[i]) return true;
        return false;
    }

    public void setFloatState(boolean state, FloatModules floatModules) { active[floatModules.ordinal()] = state; }

    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if ((this.hasActiveModule() || this.isPredicted()) && this.isFalling()) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.001, mc.thePlayer.posZ);
            this.floating = true;
        } else {
            this.floating = false;
        }
    }
}
