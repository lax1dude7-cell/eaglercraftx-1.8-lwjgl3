package myau.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;

public class PacketUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    /** While true, the send hook must NOT fire PacketEvent / blink / lag handling (replacement for sendPacket(packet, null)). */
    public static boolean suppressSendEvents = false;

    public static boolean isWorldRenderPacket(Packet<?> packet) {
        return packet instanceof S21PacketChunkData
                || packet instanceof S22PacketMultiBlockChange
                || packet instanceof S23PacketBlockChange
                || packet instanceof S24PacketBlockAction
                || packet instanceof S25PacketBlockBreakAnim
                || packet instanceof S26PacketMapChunkBulk
                || packet instanceof S28PacketEffect;
    }

    public static void sendPacket(Packet<?> packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }

    public static void sendPacketNoEvent(Packet<?> packet) {
        boolean old = suppressSendEvents;
        suppressSendEvents = true;
        try {
            mc.getNetHandler().getNetworkManager().sendPacket(packet);
        } finally {
            suppressSendEvents = old;
        }
    }
}
