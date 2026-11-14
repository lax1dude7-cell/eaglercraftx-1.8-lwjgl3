package myau.module.modules;

import myau.util.Acc;
import myau.event.types.EventType;
import myau.event.types.Priority;
import myau.events.TickEvent;
import myau.module.Module;
import myau.property.properties.IntProperty;
import net.minecraft.client.Minecraft;

public class NoJumpDelay extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public final IntProperty delay = new IntProperty("delay", 3, 0, 8);

    public NoJumpDelay() {
        super("NoJumpDelay", false);
    
        add(delay);
        setPriority(myau.module.Kinds.TICK, 0);
    }

    @Override
    public void onTick(TickEvent event) {
        if (this.isEnabled() && event.getType() == EventType.PRE) {
            Acc.setJumpTicks(mc.thePlayer, Math.min(Acc.getJumpTicks(mc.thePlayer), this.delay.getValue() + 1));
        }
    }

    @Override
    public String[] getSuffix() {
        return new String[]{String.valueOf(this.delay.getValue())};
    }
}
