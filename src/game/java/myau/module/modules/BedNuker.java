package myau.module.modules;

import myau.module.Category;

/** PLACEHOLDER until the Misc category is ported: exists so the Combat modules that reference it compile and run. */
public class BedNuker extends StubModule {
    public BedNuker() { super("BedNuker", Category.MISC, false, false, new String[0]); }
    public boolean isReady() { return false; }
}
