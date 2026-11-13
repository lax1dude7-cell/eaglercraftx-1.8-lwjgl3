package myau.ui.components;

import myau.font.UIFont;
import myau.module.Module;
import myau.ui.Component;
import myau.ui.Theme;
import myau.util.RenderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CategoryComponent {
    private static final int MAX_HEIGHT = 300;
    public final String name;
    public final ArrayList<Component> modules = new ArrayList<Component>();
    private boolean open;
    private final int width = 92, bh = 13;
    private int x = 5, y = 5;
    public boolean dragging;
    public int dragX, dragY;
    private int scroll;
    private double animScroll;
    private int height;

    public CategoryComponent(String name, List<Module> mods) {
        this.name = name;
        int tY = bh + 3;
        for (Module m : mods) {
            modules.add(new ModuleComponent(m, this, tY));
            tY += 16;
        }
    }

    public ArrayList<Component> getModules() { return modules; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getBodyTop() { return y + bh + 3; }
    public boolean isOpened() { return open; }
    public void setOpened(boolean o) { open = o; }
    public void setX(int v) { x = v; }
    public void setY(int v) { y = v; }
    public String getName() { return name; }

    public boolean isInBody(int mx, int my) {
        return my >= getBodyTop() && my <= getBodyTop() + MAX_HEIGHT;
    }

    public void render(long time) {
        update();
        height = 0;
        for (Component c : modules) height += c.getHeight();
        int maxScroll = Math.max(0, height - MAX_HEIGHT);
        if (scroll > maxScroll) scroll = maxScroll;
        if (animScroll > maxScroll) animScroll = maxScroll;
        animScroll += (scroll - animScroll) * 0.2;

        UIFont f = Theme.title();
        if (!modules.isEmpty() && open) {
            int displayHeight = Math.min(height, MAX_HEIGHT);
            RenderUtil.drawRect(x - 1, y, x + width + 1, y + bh + displayHeight + 4, 0x64000000);
        }
        RenderUtil.drawRect(x - 2, y, x + width + 2, y + bh + 3, 0xC8000000);
        float ty = y + (bh + 3 - f.getHeight()) / 2f;
        f.drawString(name, x + 2, ty, Theme.WHITE);
        String sign = open ? "-" : "+";
        f.drawString(sign, x + 86 - f.getStringWidth(sign) / 2f, ty, Theme.WHITE);

        if (open && !modules.isEmpty()) {
            int renderHeight = 0;
            RenderUtil.setClip(getBodyTop(), getBodyTop() + MAX_HEIGHT);
            for (Component c : modules) {
                int ch = c.getHeight();
                if (renderHeight + ch > animScroll && renderHeight < animScroll + MAX_HEIGHT) {
                    int drawY = (int) (renderHeight - animScroll);
                    c.setComponentStartAt(bh + 3 + drawY);
                    c.draw(new AtomicInteger(0));
                }
                renderHeight += ch;
            }
            RenderUtil.clearClip();
            if (height > MAX_HEIGHT) {
                float sy = getBodyTop() + (float) (animScroll * MAX_HEIGHT / height);
                RenderUtil.drawRect(x + width - 2, sy, x + width, sy + (float) MAX_HEIGHT * MAX_HEIGHT / height, 0x3CFFFFFF);
            }
        }
    }

    public void update() {
        int offset = bh + 3;
        for (Component c : modules) {
            c.setComponentStartAt(offset);
            offset += c.getHeight();
        }
    }

    public void handleDrag(int mx, int my, int sw, int sh) {
        if (dragging) {
            x = Math.max(2, Math.min(sw - width - 2, mx - dragX));
            y = Math.max(0, Math.min(sh - 16, my - dragY));
        }
    }

    public boolean insideHeader(int mx, int my) {
        return mx >= x - 2 && mx <= x + width + 2 && my >= y && my <= y + bh + 3;
    }

    public boolean onToggleButton(int mx, int my) {
        return mx >= x + 77 && mx <= x + width && my >= y + 2 && my <= y + bh + 1;
    }

    public void onScroll(int mx, int my, int dir) {
        if (!open || height <= MAX_HEIGHT) return;
        if (mx >= x && mx <= x + width && my >= getBodyTop() && my <= getBodyTop() + MAX_HEIGHT) {
            scroll -= dir * 12;
            scroll = Math.max(0, Math.min(scroll, height - MAX_HEIGHT));
        }
    }
}
