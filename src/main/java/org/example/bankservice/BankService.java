package org.example.bankservice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class BankService {
    /*
        ---------------------------------------
        ----  Verwaltung

        Nun implementieren wir eine Klasse, die eine Liste von Kunden und deren Konten verwaltet.

        Erstellt eine Klasse 'BankService', die eine Menge von Konten verwaltet.

        Implementiert eine Methode im BankService, um ein Konto zu eröffnen. Man soll dafür nur einen
        Kunden als Argument übergeben müssen. Es soll die neue Kontonummer zurückgeben.

        Implementiert eine Methode im BankService, von einer Kontonummer (als String) einen bestimmten
        Betrag (als BigDecimal) auf eine andere Kontonummer (als String) zu überweisen.


        ---------------------------------------
        ----  Gemeinschaftskonten und -trennungen

        Was passiert, wenn 3 Cent auf zwei Personen aufgeteilt werden?

        Baut Eure Records und Klassen so um, dass ein Konto mehreren Kontoinhaberinnen
        gehören kann (zwei oder mehreren Kontoinhaberinnen).

        Schreibe im Service eine Methode public List<String> split(String accountNumber),
        die ein Konto zu gleichen Teilen aufteilt. Aus einem Gemeinschaftskonto soll dabei
        pro Kontoinhaber*in ein Einzelkonto entstehen. Es soll die entstandenen neuen
        Kontonummern zurückgeben. Jedes Konto soll nach der Aufteilung gleich viel Geld
        abbekommen (+- 1 Cent). Achte darauf, dass uns als Bank dabei weder Cent-Gewinne
        noch Cent-Verluste entstehen.

        PS: wie üblich gibt es auch bei unserer Bank keine halben Cent ;)

        Tipp: auch hier eignet sich Test Driven Development sehr gut, um die Aufgabe zu lösen!
        (gilt auch für die folgenden Aufgaben)
     */

    private final Map<String,Account> accounts;
    final TestInterface testInterface;

    public BankService() {
        this.accounts = new HashMap<>();
        testInterface = new TestInterface();
    }

    class TestInterface {
        Account getAccount(String accountNumber) {
            return accounts.get(accountNumber);
        }
    }

    public String createAccount( Client client, Instant timestamp) {

        int index = 1;
        String newAccountNumber = String.format("%010d_%04d", client.customerNumber(), index);
        while (accounts.containsKey( newAccountNumber ))
            newAccountNumber = String.format("%010d_%04d", client.customerNumber(), ++index);

        accounts.put(newAccountNumber, new Account(newAccountNumber, client, timestamp));
        return newAccountNumber;
    }

    private boolean doWithAccount(String accountNumber, String message, Consumer<Account> action) {
        Account account = accounts.get(accountNumber);
        if (account==null) {
            System.out.printf("%s Can't find account \"%s\".%n", message, accountNumber);
            return false;
        }

        action.accept(account);
        return true;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean addClientToAccount(String accountNumber, Client client) {
        return doWithAccount(accountNumber, "Can't add client.", account -> account.addClient(client));
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean deposit(String accountNumber, BigDecimal value, Instant timestamp, String description) {
        return doWithAccount(accountNumber, "Can't deposit money.", account -> account.deposit(value, timestamp, description));
    }

    @SuppressWarnings({"UnusedReturnValue"})
    public boolean withdraw(String accountNumber, BigDecimal value, Instant timestamp, String description) {
        return doWithAccount(accountNumber, "Can't withdraw money.", account -> account.withdraw(value, timestamp, description));
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean transfer(BigDecimal value, String sourceAccountNumber, String targetAccountNumber, Instant timestamp, String description) {
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

        sourceAccount.withdraw( value, timestamp, "Transfer From: "+description );
        targetAccount.deposit ( value, timestamp, "Transfer To: "+description );
        return true;
    }

    public List<String> split(String accountNumber, Instant timestamp) {
        Account account = accounts.get(accountNumber);
        if (account==null) {
            System.out.printf("Account spliting aborted. Can't find account \"%s\".%n", accountNumber);
            return null;
        }

        BigDecimal balance = account.getBalance();
        int clientCount_int = account.getClientCount();
        BigDecimal clientCount = BigDecimal.valueOf(clientCount_int);
        BigDecimal floorValue = balance.divide(clientCount, 2, RoundingMode.FLOOR);
        BigDecimal remainingCent = balance.subtract(floorValue.multiply(clientCount));
        int remainingCentCount = remainingCent.multiply(BigDecimal.valueOf(100)).intValueExact();

        if (remainingCentCount >= clientCount_int)
            throw new IllegalStateException("count of remaining cents (%d)   >=   count of clients (%d)".formatted(remainingCentCount, clientCount_int));

        List<String> newAccountNumbers = new ArrayList<>();
        account.foreachClient(client -> {
            String newAccountNumber = createAccount(client, timestamp);
            newAccountNumbers.add(newAccountNumber);
            Account newAccount = accounts.get(newAccountNumber);
            if (newAccount==null) throw new IllegalStateException("Newly created account doesn't exists");
        });

        long now = System.currentTimeMillis();
        BigDecimal extraCent = new BigDecimal("0.01");
        for (int i=0; i<newAccountNumbers.size(); i++) {
            String newAccountNumber = newAccountNumbers.get(i);
            Account newAccount = accounts.get(newAccountNumber);
            if (newAccount==null) throw new IllegalStateException("Newly created account doesn't exists");
            newAccount.deposit(
                    i<remainingCentCount
                        ? floorValue.add(extraCent)
                        : floorValue,
                    timestamp, "Initial Balance"
            );
        }

        accounts.remove(accountNumber);

        return newAccountNumbers;
    }
}
