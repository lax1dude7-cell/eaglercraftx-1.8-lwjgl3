package myau.module.modules;

import myau.module.Category;
import myau.module.Module;

/** Empty module with no behaviour: only exists so the ClickGUI/arraylist match the reference screenshot. */
public class StubModule extends Module {
    private final String[] suffix;
    public StubModule(String name, Category cat, boolean enabled, boolean hidden, String[] suffix) {
        super(name, cat, enabled, hidden);
        this.suffix = suffix;
    }
    public String[] getSuffix() { return suffix; }
}
