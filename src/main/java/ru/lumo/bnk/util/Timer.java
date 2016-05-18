package ru.lumo.bnk.util;

/**
 * Created by misha on 18.05.16.
 */
public interface Timer<T extends Object> {

    void restart();

    void log(String m);

    void logr(String m);
}
