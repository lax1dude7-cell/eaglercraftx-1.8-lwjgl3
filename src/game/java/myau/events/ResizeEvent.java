package myau.events;

import myau.event.events.ModuleEvent;
import myau.module.Kinds;
import myau.module.Module;

public class ResizeEvent extends ModuleEvent {
    @Override
    public int getKind() {
        return Kinds.RESIZE;
    }

    @Override
    public void dispatch(Module module) {
        module.onResize(this);
    }
}
