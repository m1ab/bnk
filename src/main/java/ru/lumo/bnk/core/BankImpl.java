package ru.lumo.bnk.core;

import ru.lumo.bnk.api.Account;
import ru.lumo.bnk.api.Bank;
import ru.lumo.bnk.api.LockedAccountException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by misha on 16.05.16.
 */
public class BankImpl implements Bank {

    private final Logger logger = Logger.getLogger(BankImpl.class.getName());

    private final Random random = new Random();
    private Map<String, Account> accountsCache = new LinkedHashMap<>();
    private int totalTransfers = 0;

    private static BankImpl ourInstance = new BankImpl();

    public static BankImpl getInstance() {
        return ourInstance;
    }

    private BankImpl() {
    }

    @Override
    public long getBalance(String accNumber) {
        return getAccount(accNumber).getMoney();
    }

    @Override
    public Account getAccount(String accNumber) {
        return accountsCache.get(accNumber);
    }

    @Override
    public Object[] getAccNumbersList() {
        return accountsCache.keySet().stream().sorted().toArray();
    }

    @Override
    public void openAccount(long money) {
        String accNumber = UUID.randomUUID().toString().replace("-", "0");
        Account account = new RegularAccount(accNumber, money);
        accountsCache.putIfAbsent(accNumber, account);
    }

    @Override
    public long closeAccount(String accNumber) {
        return 0;
    }

    @Override
    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum,
                                        long amount) throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    @Override
    public void transfer(String fromAccountNum, String toAccountNum, long amount) {
        Account accFrom = accountsCache.get(fromAccountNum);
        Account accTo = accountsCache.get(toAccountNum);
        while (accFrom.isChecking() && accTo.isChecking()) {
            try {
                Thread.sleep(100);
                System.out.println("........... checking ...............");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(accFrom.isLocked() || accTo.isLocked()) {
            logger.log(Level.INFO, String.format("Deny transfer %d from %s to %s",
                    amount, accFrom.getAccNumber(), accTo.getAccNumber()));
        } else {
            if (amount > 50000) {
                Thread t = new Thread() {

                    public void run() {
                        if (findFraud(fromAccountNum, toAccountNum, amount)) {
                            lockAccounts(accFrom, accTo);
                        } else {
                            doTransactionalTransfer(accFrom, accTo, amount);
                        }
                    }

                    private boolean findFraud(String fromAccountNum,
                                              String toAccountNum, long amount) {
                        try {
                            getAccount(fromAccountNum).setChecking(true);
                            System.out.printf("LOCK for checking %s%n", fromAccountNum);
                            getAccount(toAccountNum).setChecking(true);
                            System.out.printf("LOCK for checking %s%n", toAccountNum);
                            return isFraud(fromAccountNum, toAccountNum, amount);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                            return true;
                        } finally {
                            getAccount(fromAccountNum).setChecking(false);
                            System.out.printf("UNLOCK %s%n", fromAccountNum);
                            getAccount(toAccountNum).setChecking(false);
                            System.out.printf("UNLOCK %s%n", toAccountNum);
                        }
                    }
                };
                t.start();
            } else {
                doTransactionalTransfer(accFrom, accTo, amount);
            }
        }
    }

    private void lockAccounts(Account accFrom, Account accTo) {
        accFrom.setLocked(true);
        accTo.setLocked(true);
//        logger.logger(Level.INFO, String.format("Locked accounts %s and %s", accFrom, accTo));
    }

    public synchronized void doTransactionalTransfer(Account accFrom, Account accTo, long amount) {
        try {
            accFrom.debet(amount);
            accTo.credit(amount);
            totalTransfers++;
        } catch (LockedAccountException e) {
            logger.log(Level.INFO, String.format("Skip transfer %d from %s to %s",
                    amount, accFrom.getAccNumber(), accTo.getAccNumber()));
        }
    }

    public int getTotalTransfers() {
        return totalTransfers;
    }
}
