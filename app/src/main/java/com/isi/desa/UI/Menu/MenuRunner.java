package com.isi.desa.UI.Menu;

import java.util.Scanner;

public class MenuRunner {
    private final Menu root;
    private final Scanner scanner; // CAMBIO: Almacena el scanner

    // CAMBIO: Acepta un scanner en el constructor
    public MenuRunner(Menu root, Scanner scanner) {
        this.root = root;
        this.scanner = scanner;
    }

    public void run() {
        // CAMBIO: Ya no usa try-with-resources.
        // Simplemente ejecuta el menu con el scanner provisto.
        // El scanner sera cerrado por quien lo creo (la clase UI).
        root.execute(scanner);
    }
}