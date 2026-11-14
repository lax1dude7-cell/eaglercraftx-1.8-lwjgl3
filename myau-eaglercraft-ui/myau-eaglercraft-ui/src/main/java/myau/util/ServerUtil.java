package myau.util;

import net.minecraft.client.Minecraft;

public class ServerUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    /** Eaglercraft: server heuristics are IP based (the scoreboard-footer trick of the Forge client is not used). */
    public static boolean isHypixel() {
        try {
            if (mc.getCurrentServerData() == null) return false;
            String ip = mc.getCurrentServerData().serverIP;
            return ip != null && ip.toLowerCase().contains("hypixel");
        } catch (Throwable t) {
            return false;
        }
    }

    /** Hypixel-lobby scoreboard heuristic from the Forge client: not applicable on Eaglercraft, always false. */
    public static boolean hasPlayerCountInfo() { return false; }
}
