package myau.module.modules;

import myau.util.TextUtil;
import myau.event.types.EventType;
import myau.events.PickEvent;
import myau.events.RaytraceEvent;
import myau.events.TickEvent;
import myau.module.Module;
import myau.property.properties.FloatProperty;
import myau.property.properties.PercentProperty;

import java.util.Locale;
import java.util.Random;

public class Reach extends Module {
        private final Random theRandom = new Random();
    private boolean expanding = true;
    public final FloatProperty range = new FloatProperty("range", 3.1F, 3.0F, 6.0F);
    public final PercentProperty chance = new PercentProperty("chance", 100);

    public Reach() {
        super("Reach", false, false, "Reach higher using nano technologo make ur dick bigger to hit further");
    
        add(range);
        add(chance);
    }

    @Override
    public void onPick(PickEvent event) {
        if (this.isEnabled() && this.expanding) {
            event.setRange(((double) this.range.getValue()));
        }
    }

    @Override
    public void onRaytrace(RaytraceEvent event) {
        if (this.isEnabled() && this.expanding) {
            event.setRange(Math.max(event.getRange(), ((double) this.range.getValue()) + 0.5));
        }
    }

    @Override
    public void onTick(TickEvent event) {
        if (this.isEnabled() && event.getType() == EventType.PRE) {
            this.expanding = this.theRandom.nextDouble() <= (double) this.chance.getValue() / 100.0;
        }
    }

    @Override
    public String[] getSuffix() {
        return new String[]{TextUtil.fmt(this.range.getValue(), 1, 2)};
    }
}
