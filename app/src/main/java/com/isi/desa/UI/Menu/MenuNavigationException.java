package com.isi.desa.UI.Menu;

public class MenuNavigationException extends Exception {
    public enum Type { BACK_TO_ROOT }

    private final Type type;

    public MenuNavigationException(Type type) {
        super("Navigation signal: " + type);
        this.type = type;
    }

    public Type getType() { return type; }
}

