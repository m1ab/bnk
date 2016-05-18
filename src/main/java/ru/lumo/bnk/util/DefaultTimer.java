package ru.lumo.bnk.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by misha on 17.05.16.
 */
public class DefaultTimer<T extends Object> implements Timer<T> {

    private final Logger log = Logger.getLogger(DefaultTimer.class.getName());

    private long milliseconds;
    private Class<T> clazz;

    public DefaultTimer(Class<T> clazz) {
        this.clazz = clazz;
        restart();
    }

    @Override
    public void restart() {
        milliseconds = System.currentTimeMillis();
    }

    @Override
    public void log(String m) {
        log.log(Level.INFO, String.format("[%s] (%d ms): %s", clazz, System.currentTimeMillis() - milliseconds, m));
    }

    @Override
    public void logr(String m) {
        log.log(Level.INFO, String.format("[%s] (%d ms): %s", clazz, System.currentTimeMillis() - milliseconds, m));
        restart();
    }
}
