package com.isi.desa.UI.Menu;

import java.util.Scanner;

public interface MenuComponent {
    String getTitle();
    void execute(Scanner scanner) throws Exception;
}
