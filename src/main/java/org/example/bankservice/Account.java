package org.example.bankservice;

import java.math.BigDecimal;

public class Account {
    private final String accountNumber;
    private BigDecimal balance;
    private final Client client;

    public Account(String accountNumber, BigDecimal balance, Client client) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.client = client;
    }

    public void deposit(double money) {
        balance = balance.add(BigDecimal.valueOf(money));
    }

    public void withdraw(double money) {
        balance = balance.subtract(BigDecimal.valueOf(money));
    }
}
