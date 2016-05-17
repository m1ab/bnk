package ru.lumo.bnk.core;

import ru.lumo.bnk.api.Account;
import ru.lumo.bnk.api.LockedAccountException;

/**
 * Created by misha on 16.05.16.
 */
public class RegularAccount implements Account {

    private String accNumber;
    private long money;
    private boolean locked = false;
    private boolean checking = false;

    public RegularAccount() {
    }

    public RegularAccount(String accNumber) {
        this(accNumber, 0);
    }

    public RegularAccount(String accNumber, long money) {
        this.accNumber = accNumber;
        this.money = money;
    }

    @Override
    public String getAccNumber() {
        return accNumber;
    }

    @Override
    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    @Override
    public long getMoney() {
        return money;
    }

    @Override
    public void setMoney(long money) {
        this.money = money;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public synchronized boolean isChecking() {
        return checking;
    }

    public synchronized void setChecking(boolean checking) {
        this.checking = checking;
    }

    @Override
    public void debet(long amount) throws LockedAccountException {
        if (locked) {
            throw new LockedAccountException(String.format("%s is locked", toString()));
        }
        money = money - amount;
    }

    @Override
    public void credit(long amount) throws LockedAccountException {
        if (locked) {
            throw new LockedAccountException(String.format("%s is locked", toString()));
        }
        money = money + amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegularAccount that = (RegularAccount) o;

        return accNumber.equals(that.accNumber);

    }

    @Override
    public int hashCode() {
        return accNumber.hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("RegularAccount{")
                .append("accNumber='")
                .append(accNumber)
                .append('\'')
                .append(", money=")
                .append(money)
                .append(locked ? ", locked=" + locked : "")
                .append('}')
                .toString();
    }
}
