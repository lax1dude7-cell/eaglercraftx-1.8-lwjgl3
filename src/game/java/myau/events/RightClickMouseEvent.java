package myau.events;

import myau.event.events.callables.EventCancellable;
import myau.module.Kinds;
import myau.module.Module;

public class RightClickMouseEvent extends EventCancellable {
    @Override
    public int getKind() {
        return Kinds.RIGHT_CLICK;
    }

    @Override
    public void dispatch(Module module) {
        module.onRightClick(this);
    }
}
