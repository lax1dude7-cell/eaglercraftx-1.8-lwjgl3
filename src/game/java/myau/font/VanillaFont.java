package myau.font;

import net.minecraft.client.Minecraft;

/** Fallback used when the TTF could not be loaded, so the GUI never crashes. */
public final class VanillaFont implements UIFont {
    public float drawString(String s, float x, float y, int color) {
        Minecraft.getMinecraft().fontRendererObj.drawString(s, x, y, color, false);
        return getStringWidth(s);
    }
    public float drawStringWithShadow(String s, float x, float y, int color) {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(s, x, y, color);
        return getStringWidth(s);
    }
    public float getStringWidth(String s) { return Minecraft.getMinecraft().fontRendererObj.getStringWidth(s); }
    public float getHeight() { return 7f; }
    public float getLineHeight() { return 9f; }
}
