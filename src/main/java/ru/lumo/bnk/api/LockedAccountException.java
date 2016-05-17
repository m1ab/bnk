package ru.lumo.bnk.api;

/**
 * Created by misha on 16.05.16.
 */
public class LockedAccountException extends Exception {
    public LockedAccountException(String message) {
        super(message);
    }
}
