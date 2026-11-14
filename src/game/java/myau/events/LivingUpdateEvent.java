package myau.events;

import myau.event.events.ModuleEvent;
import myau.module.Kinds;
import myau.module.Module;

public class LivingUpdateEvent extends ModuleEvent {
    @Override
    public int getKind() {
        return Kinds.LIVING_UPDATE;
    }

    @Override
    public void dispatch(Module module) {
        module.onLivingUpdate(this);
    }
}
