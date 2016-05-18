package ru.lumo.bnk;

import ru.lumo.bnk.api.Bank;
import ru.lumo.bnk.core.BankImpl;
import ru.lumo.bnk.util.AppHelper;
import ru.lumo.bnk.util.DefaultTimer;
import ru.lumo.bnk.util.Timer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        new App().makePooledTransfers(timer, 1000, 100);
//        makeTransfers(timer, 1000);
//        AppHelper.printBlockedAccounts(timer, bank);
        bank.shutdown();
        System.out.printf("Total transfers: %d%n", bank.getTotalTransfers());
    }

    private void makePooledTransfers(Timer timer, int times, int threadsCount) {
        final TransferHandler transferHandler = new TransferHandler();
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        for (int i = 0; i < times; i++) {
            executor.execute(new TransferWorker(transferHandler));
        }
        System.out.println("   *** --- Started all transfers --- ***   ");
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        timer.log("TRANSFERS STOPPED");
    }

    public class TransferWorker implements Runnable {

        final TransferHandler transferHandler;

        public TransferWorker(TransferHandler transferHandler){
            this.transferHandler = transferHandler;
        }

        @Override
        public void run() {
            transferHandler.makeTransfer();
        }
    }

//    private static void makeTransfers(Timer timer, int threadsCount) {
//        final TransferHandler transferHandler = new TransferHandler();
//        Thread[] threads = new Thread[threadsCount];
//        for(int i = 0; i < threadsCount; i++) {
//            threads[i] = asyncTransfer(transferHandler);
//        }
//        timer.log(String.format("TRANSFERS STARTED %d", threadsCount));
//        int counter;
//        while(true) {
//            counter = 0;
//            for (Thread t : threads) {
//                if(t != null && t.isAlive()) {
//                    counter++;
//                }
//            }
////            System.out.printf(".", counter);
//            if (counter == 0) {
////                System.out.println(".");
//                break;
//            }
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        timer.log("TRANSFERS STOPPED");
//    }
//
//    private static Thread asyncTransfer(TransferHandler transferHandler) {
//        Thread t = new Thread(() -> transferHandler.makeTransfer());
//        t.start();
//        return t;
//    }
}
