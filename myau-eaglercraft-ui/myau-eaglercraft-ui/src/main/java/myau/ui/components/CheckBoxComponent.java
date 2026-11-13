package myau.ui.components;

import myau.font.UIFont;
import myau.property.properties.BooleanProperty;
import myau.ui.Component;
import myau.ui.Theme;

import java.util.concurrent.atomic.AtomicInteger;

public class CheckBoxComponent implements Component {
    private final BooleanProperty p;
    private final ModuleComponent m;
    private int offsetY;

    public CheckBoxComponent(BooleanProperty p, ModuleComponent m, int offsetY) { this.p = p; this.m = m; this.offsetY = offsetY; }

    public void draw(AtomicInteger offset) {
        UIFont f = Theme.small();
        float y = m.category.getY() + offsetY + (12 - f.getHeight()) / 2f;
        float x = m.category.getX() + 4;
        x += f.drawString(p.getName().replace("-", " ") + ": ", x, y, Theme.WHITE);
        f.drawString(p.formatValue(), x, y, p.getValue() ? Theme.GREEN : Theme.RED);
    }

    public void update(int mx, int my) {}
    public void setComponentStartAt(int y) { offsetY = y; }
    public int getHeight() { return 12; }
    public boolean isVisible() { return p.isVisible(); }
    public boolean isCapturingInput() { return false; }
    public void mouseReleased(int x, int y, int b) {}
    public void keyTyped(char c, int k) {}

    public void mouseDown(int x, int y, int b) {
        if (b == 0 && m.panelExpand && hovered(x, y)) p.setValue(!p.getValue());
    }

    private boolean hovered(int x, int y) {
        int cy = m.category.getY() + offsetY;
        return x > m.category.getX() && x < m.category.getX() + m.category.getWidth() && y > cy && y < cy + 11;
    }
}
