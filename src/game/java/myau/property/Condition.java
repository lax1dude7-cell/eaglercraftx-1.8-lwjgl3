package myau.property;

/** Visibility condition for a setting (anonymous class instead of a lambda: TeaVM WASM-GC safe). */
public interface Condition {
    boolean test();
}
