package myau.module.modules;

import myau.util.Acc;
import myau.event.types.EventType;
import myau.events.UpdateEvent;
import myau.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class AntiAFK extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private int lastInput;

    public AntiAFK() {
        super("AntiAFK", false);
    
    }

    @Override
    public void onUpdate(UpdateEvent event){
        if(event.getType() == EventType.PRE && this.isEnabled()){
            GameSettings gameSettings = mc.gameSettings;
            if (gameSettings.keyBindJump.isPressed() || gameSettings.keyBindRight.isPressed() || gameSettings.keyBindForward.isPressed() || gameSettings.keyBindLeft.isPressed() || gameSettings.keyBindBack.isPressed()) {
                lastInput = 0;
            }
            lastInput++;
            if (lastInput < 20 * 10) return;
            if (mc.thePlayer.ticksExisted % 5 == 0) {
                Acc.setPressed(mc.gameSettings.keyBindRight, false);
                Acc.setPressed(mc.gameSettings.keyBindLeft, false);
                Acc.setPressed(mc.gameSettings.keyBindJump, false);
            }
            if (mc.thePlayer.ticksExisted % 20 == 0) {
                if (mc.thePlayer.ticksExisted % 40 == 0) {
                    Acc.setPressed(mc.gameSettings.keyBindRight, true);
                } else {
                    Acc.setPressed(mc.gameSettings.keyBindLeft, true);
                }
            }
            if (mc.thePlayer.ticksExisted % 100 == 0) {
                Acc.setPressed(mc.gameSettings.keyBindJump, true);
            }
        }
    }
}
