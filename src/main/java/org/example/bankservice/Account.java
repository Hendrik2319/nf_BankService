package org.example.bankservice;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Account {
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
