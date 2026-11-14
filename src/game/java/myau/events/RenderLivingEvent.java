package myau.events;

import myau.event.events.ModuleEvent;
import myau.event.types.EventType;
import myau.module.Kinds;
import myau.module.Module;
import net.minecraft.entity.EntityLivingBase;

public class RenderLivingEvent extends ModuleEvent {
    private final EventType type;
    private final EntityLivingBase entity;

    public RenderLivingEvent(EventType type, EntityLivingBase entityLivingBase) {
        this.type = type;
        this.entity = entityLivingBase;
    }

    public EventType getType() {
        return this.type;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }

    @Override
    public int getKind() {
        return Kinds.RENDER_LIVING;
    }

    @Override
    public void dispatch(Module module) {
        module.onRenderLiving(this);
    }
}
