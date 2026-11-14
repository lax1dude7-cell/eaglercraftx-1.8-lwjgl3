package myau.events;

import myau.event.events.ModuleEvent;
import myau.module.Kinds;
import myau.module.Module;

public class MoveInputEvent extends ModuleEvent {
    @Override
    public int getKind() {
        return Kinds.MOVE_INPUT;
    }

    @Override
    public void dispatch(Module module) {
        module.onMoveInput(this);
    }
}
