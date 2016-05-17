package ru.lumo.bnk.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by misha on 17.05.16.
 */
public class NanoTimer<T extends Object> {

    private final Logger log = Logger.getLogger(NanoTimer.class.getName());

    private long nanos;
    private Class<T> clazz;

    public NanoTimer(Class<T> clazz) {
        this.clazz = clazz;
        restart();
    }

    public void restart() {
        nanos = System.nanoTime();
    }

    public void log(String m) {
        log.log(Level.INFO, String.format("[%s] (%d ns): %s", clazz, System.nanoTime() - nanos, m));
    }

    public void logr(String m) {
        log.log(Level.INFO, String.format("[%s] (%d ns): %s", clazz, System.nanoTime() - nanos, m));
        restart();
    }
}
