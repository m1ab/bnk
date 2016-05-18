package ru.lumo.bnk.api;

import java.util.List;

/**
 * Created by misha on 16.05.16.
 */
public interface Bank {

    long getBalance(String accNumber);

    Account getAccount(String accNumber);

    void openAccount(long money);

    long closeAccount(String accNumber);

    List<String> getAccNumbers();

    List<Account> getBlockedAccounts();

    boolean isFraud(String fromAccountNum, String toAccountNum, long amount) throws InterruptedException;

    void transfer(String fromAccountNum, String toAccountNum, long amount);

    int getTotalTransfers();
}
