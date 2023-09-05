package org.example.bankservice;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
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
     */
    private final String accountNumber;
    private BigDecimal balance;
    private final Set<Client> clients;

    public Account(String accountNumber, BigDecimal balance, Client client) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.clients = new HashSet<>();
        this.clients.add(client);
    }

    public void addClient(Client client) {
        clients.add(client);
    }

    public void deposit(BigDecimal value) {
        balance = balance.add( value );
    }

    public void withdraw(BigDecimal value) {
        balance = balance.subtract( value );
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", clients=" + clients +
                '}';
    }

    public void foreachClient(Consumer<Client> action) {
        clients.forEach(action);
    }

    public int getClientCount() {
        return clients.size();
    }
}
