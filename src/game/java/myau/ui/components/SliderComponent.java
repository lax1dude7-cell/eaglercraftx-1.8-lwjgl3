package myau.ui.components;

import myau.font.UIFont;
import myau.property.SliderProperty;
import myau.ui.Component;
import myau.ui.Theme;
import myau.util.RenderUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class SliderComponent implements Component {
    private final SliderProperty s;
    private final ModuleComponent m;
    private int offsetY;
    private boolean dragging;
    private double sliderWidth;
    private long inc, dec;

    public SliderComponent(SliderProperty s, ModuleComponent m, int offsetY) { this.s = s; this.m = m; this.offsetY = offsetY; }

    public void draw(AtomicInteger offset) {
        int cx = m.category.getX(), cy = m.category.getY() + offsetY;
        RenderUtil.drawRect(cx + 4, cy + 11, cx + 4 + m.category.getWidth() - 8, cy + 15, Theme.SLIDER_BG);
        int end = cx + 4 + (int) sliderWidth;
        if (end - (cx + 4) > 84) end = cx + 4 + 84;
        RenderUtil.drawRect(cx + 4, cy + 11, end, cy + 15, Theme.hud(System.currentTimeMillis(), offset.get()));
        UIFont f = Theme.small();
        float x = cx + 4, y = cy + 1f + (8 - f.getHeight()) / 2f;
        x += f.drawStringWithShadow(s.getName().replace("-", " ") + ": ", x, y, Theme.WHITE);
        f.drawStringWithShadow(s.getValueString(), x, y, Theme.AQUA);
    }

    public void update(int mx, int my) {
        int cx = m.category.getX(), w = m.category.getWidth() - 8;
        double d = Math.min(w, Math.max(0, mx - cx - 4));
        sliderWidth = w * (s.getDouble() - s.getMin()) / (s.getMax() - s.getMin());
        if (dragging) {
            double raw = d / w * (s.getMax() - s.getMin()) + s.getMin();
            double inc = s.getIncrement();
            if (inc > 0) raw = Math.round(raw / inc) * inc;
            s.setDouble(raw);
        }
        long now = System.currentTimeMillis();
        if (this.inc != 0 && this.inc < now) { this.inc = now + 50; s.setDouble(s.getDouble() + s.getStep()); }
        if (this.dec != 0 && this.dec < now) { this.dec = now + 50; s.setDouble(s.getDouble() - s.getStep()); }
    }

    public void mouseDown(int x, int y, int b) {
        if (!m.panelExpand) return;
        int cx = m.category.getX(), cy = m.category.getY() + offsetY, w = m.category.getWidth();
        if (x > cx && x < cx + w && y > cy + 8 && y < cy + 16) {
            if (b == 0) dragging = true;
            else if (b == 1) {
                boolean right = x > cx + w / 2;
                if (right && inc == 0) { inc = System.currentTimeMillis() + 500; s.setDouble(s.getDouble() + s.getStep()); }
                if (!right && dec == 0) { dec = System.currentTimeMillis() + 500; s.setDouble(s.getDouble() - s.getStep()); }
            }
        }
    }

    public void mouseReleased(int x, int y, int b) { dragging = false; inc = 0; dec = 0; }
    public void keyTyped(char c, int k) {}
    public void setComponentStartAt(int y) { offsetY = y; }
    public int getHeight() { return 16; }
    public boolean isVisible() { return s.isVisible(); }
    public boolean isCapturingInput() { return false; }
}
