package myau.property.properties;

import myau.property.Property;
import java.util.function.BooleanSupplier;

/** Value is 0xRRGGBB (alpha ignored). */
public class ColorProperty extends Property<Integer> {
    public ColorProperty(String name, int rgb) { super(name, rgb & 0xFFFFFF, null); }
    public ColorProperty(String name, int rgb, BooleanSupplier vis) { super(name, rgb & 0xFFFFFF, vis); }
    public void setValue(Integer v) { value = v & 0xFFFFFF; }
    public String formatValue() {
        String h = Integer.toHexString(value);
        while (h.length() < 6) h = "0" + h;
        return "#" + h.toUpperCase();
    }
}
