package ru.lumo.bnk;

import ru.lumo.bnk.api.Bank;
import ru.lumo.bnk.core.BankImpl;
import ru.lumo.bnk.util.AppHelper;
import ru.lumo.bnk.util.DefaultTimer;
import ru.lumo.bnk.util.Timer;

/**
 * Transfer App
 *
 */
public class App {

    public static void main( String[] args ) {
        Timer<App> timer = new DefaultTimer<>(App.class);
        timer.log("APP START");

        Bank bank = BankImpl.getInstance();
        AppHelper.generateAccounts(timer, bank, 1000000);
//        AppHelper.printAccounts(timer, bank);
        makeTransfers(timer, 1000);
        AppHelper.printBlockedAccounts(timer, bank);

        System.out.printf("Total transfers: %d%n", bank.getTotalTransfers());
    }

    private static Thread asyncTransfer(TransferHandler transferHandler) {
        Thread t = new Thread() {
            public void run() {
                transferHandler.makeTransfer();
            }
        };
        t.start();
        return t;
    }

    private static void makeTransfers(Timer nt, int threadsCount) {
        final TransferHandler transferHandler = new TransferHandler();
        Thread[] threads = new Thread[threadsCount];
        for(int i = 0; i < threadsCount; i++) {
            threads[i] = asyncTransfer(transferHandler);
        }
        nt.log(String.format("TRANSFERS STARTED %d", threadsCount));
        int counter;
        while(true) {
            counter = 0;
            for (Thread t : threads) {
                if(t != null && t.isAlive()) {
                    counter++;
                }
            }
//            System.out.printf(".", counter);
            if (counter == 0) {
//                System.out.println(".");
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nt.log("TRANSFERS STOPPED");
    }

}
