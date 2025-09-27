package com.isi.desa.Service.Interfaces;

public interface ILogger {
    void info(String message);
    void warn(String message);
    void error(String message, Throwable throwable);
}

