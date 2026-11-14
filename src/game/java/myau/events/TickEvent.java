package myau.events;

import myau.event.events.callables.EventCancellable;
import myau.event.types.EventType;
import myau.module.Kinds;
import myau.module.Module;

public class TickEvent extends EventCancellable {
    private final EventType type;

    public TickEvent(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return this.type;
    }

    @Override
    public int getKind() {
        return Kinds.TICK;
    }

    @Override
    public void dispatch(Module module) {
        module.onTick(this);
    }

    @Override
    public void dispatchManagers() {
        myau.Myau.rotationManager.onTick(this);
        myau.Myau.blinkManager.onTick(this);
        myau.Myau.lagManager.onTick(this);
        myau.Myau.playerStateManager.onTick(this);
    }
}