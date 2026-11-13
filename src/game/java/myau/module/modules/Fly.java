package myau.module.modules;

import myau.module.Category;

/** PLACEHOLDER until the Movement category is ported: exists so the Combat modules that reference it compile and run. */
public class Fly extends StubModule {
    public Fly() { super("Fly", Category.MOVEMENT, false, false, new String[0]); }
}
