package myau.events;

import myau.event.events.ModuleEvent;
import myau.module.Kinds;
import myau.module.Module;

public class RaytraceEvent extends ModuleEvent {
    private double range;

    public RaytraceEvent(double range) {
        this.range = range;
    }

    public double getRange() {
        return this.range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    @Override
    public int getKind() {
        return Kinds.RAYTRACE;
    }

    @Override
    public void dispatch(Module module) {
        module.onRaytrace(this);
    }
}
