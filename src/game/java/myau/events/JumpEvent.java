package myau.events;

import myau.event.events.callables.EventCancellable;
import myau.module.Kinds;
import myau.module.Module;

public class JumpEvent extends EventCancellable {
    @Override
    public int getKind() {
        return Kinds.JUMP;
    }

    @Override
    public void dispatch(Module module) {
        module.onJump(this);
    }
}