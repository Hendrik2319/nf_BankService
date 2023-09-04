package org.example.bankservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankService {
    /*
        Nun implementieren wir eine Klasse, die eine Liste von Kunden und deren Konten verwaltet.

        Erstellt eine Klasse 'BankService', die eine Menge von Konten verwaltet.

        Implementiert eine Methode im BankService, um ein Konto zu eröffnen. Man soll dafür nur einen
        Kunden als Argument übergeben müssen. Es soll die neue Kontonummer zurückgeben.

        Implementiert eine Methode im BankService, von einer Kontonummer (als String) einen bestimmten
        Betrag (als BigDecimal) auf eine andere Kontonummer (als String) zu überweisen.
     */

    private Map<String,Account> accounts;

    public BankService() {
        this.accounts = new HashMap<>();
    }

    public String createAccount( Client client ) {

        int index = 1;
        String newAccountNumber = String.format("%010d_%04d", client.customerNumber(), index);
        while (accounts.containsKey( newAccountNumber ))
            newAccountNumber = String.format("%010d_%04d", client.customerNumber(), ++index);

        accounts.put(newAccountNumber, new Account(newAccountNumber, BigDecimal.ZERO, client));
        return newAccountNumber;
    }

    public boolean transfer(BigDecimal value, String sourceAccountNumber, String targetAccountNumber) {
        Account sourceAccount = accounts.get(sourceAccountNumber);
        Account targetAccount = accounts.get(targetAccountNumber);
        if (sourceAccount==null) {
            System.out.printf("Transfer aborted. Can't find source account \"%s\".%n", sourceAccountNumber);
            return false;
        }
        if (targetAccount==null) {
            System.out.printf("Transfer aborted. Can't find target account \"%s\".%n", targetAccountNumber);
            return false;
        }

        sourceAccount.withdraw( value );
        targetAccount.deposit ( value );
        return true;
    }
}
