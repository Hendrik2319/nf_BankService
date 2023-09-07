package org.example.bankservice;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {

    @SuppressWarnings("SimplifiableAssertion")
    @Test
    void testCreateAccount() {
        // Given
        Clock clock = Clock.system(ZoneId.systemDefault());
        Instant now = clock.instant();

        BankService bankService = new BankService();
        Client client = new Client("FirstName1", "LastName1", 12345);

        // When
        String accountNo = bankService.createAccount(client, now);

        // Then
        assertNotNull(accountNo, "No AccountNumber returned");

        Account account = bankService.testInterface.getAccount(accountNo);
        assertNotNull(account, "Can't find account after creation");

        Transaction initialTransaction = account.testInterface.getTransaction(0);
        assertNotNull(initialTransaction, "No initial transaction in created account");
        assertTrue(BigDecimal.ZERO.compareTo(initialTransaction.balance())==0, "Balance of initial transaction == 0");
        assertTrue(BigDecimal.ZERO.compareTo(initialTransaction.change ())==0, "Change of initial transaction == 0");
    }

    @SuppressWarnings("SimplifiableAssertion")
    @Test
    void testDeposit() {
        // Given
        Instant timeCreated  = ZonedDateTime.of(2023, 7, 20, 12, 0, 0, 0, ZoneId.systemDefault()).toInstant();
        Instant timeDeposit1 = ZonedDateTime.of(2023, 8,  1, 15, 0, 0, 0, ZoneId.systemDefault()).toInstant();
        Instant timeDeposit2 = ZonedDateTime.of(2023, 8,  5,  8, 0, 0, 0, ZoneId.systemDefault()).toInstant();
        BigDecimal valueDeposit1 = new BigDecimal("100.34");
        BigDecimal valueDeposit2 = new BigDecimal( "23.42");
        BigDecimal balance0 = new BigDecimal(  "0.00");
        BigDecimal balance1 = new BigDecimal("100.34");
        BigDecimal balance2 = new BigDecimal("123.76");

        BankService bankService = new BankService();
        Client client = new Client("FirstName1", "LastName1", 12345);
        String accountNo = bankService.createAccount(client, timeCreated);

        // When
        bankService.deposit(accountNo, valueDeposit1, timeDeposit1, "First Transaction");
        bankService.deposit(accountNo, valueDeposit2, timeDeposit2, "Second Transaction");

        // Then
        assertNotNull(accountNo, "No AccountNumber returned");

        Account account = bankService.testInterface.getAccount(accountNo);
        assertNotNull(account, "Can't find account after creation");

        Transaction initialTransaction  = account.testInterface.getTransaction(0);
        Transaction deposit1Transaction = account.testInterface.getTransaction(1);
        Transaction deposit2Transaction = account.testInterface.getTransaction(2);
        assertNotNull(initialTransaction , "No "+"initial "  +"transaction");
        assertNotNull(deposit1Transaction, "No "+"deposit 1 "+"transaction");
        assertNotNull(deposit2Transaction, "No "+"deposit 2 "+"transaction");

        assertTrue(balance0       .compareTo(initialTransaction .balance())==0, "Balance of %s transaction == %s".formatted("initial "  , balance0));
        assertTrue(balance1       .compareTo(deposit1Transaction.balance())==0, "Balance of %s transaction == %s".formatted("deposit 1 ", balance1));
        assertTrue(balance2       .compareTo(deposit2Transaction.balance())==0, "Balance of %s transaction == %s".formatted("deposit 2 ", balance2));
        assertTrue(BigDecimal.ZERO.compareTo(initialTransaction .change ())==0, "Change of %s transaction == %s".formatted("initial "  , BigDecimal.ZERO));
        assertTrue(valueDeposit1  .compareTo(deposit1Transaction.change ())==0, "Change of %s transaction == %s".formatted("deposit 1 ", valueDeposit1  ));
        assertTrue(valueDeposit2  .compareTo(deposit2Transaction.change ())==0, "Change of %s transaction == %s".formatted("deposit 2 ", valueDeposit2  ));
    }

    @Test
    void testTransfer() {
        // Given
        Clock clock = Clock.system(ZoneId.systemDefault());
        Instant now = clock.instant();

        BankService bankService = new BankService();
        Client client1 = new Client("FirstName1", "LastName1", 12345);
        String accountNo1 = bankService.createAccount(client1, now);
        String accountNo2 = bankService.createAccount(client1, now);
        Account account1 = bankService.testInterface.getAccount(accountNo1);
        Account account2 = bankService.testInterface.getAccount(accountNo2);

        BigDecimal transferValue = new BigDecimal("12.40");
        BigDecimal initialBalance1 = new BigDecimal("100");
        BigDecimal initialBalance2 = new BigDecimal("90");
        BigDecimal expectedFinalBalance1 = new BigDecimal("87.60");
        BigDecimal expectedFinalBalance2 = new BigDecimal("102.40");

        // When
        bankService.deposit(accountNo1, initialBalance1, now, "Initial Balance");
        bankService.deposit(accountNo2, initialBalance2, now, "Initial Balance");
        bankService.transfer(transferValue, accountNo1, accountNo2, now, "Test Transfer");
        BigDecimal actualBalance1 = account1.getBalance();
        BigDecimal actualBalance2 = account2.getBalance();

        // Then
        assertEquals(actualBalance1, expectedFinalBalance1);
        assertEquals(actualBalance2, expectedFinalBalance2);
    }

    @Test
    void testSplit() {
        // Given
        Clock clock = Clock.system(ZoneId.systemDefault());
        Instant now = clock.instant();

        BankService bankService = new BankService();
        String accountNo1 = bankService.createAccount(new Client("FirstName1", "LastName1", 12345), now);
        bankService.addClientToAccount(accountNo1, new Client("FirstName2", "LastName2", 12346));
        bankService.addClientToAccount(accountNo1, new Client("FirstName3", "LastName3", 12347));

        BigDecimal oldBalance = new BigDecimal("100.00");
        bankService.deposit(accountNo1, oldBalance, now, "Initial Balance");

        // When
        List<String> newAccountNumbers = bankService.split(accountNo1, now);

        // Then
        Account oldAccount = bankService.testInterface.getAccount(accountNo1);
        assertNull(oldAccount, "Old account still exists after split");

        assertNotNull(newAccountNumbers, "Returned list of account numbers is NULL");

        BigDecimal sumOfBalances = BigDecimal.ZERO;
        for (String newAccountNumber : newAccountNumbers) {
            Account newAccount = bankService.testInterface.getAccount(newAccountNumber);
            assertNotNull(newAccount, "One account (%s) of returned list doesn't exist".formatted(newAccountNumber));

            BigDecimal newBalance = newAccount.getBalance();
            assertTrue(newBalance.compareTo(new BigDecimal("33.33"))>=0);
            assertTrue(newBalance.compareTo(new BigDecimal("33.34"))<=0);

            sumOfBalances = sumOfBalances.add(newBalance);
        }

        assertEquals(oldBalance, sumOfBalances, "Sum of balances of created account has to be equal to balance of old account");
    }
}