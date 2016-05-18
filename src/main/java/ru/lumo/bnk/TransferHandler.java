package ru.lumo.bnk;

import ru.lumo.bnk.api.Bank;
import ru.lumo.bnk.core.BankImpl;
import ru.lumo.bnk.util.AppHelper;

import java.util.List;

/**
 * Created by misha on 17.05.16.
 */
public class TransferHandler {

    private Bank bank;
    private String[] accNumbers;

    public TransferHandler() {
        this.bank = BankImpl.getInstance();
        List<String> set = bank.getAccNumbers();
        accNumbers = set.toArray(new String[set.size()]);
    }

    public void makeTransfer() {
        String[] pair = AppHelper.getNextRandomPairAccounts(accNumbers);
        bank.transfer(pair[0], pair[1], AppHelper.nextRandomTransferAmount());
    }
}
