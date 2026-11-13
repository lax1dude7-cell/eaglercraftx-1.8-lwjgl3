package myau.ui;

import myau.font.FontManager;
import myau.font.UIFont;
import myau.module.modules.HUD;
import myau.Myau;

public final class Theme {
    private Theme() {}
    public static UIFont title() { return FontManager.main(9f); }
    public static UIFont small() { return FontManager.main(6f); }
    public static final int WHITE = 0xFFFFFFFF;
    public static final int GRAY = 0xFFAAAAAA;
    public static final int DISABLED = 0xFF666666;
    public static final int SLIDER_BG = 0xFF444444; // -12302777 in the original
    public static final int BLUE = 0xFF5555FF;
    public static final int AQUA = 0xFF55FFFF;
    public static final int GREEN = 0xFF55FF55;
    public static final int RED = 0xFFFF5555;

    public static int hud(long time, long offset) {
        HUD h = Myau.moduleManager.getModule(HUD.class);
        return h == null ? 0xFF55FF55 : h.getColor(time, offset);
    }
}
