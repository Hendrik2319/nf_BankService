package org.example.bankservice;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Account {
    /*
        -----------------------------------
        ----  BankService-Setup

        Erstellt eine Record 'Client' mit den Eigenschaften Vorname, Nachname und
        Kundennummer (wähle geeignete englische Feldnamen).

        Erstellt eine Klasse 'Account' (kein Record, soll vorerst veränderlich sein)
        mit den Eigenschaften Kontonummer (String), Kontostand (BigDecimal) und dem zugehörigen Kunden.

        Bitte poste hier den Link zu eurem GitHub-Repository, in dem ihr eure Lösungen teilt.


        ---------------------------------------
        ----  Kontostand

        Nun implementieren wir convenience-Methoden, um den Kontostand zu ermitteln und zu verändern.

        Implementiert eine Methode, um Geld auf das Konto einzuzahlen.
        Implementiert eine Methode, um Geld vom Konto abzuheben.


        ---------------------------------------
        ----  Transaktionen

        "Einen Kontoauszug bitte!"
        Schreibt Eure Klassen so um, dass es nicht mehr einen fixen Kontostand gibt, sondern eine
        Liste von Transaktionen. Jede Transaktion hat einen Betrag, einen Saldo (neuer Kontostand
        nach der Veränderung), eine Beschreibung (optional) und ein Datum. Die Transaktionen sollen
        als Record implementiert werden. Um den aktuellen Kontostand zu ermitteln soll der BankService
        das Saldo der letzten Transaktion zurückgeben.
     */
    private final String accountNumber;
    private final Stack<Transaction> transactions;
    private final Set<Client> clients;
    final TestInterface testInterface;

    public Account(String accountNumber, Client client, Instant timestamp) {
        this.accountNumber = accountNumber;
        clients = new HashSet<>();
        clients.add(client);
        transactions = new Stack<>();
        transactions.add(new Transaction(BigDecimal.ZERO, BigDecimal.ZERO, timestamp,"Account created"));
        testInterface = new TestInterface();
    }

    class TestInterface {
        Transaction getTransaction(int index) {
            return index<0 || index>=transactions.size() ? null : transactions.get(index);
        }
    }

    public void addClient(Client client) {
        clients.add(client);
    }

    public void deposit(BigDecimal value, Instant timestamp, String description) {
        addTransaction(value, timestamp, description);
    }

    public void withdraw(BigDecimal value, Instant timestamp, String description) {
        addTransaction(value.negate(), timestamp, description);
    }

    private void addTransaction(BigDecimal change, Instant timestamp, String description) {
        Transaction transaction = new Transaction(change, getBalance().add(change), timestamp, description);
        transactions.push(transaction);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return transactions.isEmpty()
                ? BigDecimal.ZERO
                : transactions.peek().balance();
    }

    @Override
    public String toString() {
        return "Account{accountNumber='%s', %d transactions, %d clients}".formatted(accountNumber, transactions.size(), clients.size());
/*
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", transactions=" + transactions +
                ", clients=" + clients +
                '}';
*/
    }

    public void foreachClient(Consumer<Client> action) {
        clients.forEach(action);
    }

    public int getClientCount() {
        return clients.size();
    }
}
