package myau.property.properties;

import myau.property.Property;
import java.util.function.BooleanSupplier;

public class ModeProperty extends Property<Integer> {
    private final String[] modes;
    public ModeProperty(String name, int def, String[] modes) { this(name, def, modes, null); }
    public ModeProperty(String name, int def, String[] modes, BooleanSupplier vis) { super(name, def, vis); this.modes = modes; }
    public String getModeString() { return modes[value]; }
    public String[] getModes() { return modes; }
    public void nextMode() { value = (value + 1) % modes.length; }
    public void previousMode() { value = (value - 1 + modes.length) % modes.length; }
    public String formatValue() { return getModeString(); }
}
