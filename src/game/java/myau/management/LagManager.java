package myau.management;

import myau.event.types.EventType;
import myau.events.PacketEvent;
import myau.events.TickEvent;
import myau.util.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;

import java.util.ArrayList;

public class LagManager {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public final ArrayList<LagPacket> packetQueue = new ArrayList<LagPacket>();
    private int tickDelay = 0;
    private boolean flushing = false;
    private Vec3 lastPosition = new Vec3(0.0, 0.0, 0.0);

    private void flushQueue() {
        if (mc.getNetHandler() == null) {
            packetQueue.clear();
            return;
        }
        flushing = true;
        while (!packetQueue.isEmpty()) {
            LagPacket lp = packetQueue.get(0);
            if (tickDelay > 0 && lp.delay <= tickDelay) break;
            PacketUtil.sendPacketNoEvent(lp.packet);
            if (lp.packet instanceof C03PacketPlayer) {
                C03PacketPlayer c03 = (C03PacketPlayer) lp.packet;
                if (c03.isMoving()) lastPosition = new Vec3(c03.getPositionX(), c03.getPositionY(), c03.getPositionZ());
            }
            packetQueue.remove(0);
        }
        flushing = false;
    }

    private void incrementDelays() {
        for (int i = 0; i < packetQueue.size(); i++) packetQueue.get(i).delay++;
    }

    /** @return true if the packet was queued (caller must cancel the send). */
    public boolean handlePacket(Packet<?> packet) {
        flushQueue();
        if (packet instanceof C00PacketKeepAlive || packet instanceof C01PacketChatMessage) return false;
        if (tickDelay > 0) {
            packetQueue.add(new LagPacket(packet));
            return true;
        }
        if (packet instanceof C03PacketPlayer) {
            C03PacketPlayer c03 = (C03PacketPlayer) packet;
            if (c03.isMoving()) lastPosition = new Vec3(c03.getPositionX(), c03.getPositionY(), c03.getPositionZ());
        }
        return false;
    }

    public void setDelay(int delay) { tickDelay = delay; }
    public Vec3 getLastPosition() { return lastPosition; }
    public boolean isFlushing() { return flushing; }

    public void onTick(TickEvent event) {
        if (event.getType() == EventType.POST) {
            if (mc.thePlayer != null && mc.thePlayer.isDead) setDelay(0);
            incrementDelays();
            flushQueue();
        }
    }

    public void onPacket(PacketEvent event) { }

    public static class LagPacket {
        public final Packet<?> packet;
        public int delay;
        public LagPacket(Packet<?> packet) { this.packet = packet; this.delay = 0; }
    }
}
