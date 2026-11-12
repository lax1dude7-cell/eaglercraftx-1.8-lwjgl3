package myau.ui.components;

import myau.font.UIFont;
import myau.property.properties.ModeProperty;
import myau.ui.Component;
import myau.ui.Theme;

import java.util.concurrent.atomic.AtomicInteger;

public class ModeComponent implements Component {
    private final ModeProperty p;
    private final ModuleComponent m;
    private int offsetY;

    public ModeComponent(ModeProperty p, ModuleComponent m, int offsetY) { this.p = p; this.m = m; this.offsetY = offsetY; }

    public void draw(AtomicInteger offset) {
        UIFont f = Theme.small();
        float y = m.category.getY() + offsetY + (12 - f.getHeight()) / 2f;
        float x = m.category.getX() + 4;
        String mode = p.getModeString().replace("_", " ");
        mode = mode.substring(0, 1).toUpperCase() + mode.substring(1).toLowerCase();
        x += f.drawStringWithShadow(p.getName() + ": ", x, y, Theme.WHITE);
        f.drawStringWithShadow(mode, x, y, Theme.BLUE);
    }

    public void update(int mx, int my) {}
    public void setComponentStartAt(int y) { offsetY = y; }
    public int getHeight() { return 12; }
    public boolean isVisible() { return p.isVisible(); }
    public boolean isCapturingInput() { return false; }
    public void mouseReleased(int x, int y, int b) {}
    public void keyTyped(char c, int k) {}

    public void mouseDown(int x, int y, int b) {
        int cy = m.category.getY() + offsetY;
        if (m.panelExpand && x > m.category.getX() && x < m.category.getX() + m.category.getWidth() && y > cy && y < cy + 11) {
            if (b == 0) p.nextMode(); else if (b == 1) p.previousMode();
        }
    }
}
