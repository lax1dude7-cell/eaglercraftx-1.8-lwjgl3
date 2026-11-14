package myau.events;

import myau.event.events.callables.EventCancellable;
import myau.module.Kinds;
import myau.module.Module;

public class SwapItemEvent extends EventCancellable {
    private final int slot;
    private final int offset;

    public SwapItemEvent(int slot, int offset) {
        this.slot = slot;
        this.offset = Math.min(Math.max(offset, -1), 1);
    }

    public int setSlot(int integer) {
        return this.slot >= 0 && this.slot <= 8 ? this.slot : Math.floorMod(integer - this.offset, 9);
    }

    @Override
    public int getKind() {
        return Kinds.SWAP_ITEM;
    }

    @Override
    public void dispatch(Module module) {
        module.onSwapItem(this);
    }
}
