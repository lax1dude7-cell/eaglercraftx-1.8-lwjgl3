package myau.module.modules;

import myau.font.FontManager;
import myau.font.UIFont;
import myau.module.Category;
import myau.module.Module;
import myau.property.properties.BooleanProperty;
import myau.property.properties.ColorProperty;
import myau.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;

/** Top-centre pill: "Myau+ · name · 0ms to SinglePlayer · 43fps". (Blur/glow shaders omitted: not portable to WebGL.) */
public class DynamicIsland extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public final ColorProperty accent = new ColorProperty("AccentColor", 0xE0C828);
    public final BooleanProperty textShadow = new BooleanProperty("TextShadow", true);

    public DynamicIsland() {
        super("DynamicIsland", Category.RENDER, true, false);
        add(accent); add(textShadow);
    }

    @Override
    public void onRender2D(float pt, ScaledResolution sr) {
        if (mc.thePlayer == null) return;
        String user = mc.thePlayer.getName();
        String ping = getPing() + "ms";
        String server = getServer();
        String fps = Minecraft.getDebugFPS() + "fps";
        String sep = "  \u00B7  ";
        String p1 = "Myau+", p2 = sep + user + sep, p3 = ping, p4 = " to " + server + sep + fps;

        UIFont f = FontManager.main(9f);
        float tw = f.getStringWidth(p1 + p2 + p3 + p4);
        float w = tw + 24f, h = 26f, x = sr.getScaledWidth() / 2f - w / 2f, y = 8f;

        RenderUtil.drawRoundedRect(x, y + 2f, w, h, 8f, 0x28000000);                    // soft drop shadow
        RenderUtil.drawRoundedRect(x, y, w, h, 8f, 0x82000000);                          // body (alpha 130)
        RenderUtil.drawRoundedRect(x + 0.5f, y + 0.5f, w - 1f, h - 1f, 7.5f, 0x12FFFFFF); // inner highlight

        int ac = 0xFF000000 | accent.getValue();
        float cx = x + 12f, ty = y + (h - f.getHeight()) / 2f;
        cx += draw(f, p1, cx, ty, ac);
        cx += draw(f, p2, cx, ty, 0xFFFFFFFF);
        cx += draw(f, p3, cx, ty, ac);
        draw(f, p4, cx, ty, 0xFFFFFFFF);
    }

    private float draw(UIFont f, String s, float x, float y, int c) {
        return textShadow.getValue() ? f.drawStringWithShadow(s, x, y, c) : f.drawString(s, x, y, c);
    }

    private int getPing() {
        try {
            NetworkPlayerInfo i = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getName());
            if (i != null) return i.getResponseTime();
        } catch (Throwable t) { }
        return 0;
    }

    private String getServer() {
        try {
            if (mc.isIntegratedServerRunning()) return "SinglePlayer";
            if (mc.getCurrentServerData() != null) return mc.getCurrentServerData().serverIP;
        } catch (Throwable t) { }
        return "SinglePlayer";
    }
}
