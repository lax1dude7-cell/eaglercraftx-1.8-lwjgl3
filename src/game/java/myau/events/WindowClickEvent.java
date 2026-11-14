package myau.events;

import myau.event.events.callables.EventCancellable;
import myau.module.Kinds;
import myau.module.Module;

public class WindowClickEvent extends EventCancellable {
    private final int windowsId;
    private final int slotId;
    private final int mouseButtonClicked;
    private final int mode;

    public WindowClickEvent(int windowsId, int slotId, int mouseButtonClicked, int mode) {
        this.windowsId = windowsId;
        this.slotId = slotId;
        this.mouseButtonClicked = mouseButtonClicked;
        this.mode = mode;
    }

    @Override
    public int getKind() {
        return Kinds.WINDOW_CLICK;
    }

    @Override
    public void dispatch(Module module) {
        module.onWindowClick(this);
    }
}
