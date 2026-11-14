package myau.events;

import myau.event.events.callables.EventCancellable;
import myau.module.Kinds;
import myau.module.Module;

public class CancelUseEvent extends EventCancellable {
    @Override
    public int getKind() {
        return Kinds.CANCEL_USE;
    }

    @Override
    public void dispatch(Module module) {
        module.onCancelUse(this);
    }
}
