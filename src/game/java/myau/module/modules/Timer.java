package myau.module.modules;

import myau.util.TextUtil;
import myau.util.Acc;
import myau.event.types.EventType;
import myau.event.types.Priority;
import myau.events.UpdateEvent;
import myau.module.Module;
import myau.property.properties.FloatProperty;
import net.minecraft.client.Minecraft;

public class Timer extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public final FloatProperty speed = new FloatProperty("speed", 1.0F, 0.01F, 10.0F);

    public Timer() {
        super("Timer", false);
    
        add(speed);
        setPriority(myau.module.Kinds.UPDATE, 0);
    }

    @Override
    public void onDisabled() {
        net.minecraft.util.Timer timer = Acc.getTimer(mc);
        if (timer != null) {
            timer.timerSpeed = 1.0F;
        }
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (!this.isEnabled() || event.getType() != EventType.PRE) {
            return;
        }

        net.minecraft.util.Timer timer = Acc.getTimer(mc);
        if (timer != null) {
            timer.timerSpeed = this.speed.getValue();
        }
    }

    @Override
    public String[] getSuffix() {
        return new String[]{TextUtil.fmt(this.speed.getValue(), 1, 1) + "x"};
    }
}