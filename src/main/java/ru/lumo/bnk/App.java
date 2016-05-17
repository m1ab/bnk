package ru.lumo.bnk;

import ru.lumo.bnk.api.Bank;
import ru.lumo.bnk.core.BankImpl;
import ru.lumo.bnk.util.NanoTimer;

import java.util.Random;

/**
 * Hello world!
 *
 */
public class App {

    private final static Random r = new Random();

    public static void main( String[] args ) {

        NanoTimer<App> nt = new NanoTimer<>(App.class);
        nt.log("APP START");

        Bank bank = BankImpl.getInstance();

        generateAccounts(nt, bank, 1000);

        printAccounts(nt, bank);

        makeTransfers(10, 100, nt);

        printAccounts(nt, bank);

        System.out.printf("Total transfers: %d%n", bank.getTotalTransfers());
    }

    private static void printAccounts(NanoTimer nt, Bank bank) {
        String accNumber;
        for (Object obj : bank.getAccNumbersList()) {
            accNumber = obj.toString();
            System.out.println(bank.getAccount(accNumber).toString());
        }
        nt.log("ACCOUNTS PRINTED");
    }


    private static void makeTransfers(int threadsCount, int perThread, NanoTimer nt) {
        Thread[] threads = new Thread[threadsCount];
        for(int i = 0; i < threadsCount; i++) {
            threads[i] = new Thread() {
                public void run() {
                    new TransferHandler().makeTransfer(perThread);
                }
            };
            threads[i].start();
        }
        nt.log(String.format("TRANSFERS STARTED %d", threadsCount));
        int counter;
        while(true) {
            counter = 0;
            for (Thread t : threads) {
                if(t.isAlive()) {
                    counter++;
                }
            }
            //System.out.println(counter);
            if (counter == 0) break;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nt.log("TRANSFERS STOPPED");
    }

    private static void generateAccounts(NanoTimer nt, Bank bank, int quantity) {
        for(int i = 0; i < quantity; i++) {
            bank.openAccount(genMoney());
        }
        nt.log("ACCOUNTS GENERATED");
    }

    private static long genMoney() {
        return r.nextInt(1000) * 1000000;
    }
}
