package com.isi.desa.Service.Implementations;

import com.isi.desa.Service.Interfaces.ILogger;
import org.springframework.stereotype.Service;

// @Service // Descomentar para correr con Spring Boot
public class Logger implements ILogger {
    // Instancia única (eager singleton)
    private static final Logger INSTANCE = new Logger();

    // Constructor privado para evitar instanciación externa
    private Logger() {}

    // Método público para obtener la instancia
    public static Logger getInstance() {
        return INSTANCE;
    }

    private void log(String message, boolean error, Throwable throwable) {
        // Persistir en el futuro
        if (error) {
            System.err.println(message);
            if (throwable != null) {
                throwable.printStackTrace(System.err);
            }
        } else {
            System.out.println(message);
        }
    }

    @Override
    public void info(String message) {
        this.log("[INFO] " + message, false, null);
    }

    @Override
    public void warn(String message) {
        this.log("[WARN] " + message, false, null);
    }

    @Override
    public void error(String message, Throwable throwable) {
        this.log("[ERROR] " + message, true, throwable);
    }
}
