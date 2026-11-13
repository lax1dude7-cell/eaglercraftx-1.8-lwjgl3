package myau.module.modules;

import myau.module.Category;
import myau.property.properties.PercentProperty;

/** PLACEHOLDER until the Movement category is ported: exists so the Combat modules that reference it compile and run. */
public class KeepSprint extends StubModule {
    public KeepSprint() { super("KeepSprint", Category.MOVEMENT, false, false, new String[0]); add(slowdown); }
    public final PercentProperty slowdown = new PercentProperty("slowdown", 0);
}
