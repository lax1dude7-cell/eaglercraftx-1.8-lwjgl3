package myau.events;

import myau.event.events.callables.EventCancellable;
import myau.module.Kinds;
import myau.module.Module;

public class LeftClickMouseEvent extends EventCancellable {
    @Override
    public int getKind() {
        return Kinds.LEFT_CLICK;
    }

    @Override
    public void dispatch(Module module) {
        module.onLeftClick(this);
    }
}
