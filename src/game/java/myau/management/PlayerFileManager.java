package myau.management;

import myau.util.Color;
import myau.util.Compat;

import java.util.ArrayList;

/** Friend / enemy list persisted in browser storage (no java.io.File under TeaVM). */
public abstract class PlayerFileManager {
    public ArrayList<String> players = new ArrayList<String>();
    private final String storageKey;
    public Color color;

    public PlayerFileManager(String storageKey, Color color) {
        this.storageKey = storageKey;
        this.color = color;
    }

    public void load() {
        players.clear();
        String s = Compat.storageGet(storageKey);
        if (s == null) return;
        String[] parts = s.split("\n");
        for (int i = 0; i < parts.length; i++) {
            String t = parts[i].trim();
            if (t.length() > 0) players.add(t);
        }
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) { if (i > 0) sb.append('\n'); sb.append(players.get(i)); }
        Compat.storageSet(storageKey, sb.toString());
    }

    public String add(String name) {
        if (isFriend(name)) return null;
        players.add(name);
        save();
        return name;
    }

    public String remove(String name) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).equalsIgnoreCase(name)) {
                String p = players.remove(i);
                save();
                return p;
            }
        }
        return null;
    }

    public void clear() { players.clear(); save(); }

    public boolean isFriend(String name) {
        for (int i = 0; i < players.size(); i++) if (players.get(i).equalsIgnoreCase(name)) return true;
        return false;
    }

    public ArrayList<String> getPlayers() { return players; }
    public Color getColor() { return color; }
}
