package ru.lumo.bnk.core;

import ru.lumo.bnk.api.Account;
import ru.lumo.bnk.api.Bank;
import ru.lumo.bnk.api.LockedAccountException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by misha on 16.05.16.
 */
public class BankImpl implements Bank {

    private final Logger logger = Logger.getLogger(BankImpl.class.getName());

    private final Random random = new Random();
    private Map<String, Account> accountsCache = new ConcurrentHashMap<>();
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
    public List<String> getAccNumbers() {
        return accountsCache.keySet().parallelStream().sorted().collect(Collectors.toList());
    }

    @Override
    public List<Account> getBlockedAccounts() {
        return accountsCache
                .values()
                .parallelStream()
                .filter(account -> account.isLocked())
                .collect(Collectors.toList());
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

    private void sleepForChecking() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    @Override
    public void transfer(String fromAccountNum, String toAccountNum, long amount) {
        Account accFrom = accountsCache.get(fromAccountNum);
        Account accTo = accountsCache.get(toAccountNum);
        while (accFrom.isChecking() && accTo.isChecking()) {
            sleepForChecking();
        }
        if(accFrom.isLocked() || accTo.isLocked()) {
//            System.out.printf("    !!!!!!!!!   Deny transfer %d from %s to %s%n",
//                    amount, accFrom.getAccNumber(), accTo.getAccNumber());
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
                            getAccount(toAccountNum).setChecking(true);
                            System.out.printf("Hold for checking %s - %s %n", fromAccountNum, toAccountNum);
                            return isFraud(fromAccountNum, toAccountNum, amount);
                        } catch (InterruptedException ex) {
                            logger.log(Level.WARNING, ex.getMessage());
                            return true;
                        } finally {
                            getAccount(fromAccountNum).setChecking(false);
                            getAccount(toAccountNum).setChecking(false);
                            System.out.printf("Release %s - %s %n", fromAccountNum, toAccountNum);
                        }
                    }
                };
                t.start();
            } else {
                doTransactionalTransfer(accFrom, accTo, amount);
            }
        }
    }

    private class FraudWorker implements Runnable {
//http://www.journaldev.com/1069/java-thread-pool-example-using-executors-and-threadpoolexecutor
        private Account accFrom;
        private Account accTo;
        private long amount;

        public FraudWorker(Account accFrom, Account accTo,
                           long amount) {
            this.accFrom = accFrom;
            this.accTo = accTo;
            this.amount = amount;
        }

        public void run() {
            if (findFraud(accFrom.getAccNumber(), accTo.getAccNumber(), amount)) {
                lockAccounts(accFrom, accTo);
            } else {
                doTransactionalTransfer(accFrom, accTo, amount);
            }
        }

        private boolean findFraud(String fromAccountNum,
                                  String toAccountNum, long amount) {
            try {
                getAccount(fromAccountNum).setChecking(true);
                getAccount(toAccountNum).setChecking(true);
                System.out.printf("Hold for checking %s - %s %n", fromAccountNum, toAccountNum);
                return isFraud(fromAccountNum, toAccountNum, amount);
            } catch (InterruptedException ex) {
                logger.log(Level.WARNING, ex.getMessage());
                return true;
            } finally {
                getAccount(fromAccountNum).setChecking(false);
                getAccount(toAccountNum).setChecking(false);
                System.out.printf("Release %s - %s %n", fromAccountNum, toAccountNum);
            }
        }
    }

    private void lockAccounts(Account accFrom, Account accTo) {
        accFrom.setLocked(true);
        accTo.setLocked(true);
//        logger.logger(Level.INFO, String.format("Locked accounts %s and %s", accFrom, accTo));
    }

    public synchronized void doTransactionalTransfer(Account accFrom,
                                                     Account accTo,
                                                     long amount) {
        try {
            accFrom.debet(amount);
            accTo.credit(amount);
            totalTransfers++;
            System.out.println(".");
        } catch (LockedAccountException e) {
//            System.out.printf("    !!!!!!!!!   Deny transfer %d from %s to %s%n",
//                    amount, accFrom.getAccNumber(), accTo.getAccNumber());
        }
    }

    public int getTotalTransfers() {
        return totalTransfers;
    }
}
