package ru.lumo.bnk.util;

import com.sun.accessibility.internal.resources.accessibility;
import ru.lumo.bnk.api.Account;
import ru.lumo.bnk.api.Bank;
import ru.lumo.bnk.core.BankImpl;

import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by misha on 18.05.16.
 */
public class AppHelper {

    private static final Random r = new Random();

    public static void printAccounts(Timer timer, Bank bank) {
        bank.getAccNumbers().forEach(accNumber -> System.out.println(bank.getAccount(accNumber)));
        timer.log("ACCOUNTS PRINTED");
    }

    public static void printBlockedAccounts(Timer timer, Bank bank) {
        bank.getBlockedAccounts().forEach(account -> System.out.println(account));
        timer.log("BLOCK ACCOUNTS PRINTED");
    }

    public static void generateAccounts(Timer timer, Bank bank, int quantity) {
        for(int i = 0; i < quantity; i++) bank.openAccount(nextRandomAccountAmount());
        timer.log("ACCOUNTS GENERATED");
    }

    public static String[] getNextRandomPairAccounts(String[] accNumbers) {
        int size = accNumbers.length;
        int index1 = r.nextInt(size);
        int index2 = index1 > size/2 ? r.nextInt(size/2) : r.nextInt(size/2) + size/2;
        return new String[] {accNumbers[index1], accNumbers[index2]};
    }

    private static long nextRandomAccountAmount() {
        return r.nextInt(1000) * 1000000;
    }

    public static long nextRandomTransferAmount() {
        switch(r.nextInt(20)) {
            case 5: return (r.nextInt(100) + 111) * 49999;
            default: return (r.nextInt(40) + 10) * 1000;
        }
    }
}
