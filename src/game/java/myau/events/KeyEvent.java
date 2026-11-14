package myau.events;

import myau.event.events.ModuleEvent;
import myau.module.Kinds;
import myau.module.Module;

public class KeyEvent extends ModuleEvent {
    private final int keyCode;

    public KeyEvent(int key) {
        this.keyCode = key;
    }

    public int getKey() {
        return this.keyCode;
    }

    @Override
    public int getKind() {
        return Kinds.KEY;
    }

    @Override
    public void dispatch(Module module) {
        module.onKey(this);
    }
}
