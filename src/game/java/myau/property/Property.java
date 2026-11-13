package myau.property;

import myau.property.properties.BooleanProperty;
import myau.property.properties.ModeProperty;

/** Non-generic on purpose (TeaVM WASM-GC dislikes generic bridge methods + boxing). */
public abstract class Property {
    private final String name;
    private Condition cond;
    private Property dep;
    private int depKind; // 1 = bool true, 2 = mode >= depVal, 3 = mode == depVal
    private int depVal;

    protected Property(String name) { this.name = name; }

    public String getName() { return name; }
    public void setCondition(Condition c) { this.cond = c; }

    public void showIf(BooleanProperty p) { dep = p; depKind = 1; }
    public void showIfModeAtLeast(ModeProperty p, int v) { dep = p; depKind = 2; depVal = v; }
    public void showIfModeIs(ModeProperty p, int v) { dep = p; depKind = 3; depVal = v; }

    public boolean isVisible() {
        if (cond != null && !cond.test()) return false;
        if (dep == null) return true;
        if (depKind == 1) return ((BooleanProperty) dep).getValue();
        int m = ((ModeProperty) dep).getValue();
        return depKind == 2 ? m >= depVal : m == depVal;
    }

    public abstract String formatValue();
}
