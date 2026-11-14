package myau.events;

import myau.event.events.ModuleEvent;
import net.minecraft.entity.Entity;
import myau.module.Kinds;
import myau.module.Module;

public class AttackEvent extends ModuleEvent {
    private final Entity target;
    private boolean cancelled;

    public AttackEvent(Entity target) {
        this.target = target;
        this.cancelled = false;
    }

    public Entity getTarget() {
        return this.target;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public int getKind() {
        return Kinds.ATTACK;
    }

    @Override
    public void dispatch(Module module) {
        module.onAttack(this);
    }
}