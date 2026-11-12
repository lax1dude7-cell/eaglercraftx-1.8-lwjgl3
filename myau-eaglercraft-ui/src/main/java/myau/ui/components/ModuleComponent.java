package myau.ui.components;

import myau.font.UIFont;
import myau.module.Module;
import myau.property.Property;
import myau.property.SliderProperty;
import myau.property.properties.*;
import myau.ui.Component;
import myau.ui.Theme;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ModuleComponent implements Component {
    public final Module mod;
    public final CategoryComponent category;
    public int offsetY;
    private final ArrayList<Component> settings = new ArrayList<Component>();
    public boolean panelExpand;

    public ModuleComponent(Module mod, CategoryComponent category, int offsetY) {
        this.mod = mod;
        this.category = category;
        this.offsetY = offsetY;
        int y = offsetY + 12;
        for (Property<?> p : mod.getProperties()) {
            Component c = null;
            if (p instanceof BooleanProperty) c = new CheckBoxComponent((BooleanProperty) p, this, y);
            else if (p instanceof SliderProperty) c = new SliderComponent((SliderProperty) p, this, y);
            else if (p instanceof ModeProperty) c = new ModeComponent((ModeProperty) p, this, y);
            else if (p instanceof ColorProperty) c = new ColorSliderComponent((ColorProperty) p, this, y);
            else if (p instanceof TextProperty) c = new TextComponent((TextProperty) p, this, y);
            if (c != null) { settings.add(c); y += c.getHeight(); }
        }
        settings.add(new BindComponent(this, y));
    }

    public void setComponentStartAt(int newOffsetY) {
        offsetY = newOffsetY;
        int y = offsetY + 16;
        for (Component c : settings) {
            c.setComponentStartAt(y);
            if (c.isVisible()) y += c.getHeight();
        }
    }

    public void draw(AtomicInteger offset) {
        int color = mod.isEnabled() ? Theme.hud(System.currentTimeMillis(), offset.get()) : Theme.DISABLED;
        UIFont f = Theme.title();
        float tx = category.getX() + category.getWidth() / 2f - f.getStringWidth(mod.getName()) / 2f;
        f.drawStringWithShadow(mod.getName(), tx, category.getY() + offsetY + (16 - f.getHeight()) / 2f, color);
        if (panelExpand) {
            for (Component c : settings) if (c.isVisible()) { c.draw(offset); offset.incrementAndGet(); }
        }
    }

    public int getHeight() {
        if (!panelExpand) return 16;
        int h = 16;
        for (Component c : settings) if (c.isVisible()) h += c.getHeight();
        return h;
    }

    public void update(int mx, int my) {
        if (!panelExpand) return;
        for (Component c : settings) if (c.isVisible()) c.update(mx, my);
    }

    public void mouseDown(int x, int y, int button) {
        if (!category.isInBody(x, y)) return;
        if (isHovered(x, y)) {
            if (button == 0) mod.toggle();
            else if (button == 1) panelExpand = !panelExpand;
        }
        if (!panelExpand) return;
        for (Component c : settings) if (c.isVisible()) c.mouseDown(x, y, button);
    }

    public void mouseReleased(int x, int y, int button) {
        if (!panelExpand) return;
        for (Component c : settings) if (c.isVisible()) c.mouseReleased(x, y, button);
    }

    public void keyTyped(char ch, int key) {
        if (!panelExpand) return;
        for (Component c : settings) if (c.isVisible()) c.keyTyped(ch, key);
    }

    public boolean isCapturingInput() {
        for (Component c : settings) if (c.isCapturingInput()) return true;
        return false;
    }

    public boolean isHovered(int x, int y) {
        return x > category.getX() && x < category.getX() + category.getWidth()
                && y > category.getY() + offsetY && y < category.getY() + offsetY + 16;
    }

    public boolean isVisible() { return true; }
}
