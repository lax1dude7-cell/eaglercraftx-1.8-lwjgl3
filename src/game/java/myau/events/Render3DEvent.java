package myau.events;

import myau.event.events.ModuleEvent;
import myau.module.Kinds;
import myau.module.Module;

public class Render3DEvent extends ModuleEvent {
    private final float partialTicks;

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    @Override
    public int getKind() {
        return Kinds.RENDER_3D;
    }

    @Override
    public void dispatch(Module module) {
        module.onRender3D(this);
    }

    @Override
    public void dispatchManagers() {
        myau.Myau.rotationManager.onRender3D(this);
    }
}
