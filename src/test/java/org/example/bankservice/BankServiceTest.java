package org.example.bankservice;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {

    @Test
    void testCreateAccount() {
        // Given
        BankService bankService = new BankService();
        Client client = new Client("FirstName1", "LastName1", 12345);

        // When
        String accountNo = bankService.createAccount(client);

        // Then
        assertNotNull(accountNo, "No AccountNumber returned");

        Account account = bankService.getAccount(accountNo);
        assertNotNull(account, "Account doesn't exist after creation");
    }

    @Test
    void testTransfer() {
        // Given
        BankService bankService = new BankService();
        Client client1 = new Client("FirstName1", "LastName1", 12345);
        String accountNo1 = bankService.createAccount(client1);
        String accountNo2 = bankService.createAccount(client1);
        Account account1 = bankService.getAccount(accountNo1);
        Account account2 = bankService.getAccount(accountNo2);

        BigDecimal transferValue = new BigDecimal("12.40");
        BigDecimal initialBalance1 = new BigDecimal("100");
        BigDecimal initialBalance2 = new BigDecimal("90");
        BigDecimal expectedFinalBalance1 = new BigDecimal("87.60");
        BigDecimal expectedFinalBalance2 = new BigDecimal("102.40");

        // When
        bankService.deposit(accountNo1, initialBalance1);
        bankService.deposit(accountNo2, initialBalance2);
        bankService.transfer(transferValue, accountNo1, accountNo2);
        BigDecimal actualBalance1 = account1.getBalance();
        BigDecimal actualBalance2 = account2.getBalance();

        // Then
        assertEquals(actualBalance1, expectedFinalBalance1);
        assertEquals(actualBalance2, expectedFinalBalance2);
    }

    @Test
    void testSplit() {
        // Given
        BankService bankService = new BankService();
        String accountNo1 = bankService.createAccount(new Client("FirstName1", "LastName1", 12345));
        bankService.addClientToAccount(accountNo1, new Client("FirstName2", "LastName2", 12346));
        bankService.addClientToAccount(accountNo1, new Client("FirstName3", "LastName3", 12347));

        BigDecimal oldBalance = new BigDecimal("100.00");
        bankService.deposit(accountNo1, oldBalance);

        // When
        List<String> newAccountNumbers = bankService.split(accountNo1);

        // Then
        Account oldAccount = bankService.getAccount(accountNo1);
        assertNull(oldAccount, "Old account still exists after split");

        assertNotNull(newAccountNumbers, "Returned list of account numbers is NULL");

        BigDecimal sumOfBalances = BigDecimal.ZERO;
        for (String newAccountNumber : newAccountNumbers) {
            Account newAccount = bankService.getAccount(newAccountNumber);
            assertNotNull(newAccount, "One account (%s) of returned list doesn't exist".formatted(newAccountNumber));

            BigDecimal newBalance = newAccount.getBalance();
            assertTrue(newBalance.compareTo(new BigDecimal("33.33"))>=0);
            assertTrue(newBalance.compareTo(new BigDecimal("33.34"))<=0);

            sumOfBalances = sumOfBalances.add(newBalance);
        }

        assertEquals(oldBalance, sumOfBalances, "Sum of balances of created account has to be equal to balance of old account");
    }
}