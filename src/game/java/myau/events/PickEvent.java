package myau.events;

import myau.event.events.ModuleEvent;
import myau.module.Kinds;
import myau.module.Module;

public class PickEvent extends ModuleEvent {
    private double range;

    public PickEvent(double double1) {
        this.range = double1;
    }

    public double getRange() {
        return this.range;
    }

    public void setRange(double double1) {
        this.range = double1;
    }

    @Override
    public int getKind() {
        return Kinds.PICK;
    }

    @Override
    public void dispatch(Module module) {
        module.onPick(this);
    }
}
