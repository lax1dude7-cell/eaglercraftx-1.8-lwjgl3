package myau.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {
    private final Map<Class<?>, Module> byClass = new LinkedHashMap<Class<?>, Module>();
    private final List<Module> all = new ArrayList<Module>();

    public void register(Module m) {
        byClass.put(m.getClass(), m);
        all.add(m);
    }

    public boolean has(String name) { return getModule(name) != null; }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> c) { return (T) byClass.get(c); }

    public Module getModule(String name) {
        for (Module m : all) if (m.getName().equalsIgnoreCase(name)) return m;
        return null;
    }

    public List<Module> all() { return all; }

    public List<Module> byCategory(Category c) {
        List<Module> l = new ArrayList<Module>();
        for (Module m : all) if (m.getCategory() == c) l.add(m);
        Collections.sort(l, new Comparator<Module>() {
            public int compare(Module a, Module b) { return a.getName().compareToIgnoreCase(b.getName()); }
        });
        return l;
    }
}
