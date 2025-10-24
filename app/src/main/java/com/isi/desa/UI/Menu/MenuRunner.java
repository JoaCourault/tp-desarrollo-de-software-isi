package com.isi.desa.UI.Menu;

import java.util.Scanner;

public class MenuRunner {
    private final Menu root;

    public MenuRunner(Menu root) {
        this.root = root;
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            root.execute(scanner);
        }
    }
}

