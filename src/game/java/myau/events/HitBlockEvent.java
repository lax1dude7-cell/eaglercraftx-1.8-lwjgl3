package myau.events;

import myau.event.events.callables.EventCancellable;
import myau.module.Kinds;
import myau.module.Module;

public class HitBlockEvent extends EventCancellable {
    @Override
    public int getKind() {
        return Kinds.HIT_BLOCK;
    }

    @Override
    public void dispatch(Module module) {
        module.onHitBlock(this);
    }
}
