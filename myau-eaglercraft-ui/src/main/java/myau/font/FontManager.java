package myau.font;

import myau.font.ttf.TTFFont;
import myau.util.Compat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom font manager. Register any number of .ttf files, then ask for them at any size:
 *   FontManager.register("main", new ResourceLocation("minecraft", "myau/fonts/main.ttf"));
 *   UIFont f = FontManager.get("main", 9f);
 */
public final class FontManager {
    public static final String MAIN = "main";
    private static int guiScale = 2;
    private static final Map<String, TTFFont> fonts = new HashMap<String, TTFFont>();
    private static final Map<String, Map<Integer, UIFont>> cache = new HashMap<String, Map<Integer, UIFont>>();
    private static final UIFont VANILLA = new VanillaFont();

    private FontManager() {}

    public static boolean register(String name, ResourceLocation loc) {
        byte[] data = Compat.readResource(loc);
        if (data == null) {
            System.err.println("[Myau] Font not found: " + loc + " (falling back to vanilla font for '" + name + "')");
            return false;
        }
        try {
            fonts.put(name, TTFFont.parse(data));
            cache.remove(name);
            return true;
        } catch (Throwable t) {
            System.err.println("[Myau] Failed to parse font " + loc + ": " + t);
            return false;
        }
    }

    public static UIFont get(String name, float size) {
        Map<Integer, UIFont> m = cache.get(name);
        if (m == null) { m = new HashMap<Integer, UIFont>(); cache.put(name, m); }
        Integer key = Integer.valueOf(Math.round(size * 100f));
        UIFont f = m.get(key);
        if (f == null) {
            TTFFont t = fonts.get(name);
            f = t != null ? new TTFFontRenderer(t, size) : VANILLA;
            m.put(key, f);
        }
        return f;
    }

    public static UIFont main(float size) { return get(MAIN, size); }

    public static void updateScale(ScaledResolution sr) { guiScale = sr.getScaleFactor(); }

    public static int getGuiScale() { return guiScale; }
}
