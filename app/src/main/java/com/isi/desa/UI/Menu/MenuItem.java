package com.isi.desa.UI.Menu;

import java.util.Scanner;

public class MenuItem implements MenuComponent {
    private final String title;
    private final MenuAction action;

    public MenuItem(String title, MenuAction action) {
        this.title = title;
        this.action = action;
    }

    @Override
    public String getTitle() { return this.title; }

    @Override
    public void execute(Scanner scanner) throws Exception {
        action.execute(scanner);
    }
}
