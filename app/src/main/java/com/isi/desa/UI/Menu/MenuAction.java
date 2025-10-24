package com.isi.desa.UI.Menu;

import java.util.Scanner;

@FunctionalInterface
public interface MenuAction {
    void execute(Scanner scanner) throws Exception;
}

