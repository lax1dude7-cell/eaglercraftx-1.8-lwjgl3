package myau.management;

import myau.enums.BlinkModules;
import myau.event.types.EventType;
import myau.events.PacketEvent;
import myau.events.TickEvent;
import myau.util.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import java.util.ArrayList;

public class BlinkManager {
    public BlinkModules blinkModule = BlinkModules.NONE;
    public boolean blinking = false;
    public ArrayList<Packet<?>> blinkedPackets = new ArrayList<Packet<?>>();

    public boolean offerPacket(Packet<?> packet) {
        if (blinkModule == BlinkModules.NONE || packet instanceof C00PacketKeepAlive || packet instanceof C01PacketChatMessage) return false;
        if (blinkedPackets.isEmpty() && packet instanceof C0FPacketConfirmTransaction) return false;
        blinkedPackets.add(packet);
        return true;
    }

    public boolean setBlinkState(boolean state, BlinkModules module) {
        if (module == BlinkModules.NONE) return false;
        if (state) {
            blinkModule = module;
            blinking = true;
        } else {
            if (blinkModule != module) return false;
            blinking = false;
            if (Minecraft.getMinecraft().getNetHandler() != null && blinkedPackets.isEmpty()) return true;
            for (int i = 0; i < blinkedPackets.size(); i++) PacketUtil.sendPacketNoEvent(blinkedPackets.get(i));
            blinkedPackets.clear();
            blinkModule = BlinkModules.NONE;
        }
        return true;
    }

    public BlinkModules getBlinkingModule() { return blinkModule; }

    public long countMovement() {
        long n = 0;
        for (int i = 0; i < blinkedPackets.size(); i++) if (blinkedPackets.get(i) instanceof C03PacketPlayer) n++;
        return n;
    }

    public boolean isBlinking() { return blinking; }

    public void onPacket(PacketEvent event) {
        // handshake/login packets: nothing to do on a single-protocol Eaglercraft connection, kept for parity
    }

    public void onTick(TickEvent event) {
        if (event.getType() == EventType.POST && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.isDead) {
            setBlinkState(false, blinkModule);
        }
    }
}
