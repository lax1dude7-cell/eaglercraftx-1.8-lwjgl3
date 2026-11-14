package myau.events;

import myau.event.events.callables.EventCancellable;
import myau.event.types.EventType;
import myau.module.Kinds;
import myau.module.Module;
import net.minecraft.network.Packet;

public class PacketEvent extends EventCancellable {
    private final EventType type;
    private final Packet<?> packet;

    public PacketEvent(EventType type, Packet<?> packet) {
        this.type = type;
        this.packet = packet;
    }

    public EventType getType() {
        return this.type;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    @Override
    public int getKind() {
        return Kinds.PACKET;
    }

    @Override
    public void dispatch(Module module) {
        module.onPacket(this);
    }

    @Override
    public void dispatchManagers() {
        myau.Myau.blinkManager.onPacket(this);
        myau.Myau.lagManager.onPacket(this);
    }
}
