package myau.ui.components;

import myau.font.UIFont;
import myau.ui.Component;
import myau.ui.Theme;
import myau.util.Compat;

import java.util.concurrent.atomic.AtomicInteger;

public class BindComponent implements Component {
    private final ModuleComponent m;
    private int offsetY;
    private boolean listening;

    public BindComponent(ModuleComponent m, int offsetY) { this.m = m; this.offsetY = offsetY; }

    public void draw(AtomicInteger offset) {
        UIFont f = Theme.small();
        float y = m.category.getY() + offsetY + (12 - f.getHeight()) / 2f;
        float x = m.category.getX() + 4;
        x += f.drawStringWithShadow("bind: ", x, y, Theme.WHITE);
        f.drawStringWithShadow(listening ? "..." : "[" + Compat.keyName(m.mod.getKey()) + "]", x, y, listening ? Theme.AQUA : Theme.GRAY);
    }

    public void mouseDown(int x, int y, int b) {
        int cy = m.category.getY() + offsetY;
        if (m.panelExpand && b == 0 && x > m.category.getX() && x < m.category.getX() + m.category.getWidth() && y > cy && y < cy + 11) listening = !listening;
    }

    public void keyTyped(char c, int key) {
        if (!listening) return;
        if (key == 1 || key == 14 || key == 211) m.mod.setKey(0); // ESC / BACKSPACE / DELETE clears
        else m.mod.setKey(key);
        listening = false;
    }

    public void update(int mx, int my) {}
    public void mouseReleased(int x, int y, int b) {}
    public void setComponentStartAt(int y) { offsetY = y; }
    public int getHeight() { return 12; }
    public boolean isVisible() { return true; }
    public boolean isCapturingInput() { return listening; }
}
