package ru.lumo.bnk.api;

/**
 * Created by misha on 16.05.16.
 */
public interface Account {
    long getMoney();
    void setMoney(long money);
    String getAccNumber();
    void setAccNumber(String accNumber);
    boolean isLocked();
    boolean isChecking();
    void setChecking(boolean checking);
    void setLocked(boolean locked);
    void debet(long money) throws LockedAccountException;
    void credit(long money) throws LockedAccountException;
}
