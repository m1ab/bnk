package ru.lumo.bnk;

import ru.lumo.bnk.api.Bank;
import ru.lumo.bnk.core.BankImpl;

import java.util.Random;

/**
 * Created by misha on 17.05.16.
 */
public class TransferHandler {

    private Bank bank = BankImpl.getInstance();
    private Object[] accNumbers;
    private Random r = new Random();
    private int size = 0;
    public TransferHandler() {
        accNumbers = bank.getAccNumbersList();
        size = accNumbers.length/2;
    }

    public void makeTransfer(int times) {
        for (int i = 0; i < times; i++) {
            bank.transfer(
                    accNumbers[r.nextInt(size)].toString(),
                    accNumbers[r.nextInt(size) + size].toString(),
                    genMoney());
        }
    }

    private long genMoney() {
        switch(r.nextInt(20)) {
            case 5: return (r.nextInt(100) + 111) * 49999;
            default: return (r.nextInt(40) + 10) * 1000;
        }
    }

}
