package myau.module.modules;

import myau.Myau;
import myau.font.FontManager;
import myau.font.UIFont;
import myau.module.Category;
import myau.module.Module;
import myau.property.properties.*;
import myau.util.ColorUtil;
import myau.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Arraylist + info line + player head. Same properties / colour maths as OpenMyau+'s HUD. */
public class HUD extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public final ModeProperty colorMode = add(new ModeProperty("color", 0, new String[]{"RAINBOW", "CHROMA", "ASTOLFO", "CUSTOM1", "CUSTOM12", "CUSTOM123"}));
    public final FloatProperty colorSpeed = add(new FloatProperty("color-speed", 1.0F, 0.5F, 1.5F));
    public final PercentProperty colorSaturation = add(new PercentProperty("color-saturation", 50));
    public final PercentProperty colorBrightness = add(new PercentProperty("color-brightness", 100));
    public final ColorProperty custom1 = add(new ColorProperty("custom-color-1", 0xFFFFFF, () -> colorMode.getValue() >= 3));
    public final ColorProperty custom2 = add(new ColorProperty("custom-color-2", 0xFFFFFF, () -> colorMode.getValue() >= 4));
    public final ColorProperty custom3 = add(new ColorProperty("custom-color-3", 0xFFFFFF, () -> colorMode.getValue() == 5));
    public final ModeProperty posX = add(new ModeProperty("position-x", 0, new String[]{"LEFT", "RIGHT"}));
    public final ModeProperty posY = add(new ModeProperty("position-y", 0, new String[]{"TOP", "BOTTOM"}));
    public final IntProperty offsetX = add(new IntProperty("offset-x", 0, 0, 255));
    public final IntProperty offsetY = add(new IntProperty("offset-y", 25, 0, 255));
    public final FloatProperty scale = add(new FloatProperty("scale", 1.0F, 0.5F, 1.5F));
    public final PercentProperty background = add(new PercentProperty("background", 0));
    public final BooleanProperty showBar = add(new BooleanProperty("bar", true));
    public final ModeProperty sidebarMode = add(new ModeProperty("sidebar-mode", 1, new String[]{"RIGHT", "LEFT", "TOP", "NONE"}, () -> showBar.getValue()));
    public final BooleanProperty shadow = add(new BooleanProperty("shadow", true));
    public final BooleanProperty suffixes = add(new BooleanProperty("suffixes", true));
    public final ModeProperty separatorMode = add(new ModeProperty("separator-mode", 0, new String[]{"SPACE", "-", "!", "[]", "{}", "()", "\"\""}, () -> suffixes.getValue()));
    public final BooleanProperty lowerCase = add(new BooleanProperty("lower-case", true));
    public final FloatProperty padding = add(new FloatProperty("padding", 0.0F, 0.0F, 6.0F));
    public final BooleanProperty hideRender = add(new BooleanProperty("hide-render", false));
    public final BooleanProperty hidePlayer = add(new BooleanProperty("hide-player", false));
    public final BooleanProperty hideMisc = add(new BooleanProperty("hide-misc", false));
    public final BooleanProperty infoLine = add(new BooleanProperty("info-line", true));
    public final BooleanProperty playerHead = add(new BooleanProperty("player-head", true));

    public HUD() {
        super("HUD", Category.RENDER, true, true);
    }

    // ---------- colour (ported from OpenMyau+) ----------
    private float colorCycle(long time, long offset) {
        long speed = (long) (3000.0 / Math.pow(Math.min(Math.max(0.5F, colorSpeed.getValue()), 1.5F), 3.0));
        return 1.0F - (float) (Math.abs(time - offset * 300L) % speed) / (float) speed;
    }

    public int getColor(long time) { return getColor(time, 0L); }

    public int getColor(long time, long offset) {
        float hue = 0f;
        switch (colorMode.getValue()) {
            case 0: hue = colorCycle(time, offset); break;
            case 1: hue = colorCycle(time / 3L, 0L); break;
            case 2: {
                float c = colorCycle(time, offset);
                if (c % 1.0F < 0.5F) c = 1.0F - c % 1.0F;
                hue = c;
                break;
            }
            case 3: return satBri(custom1.getValue());
            case 4: {
                double c = colorCycle(time, offset);
                float f = (float) (2.0 * Math.abs(c - Math.floor(c + 0.5)));
                return satBri(ColorUtil.interpolate(f, custom1.getValue(), custom2.getValue()));
            }
            case 5: {
                double c = colorCycle(time, offset);
                float f = (float) (2.0 * Math.abs(c - Math.floor(c + 0.5)));
                int col = f <= 0.5F ? ColorUtil.interpolate(f * 2f, custom1.getValue(), custom2.getValue())
                        : ColorUtil.interpolate((f - 0.5F) * 2f, custom2.getValue(), custom3.getValue());
                return satBri(col);
            }
        }
        return ColorUtil.hsbToRGB(hue, colorSaturation.getValue() / 100f, colorBrightness.getValue() / 100f);
    }

    private int satBri(int color) {
        float[] hsb = ColorUtil.rgbToHSB(color);
        return ColorUtil.hsbToRGB(hsb[0], hsb[1] * colorSaturation.getValue() / 100f, hsb[2] * colorBrightness.getValue() / 100f);
    }

    // ---------- arraylist ----------
    private float hudScale() { return Math.max(0.5F, Math.min(1.5F, scale.getValue())); }

    private String nameOf(Module m) {
        return lowerCase.getValue() ? m.getName().toLowerCase(Locale.ROOT) : m.getName();
    }

    private String fmtSuffix(String s) {
        switch (separatorMode.getValue()) {
            case 1: return " - " + s;
            case 2: return " ! " + s;
            case 3: return " [" + s + "]";
            case 4: return " {" + s + "}";
            case 5: return " (" + s + ")";
            case 6: return " \"" + s + "\"";
            default: return " " + s;
        }
    }

    private float widthOf(Module m, UIFont f) {
        float w = f.getStringWidth(nameOf(m));
        if (suffixes.getValue()) for (String s : m.getSuffix()) w += f.getStringWidth(fmtSuffix(lowerCase.getValue() ? s.toLowerCase(Locale.ROOT) : s));
        return w;
    }

    private boolean hiddenByCategory(Module m) {
        Category c = m.getCategory();
        return (hideRender.getValue() && c == Category.RENDER) || (hidePlayer.getValue() && c == Category.PLAYER) || (hideMisc.getValue() && c == Category.MISC);
    }

    @Override
    public void onRender2D(float pt, ScaledResolution sr) {
        if (mc.gameSettings.showDebugInfo) return;
        long now = System.currentTimeMillis();
        float hs = hudScale();
        UIFont f = FontManager.main(9f * hs);

        if (infoLine.getValue()) drawInfoLine(now);
        if (playerHead.getValue() && mc.thePlayer != null) {
            RenderUtil.drawHead(mc.thePlayer.getLocationSkin(), 1, 12, 9);
            FontManager.main(9f).drawStringWithShadow(mc.thePlayer.getName(), 13, 13.5f, getColor(now, 3));
        }

        final UIFont ff = f;
        List<Module> active = new ArrayList<Module>();
        for (Module m : Myau.moduleManager.all()) if (m.isEnabled() && !m.isHidden() && !hiddenByCategory(m)) active.add(m);
        Collections.sort(active, new Comparator<Module>() {
            public int compare(Module a, Module b) { return Float.compare(widthOf(b, ff), widthOf(a, ff)); }
        });

        boolean right = posX.getValue() == 1, bottom = posY.getValue() == 1;
        boolean bar = showBar.getValue() && sidebarMode.getValue() != 3;
        float h = 8f * hs, pad = padding.getValue() * hs;
        float x = offsetX.getValue() + (1f + (bar ? 1f : 0f)) * hs;
        float y = offsetY.getValue() + hs;
        if (right) x = sr.getScaledWidth() - x;
        if (bottom) y = sr.getScaledHeight() - y - h;

        int idx = 0;
        for (Module m : active) {
            String name = nameOf(m);
            float tw = widthOf(m, f);
            int color = getColor(now, idx);
            float bx1 = x - hs - pad - (right ? tw : 0), bx2 = x + hs + pad + (right ? 0 : tw);
            float by1 = y - pad - (!bottom ? (idx == 0 ? hs : 0) : (shadow.getValue() ? hs : 0));
            float by2 = y + h + pad + (!bottom ? (shadow.getValue() ? hs : 0) : (idx == 0 ? hs : 0));
            if (background.getValue() > 0) RenderUtil.drawRect(bx1, by1, bx2, by2, ((int) (background.getValue() * 2.55f) << 24));
            if (bar) {
                switch (sidebarMode.getValue()) {
                    case 0: RenderUtil.drawRect(bx2, by1, bx2 + hs, by2, color); break;
                    case 1: RenderUtil.drawRect(bx1 - hs, by1, bx1, by2, color); break;
                    case 2: if (idx == 0) RenderUtil.drawRect(bx1, by1 - hs, bx2, by1, color); break;
                }
            }
            float tx = x - (right ? tw : 0), ty = y + hs;
            float cw = draw(f, name, tx, ty, color);
            if (suffixes.getValue()) {
                for (String s : m.getSuffix()) {
                    cw += draw(f, fmtSuffix(lowerCase.getValue() ? s.toLowerCase(Locale.ROOT) : s), tx + cw, ty, 0xFFAAAAAA);
                }
            }
            y += (h + (shadow.getValue() ? hs : 0) + pad * 2f) * (bottom ? -1f : 1f);
            idx++;
        }
    }

    private float draw(UIFont f, String s, float x, float y, int color) {
        return shadow.getValue() ? f.drawStringWithShadow(s, x, y, color) : f.drawString(s, x, y, color);
    }

    private static String two(long v) { return v < 10 ? "0" + v : String.valueOf(v); }

    private void drawInfoLine(long now) {
        UIFont f = FontManager.main(9f);
        long secs = (now - Myau.START_TIME) / 1000L;
        String time = two(secs / 3600) + ":" + two((secs / 60) % 60) + ":" + two(secs % 60);
        float x = 1f, y = 1.5f;
        x += f.drawStringWithShadow(Myau.INFO_NAME, x, y, getColor(now, 0));
        x += f.drawStringWithShadow(": ", x, y, 0xFFAAAAAA);
        x += f.drawStringWithShadow(time, x, y, 0xFFCCCCCC);
        x += f.drawStringWithShadow(" : ", x, y, 0xFFAAAAAA);
        f.drawStringWithShadow(Minecraft.getDebugFPS() + " fps", x, y, 0xFFCCCCCC);
    }
}
