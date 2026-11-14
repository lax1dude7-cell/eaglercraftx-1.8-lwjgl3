package myau.event;

import myau.Myau;
import myau.event.events.ModuleEvent;
import myau.events.*;
import myau.module.Kinds;
import myau.module.Module;

import java.util.List;

/** Reflection-free event dispatcher. Same call site as OpenMyau+: EventManager.call(new XEvent(...)). */
public final class EventManager {
    private EventManager() {}

    public static void call(ModuleEvent e) {
        if (Myau.moduleManager == null) return;
        int kind = e.getKind();
        e.dispatchManagers();
        List<Module> all = Myau.moduleManager.all();
        int n = all.size();
        for (int p = 0; p <= 4; p++) {
            for (int i = 0; i < n; i++) {
                Module m = all.get(i);
                if (m.isEnabled() && m.getPriority(kind) == p) e.dispatch(m);
            }
        }
    }
}
