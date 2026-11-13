package myau.ui.components;

import myau.font.UIFont;
import myau.property.properties.ColorProperty;
import myau.ui.Component;
import myau.ui.Theme;
import myau.util.ColorUtil;
import myau.util.RenderUtil;

import java.util.concurrent.atomic.AtomicInteger;

/** Three thin bars: hue / saturation / brightness. */
public class ColorSliderComponent implements Component {
    private final ColorProperty p;
    private final ModuleComponent m;
    private int offsetY;
    private int drag = -1; // 0 hue, 1 sat, 2 bri
    private float[] hsb;

    public ColorSliderComponent(ColorProperty p, ModuleComponent m, int offsetY) {
        this.p = p; this.m = m; this.offsetY = offsetY;
        hsb = ColorUtil.rgbToHSB(p.getValue());
    }

    public void draw(AtomicInteger offset) {
        int cx = m.category.getX() + 4, cy = m.category.getY() + offsetY, w = m.category.getWidth() - 8;
        UIFont f = Theme.small();
        float x = cx, ty = cy + (10 - f.getHeight()) / 2f;
        x += f.drawStringWithShadow(p.getName().replace("-", " ") + ": ", x, ty, Theme.WHITE);
        f.drawStringWithShadow(p.formatValue(), x, ty, 0xFF000000 | p.getValue());
        int seg = 23;
        for (int i = 0; i < seg; i++) {
            float t0 = i / (float) seg, x0 = cx + w * t0, x1 = cx + w * (i + 1) / (float) seg;
            RenderUtil.drawRect(x0, cy + 10, x1, cy + 13, ColorUtil.hsbToRGB(t0, 1f, 1f));
            RenderUtil.drawRect(x0, cy + 14, x1, cy + 17, ColorUtil.hsbToRGB(hsb[0], t0, hsb[2]));
            RenderUtil.drawRect(x0, cy + 18, x1, cy + 21, ColorUtil.hsbToRGB(hsb[0], hsb[1], t0));
        }
        int[] ys = {10, 14, 18};
        float[] vals = {hsb[0], hsb[1], hsb[2]};
        for (int i = 0; i < 3; i++) {
            float mx = cx + w * Math.min(0.99f, vals[i]);
            RenderUtil.drawRect(mx - 0.5f, cy + ys[i] - 1, mx + 0.5f, cy + ys[i] + 4, Theme.WHITE);
        }
    }

    public void update(int mx, int my) {
        if (drag < 0) return;
        int cx = m.category.getX() + 4, w = m.category.getWidth() - 8;
        float v = Math.max(0f, Math.min(1f, (mx - cx) / (float) w));
        hsb[drag] = v;
        p.setValue(ColorUtil.hsbToRGB(hsb[0], hsb[1], hsb[2]));
    }

    public void mouseDown(int x, int y, int b) {
        if (!m.panelExpand || b != 0) return;
        int cx = m.category.getX() + 4, cy = m.category.getY() + offsetY, w = m.category.getWidth() - 8;
        if (x < cx || x > cx + w) return;
        if (y >= cy + 9 && y < cy + 13) drag = 0;
        else if (y >= cy + 13 && y < cy + 17) drag = 1;
        else if (y >= cy + 17 && y < cy + 22) drag = 2;
    }

    public void mouseReleased(int x, int y, int b) { drag = -1; }
    public void keyTyped(char c, int k) {}
    public void setComponentStartAt(int y) { offsetY = y; }
    public int getHeight() { return 24; }
    public boolean isVisible() { return p.isVisible(); }
    public boolean isCapturingInput() { return false; }
}
