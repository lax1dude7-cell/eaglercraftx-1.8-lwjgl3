package myau.module;

import myau.property.Property;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

public class Module {
    private final String name;
    private final Category category;
    private boolean enabled;
    private final boolean hidden;
    private int key;
    private final List<Property<?>> properties = new ArrayList<Property<?>>();

    public Module(String name, Category category, boolean enabled, boolean hidden) {
        this.name = name;
        this.category = category;
        this.enabled = enabled;
        this.hidden = hidden;
    }

    protected <P extends Property<?>> P add(P p) { properties.add(p); return p; }

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public boolean isHidden() { return hidden; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }
    public List<Property<?>> getProperties() { return properties; }
    public String[] getSuffix() { return new String[0]; }

    public void toggle() { setEnabled(!enabled); }
    public void setEnabled(boolean e) {
        if (e == enabled) return;
        enabled = e;
        if (e) onEnable(); else onDisable();
    }

    protected void onEnable() {}
    protected void onDisable() {}
    /** Called every frame from the HUD hook while enabled. */
    public void onRender2D(float partialTicks, ScaledResolution sr) {}
    public void onTick() {}
}
