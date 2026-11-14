package myau.events;

import myau.event.events.ModuleEvent;
import myau.module.Kinds;
import myau.module.Module;

public class PostMotionEvent extends ModuleEvent {
    //sigma
    @Override
    public int getKind() {
        return Kinds.POST_MOTION;
    }

    @Override
    public void dispatch(Module module) {
        module.onPostMotion(this);
    }
}