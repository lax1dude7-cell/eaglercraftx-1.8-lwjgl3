package myau.property.properties;

import myau.property.Property;

/** Value is 0xRRGGBB. */
public class ColorProperty extends Property {
    private int value;
    public ColorProperty(String name, int rgb) { super(name); value = rgb & 0xFFFFFF; }
    public ColorProperty(String name, int rgb, myau.property.Condition c) { this(name, rgb); setCondition(c); }
    public int getValue() { return value; }
    public void setValue(int v) { value = v & 0xFFFFFF; }
    public String formatValue() {
        String h = Integer.toHexString(value);
        while (h.length() < 6) h = "0" + h;
        return "#" + h.toUpperCase();
    }
}
