package myau.events;

import myau.event.events.ModuleEvent;
import myau.module.Kinds;
import myau.module.Module;

public class PlayerUpdateEvent extends ModuleEvent {
    @Override
    public int getKind() {
        return Kinds.PLAYER_UPDATE;
    }

    @Override
    public void dispatch(Module module) {
        module.onPlayerUpdate(this);
    }

    @Override
    public void dispatchManagers() {
        myau.Myau.floatManager.onPlayerUpdate(this);
    }
}
