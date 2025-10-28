package com.isi.desa.UI.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu implements MenuComponent {
    private final String title;
    private final List<MenuComponent> children = new ArrayList<>();

    // Texto de la opcion de salida personalizable
    private String exitOptionText = "Volver"; // Valor por defecto

    public Menu(String title) {
        this.title = title;
    }

    public void add(MenuComponent comp) { this.children.add(comp); }
    public void remove(MenuComponent comp) { this.children.remove(comp); }

    // Nuevo metodo para setear el texto de salida
    public void setExitOptionText(String text) {
        this.exitOptionText = text;
    }

    @Override
    public String getTitle() { return this.title; }

    @Override
    public void execute(Scanner scanner) {
        while (true) {
            System.out.println("\n=== " + this.title + " ===");

            for (int i = 0; i < children.size(); i++) {
                System.out.println((i + 1) + ") " + children.get(i).getTitle());
            }
            // Se usa el texto de salida personalizable
            System.out.println("0) " + this.exitOptionText);

            System.out.print("Seleccione una opcion: ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.out.println("Entrada cerrada. Saliendo del menu.");
                return;
            }

            String line = scanner.nextLine();
            int opt;
            try {
                opt = Integer.parseInt(line.trim());
            } catch (Exception e) {
                System.out.println("Opcion invalida");
                continue;
            }

            if (opt == 0) {
                return;
            }
            if (opt < 0 || opt > children.size()) {
                System.out.println("Opcion invalida");
                continue;
            }

            MenuComponent selected = children.get(opt - 1);
            try {
                selected.execute(scanner);
            } catch (MenuNavigationException mne) {
                if (mne.getType() == MenuNavigationException.Type.BACK_TO_ROOT) {
                    return;
                } else {
                    System.out.println("Navegacion: " + mne.getType());
                }
            } catch (Exception e) {
                System.out.println("Ocurrio un error: " + e.getMessage());
            }
        }
    }
}