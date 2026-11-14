package myau.event.events;

import myau.module.Module;

public abstract class ModuleEvent implements Event {
    public abstract int getKind();

    public abstract void dispatch(Module module);

    public void dispatchManagers() {
    }
}
