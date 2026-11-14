package myau.events;

import myau.event.events.ModuleEvent;
import myau.module.Kinds;
import myau.module.Module;

public class SafeWalkEvent extends ModuleEvent {
    private boolean safeWalk;

    public SafeWalkEvent(boolean safeWalk) {
        this.safeWalk = safeWalk;
    }

    public boolean isSafeWalk() {
        return this.safeWalk;
    }

    public void setSafeWalk(boolean safeWalk) {
        this.safeWalk = safeWalk;
    }

    @Override
    public int getKind() {
        return Kinds.SAFE_WALK;
    }

    @Override
    public void dispatch(Module module) {
        module.onSafeWalk(this);
    }
}
