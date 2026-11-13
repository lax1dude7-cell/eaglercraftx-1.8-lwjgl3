package myau.ui.components;

import myau.font.UIFont;
import myau.property.properties.TextProperty;
import myau.ui.Component;
import myau.ui.Theme;

import java.util.concurrent.atomic.AtomicInteger;

public class TextComponent implements Component {
    private final TextProperty p;
    private final ModuleComponent m;
    private int offsetY;
    private boolean editing;

    public TextComponent(TextProperty p, ModuleComponent m, int offsetY) { this.p = p; this.m = m; this.offsetY = offsetY; }

    public void draw(AtomicInteger offset) {
        UIFont f = Theme.small();
        float y = m.category.getY() + offsetY + (12 - f.getHeight()) / 2f;
        float x = m.category.getX() + 4;
        x += f.drawStringWithShadow(p.getName().replace("-", " ") + ": ", x, y, Theme.WHITE);
        boolean caret = editing && (System.currentTimeMillis() / 500L) % 2L == 0L;
        f.drawStringWithShadow(p.getValue() + (caret ? "_" : ""), x, y, editing ? Theme.AQUA : Theme.GRAY);
    }

    public void mouseDown(int x, int y, int b) {
        int cy = m.category.getY() + offsetY;
        boolean hit = m.panelExpand && x > m.category.getX() && x < m.category.getX() + m.category.getWidth() && y > cy && y < cy + 11;
        if (b == 0) editing = hit ? !editing : false;
    }

    public void keyTyped(char c, int key) {
        if (!editing) return;
        if (key == 1 || key == 28) editing = false;
        else if (key == 14) { String v = p.getValue(); if (v.length() > 0) p.setValue(v.substring(0, v.length() - 1)); }
        else if (c >= 32 && c != 127 && p.getValue().length() < 64) p.setValue(p.getValue() + c);
    }

    public void update(int mx, int my) {}
    public void mouseReleased(int x, int y, int b) {}
    public void setComponentStartAt(int y) { offsetY = y; }
    public int getHeight() { return 12; }
    public boolean isVisible() { return p.isVisible(); }
    public boolean isCapturingInput() { return editing; }
}
