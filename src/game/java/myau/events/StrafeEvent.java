package myau.events;

import myau.event.events.ModuleEvent;
import myau.module.Kinds;
import myau.module.Module;

public class StrafeEvent extends ModuleEvent {
    private float strafe;
    private float forward;
    private float friction;

    public StrafeEvent(float strafe, float forward, float friction) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public float getForward() {
        return this.forward;
    }

    public float getFriction() {
        return this.friction;
    }

    public void setStrafe(float float1) {
        this.strafe = float1;
    }

    public void setForward(float float1) {
        this.forward = float1;
    }

    public void setFriction(float float1) {
        this.friction = float1;
    }

    @Override
    public int getKind() {
        return Kinds.STRAFE;
    }

    @Override
    public void dispatch(Module module) {
        module.onStrafe(this);
    }
}
