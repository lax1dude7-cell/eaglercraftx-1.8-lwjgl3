package myau.management;

import myau.event.types.EventType;
import myau.events.TickEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

public class PlayerStateManager {
    private long lastCombatActionTime = 0;
    private static final long COMBAT_TIMEOUT = 5000;
    private boolean inCombat = false;
    public boolean attacking = false;
    public boolean digging = false;
    public boolean placing = false;
    public boolean swapping = false;
    public boolean swinging = false;

    public void handlePacket(Packet<?> packet) {
        if (packet instanceof C02PacketUseEntity) { attacking = true; updateCombatState(); }
        if (packet instanceof C07PacketPlayerDigging) digging = true;
        if (packet instanceof C08PacketPlayerBlockPlacement) placing = true;
        if (packet instanceof C09PacketHeldItemChange) swapping = true;
        if (packet instanceof C0APacketAnimation) { swinging = true; updateCombatState(); }
        if (packet instanceof C03PacketPlayer) {
            attacking = false; digging = false; placing = false; swapping = false; swinging = false;
        }
    }

    private void updateCombatState() { lastCombatActionTime = System.currentTimeMillis(); inCombat = true; }
    public boolean isInCombat() { return inCombat; }

    public void onTick(TickEvent event) {
        if (event.getType() == EventType.POST && inCombat && System.currentTimeMillis() - lastCombatActionTime > COMBAT_TIMEOUT) {
            inCombat = false;
        }
    }
}
