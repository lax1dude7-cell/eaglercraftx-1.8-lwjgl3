package myau.events;

import myau.event.events.ModuleEvent;
import myau.module.Kinds;
import myau.module.Module;

public class LoadWorldEvent extends ModuleEvent {
    @Override
    public int getKind() {
        return Kinds.LOAD_WORLD;
    }

    @Override
    public void dispatch(Module module) {
        module.onLoadWorld(this);
    }
}
