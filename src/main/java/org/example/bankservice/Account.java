package org.example.bankservice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
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

    private final BigDecimal ratePerAnno;

    private final Stack<Transaction> transactions;
    private final Set<Client> clients;
    final TestInterface testInterface;

    public Account(String accountNumber, Client client, ZonedDateTime timestamp, BigDecimal ratePerAnno) {
        this.accountNumber = accountNumber;
        this.ratePerAnno = ratePerAnno;
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

    public void deposit(BigDecimal value, ZonedDateTime timestamp, String description) {
        addTransaction(value, timestamp, description);
    }

    public void withdraw(BigDecimal value, ZonedDateTime timestamp, String description) {
        addTransaction(value.negate(), timestamp, description);
    }

    private void addTransaction(BigDecimal change, ZonedDateTime timestamp, String description) {
        Transaction lastTransaction = transactions.lastElement();

        long duration_s = Duration.between(lastTransaction.timestamp(), timestamp).getSeconds();
        BigDecimal localRate = computeLocalRate(duration_s);

        BigDecimal interest = lastTransaction.balance().multiply(localRate);
        interest = interest.setScale(2, RoundingMode.HALF_EVEN);

        addTransaction_raw(interest, timestamp, "interest of last time period");
        addTransaction_raw(change  , timestamp, description);
    }

    private void addTransaction_raw(BigDecimal change, ZonedDateTime timestamp, String description) {
        Transaction transaction = new Transaction(change, getBalance().add(change), timestamp, description);
        transactions.push(transaction);
    }

    private BigDecimal computeLocalRate(long duration_s) {
        // rate_konform := (1 + rate)^(duration / 1 year) - 1;
        double rate = ratePerAnno.doubleValue();
        double rate_konform = Math.pow(1 + rate, duration_s / (365.0 * 86400.0)) - 1;
        return BigDecimal.valueOf(rate_konform);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getRatePerAnno() {
        return ratePerAnno;
    }

    public BigDecimal getBalance() {
        return transactions.isEmpty()
                ? BigDecimal.ZERO
                : transactions.peek().balance();
    }

    @SuppressWarnings("CommentedOutCode")
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

    public void showListOfTransactions(String indent) {
        System.out.printf("%sTransactions: [%d]%n", indent, transactions.size());
        for (int i = 0; i < transactions.size(); i++)
            System.out.printf("%s   [%03d] %s%n", indent, i + 1, transactions.get(i).toLine());
    }
}
