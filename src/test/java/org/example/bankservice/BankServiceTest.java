package org.example.bankservice;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {

    @SuppressWarnings("SimplifiableAssertion")
    @Test
    void testCreateAccount() {
        // Given
        ZonedDateTime now = ZonedDateTime.now();

        BankService bankService = new BankService();
        Client client = new Client("FirstName1", "LastName1", 12345);
        BigDecimal ratePerAnno = new BigDecimal("0.035");

        // When
        String accountNo = bankService.createAccount(client, now, ratePerAnno);

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
        ZonedDateTime timeCreated  = ZonedDateTime.of(2023, 7, 20, 12, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime timeDeposit1 = ZonedDateTime.of(2023, 8,  1, 15, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime timeDeposit2 = ZonedDateTime.of(2023, 8,  5,  8, 0, 0, 0, ZoneId.systemDefault());
        BigDecimal valueInterest1 = new BigDecimal(  "0.00");
        BigDecimal valueDeposit1  = new BigDecimal("100.34");
        BigDecimal valueInterest2 = new BigDecimal(  "0.04");
        BigDecimal valueDeposit2  = new BigDecimal( "23.42");
        BigDecimal balance0 = new BigDecimal(  "0.00");
        BigDecimal balance1 = new BigDecimal(  "0.00");
        BigDecimal balance2 = new BigDecimal("100.34");
        BigDecimal balance3 = new BigDecimal("100.38");
        BigDecimal balance4 = new BigDecimal("123.80");

        BankService bankService = new BankService();
        Client client = new Client("FirstName1", "LastName1", 12345);
        BigDecimal ratePerAnno = new BigDecimal("0.035");
        String accountNo = bankService.createAccount(client, timeCreated, ratePerAnno);

        // When
        bankService.deposit(accountNo, valueDeposit1, timeDeposit1, "First Transaction");
        bankService.deposit(accountNo, valueDeposit2, timeDeposit2, "Second Transaction");

        // Then
        assertNotNull(accountNo, "No AccountNumber returned");

        Account account = bankService.testInterface.getAccount(accountNo);
        assertNotNull(account, "Can't find account after creation");

        Transaction trInitial   = account.testInterface.getTransaction(0);
        Transaction trInterest1 = account.testInterface.getTransaction(1);
        Transaction trDeposit1  = account.testInterface.getTransaction(2);
        Transaction trInterest2 = account.testInterface.getTransaction(3);
        Transaction trDeposit2  = account.testInterface.getTransaction(4);
        assertNotNull(trInitial  , "No "+"initial "   +"transaction");
        assertNotNull(trInterest1, "No "+"interest 1 "+"transaction");
        assertNotNull(trDeposit1 , "No "+"deposit 1 " +"transaction");
        assertNotNull(trInterest2, "No "+"interest 1 "+"transaction");
        assertNotNull(trDeposit2 , "No "+"deposit 2 " +"transaction");

        assertTrue(balance0       .compareTo(trInitial  .balance())==0, "Balance of %s transaction == %s".formatted("initial "   , balance0));
        assertTrue(balance1       .compareTo(trInterest1.balance())==0, "Balance of %s transaction == %s".formatted("interest 1 ", balance1));
        assertTrue(balance2       .compareTo(trDeposit1 .balance())==0, "Balance of %s transaction == %s".formatted("deposit 1 " , balance2));
        assertTrue(balance3       .compareTo(trInterest2.balance())==0, "Balance of %s transaction == %s".formatted("interest 1 ", balance2));
        assertTrue(balance4       .compareTo(trDeposit2 .balance())==0, "Balance of %s transaction == %s".formatted("deposit 2 " , balance2));
        assertTrue(BigDecimal.ZERO.compareTo(trInitial  .change ())==0, "Change of %s transaction == %s".formatted("initial "   , BigDecimal.ZERO));
        assertTrue(valueInterest1 .compareTo(trInterest1.change ())==0, "Change of %s transaction == %s".formatted("interest 1 ", valueInterest1 ));
        assertTrue(valueDeposit1  .compareTo(trDeposit1 .change ())==0, "Change of %s transaction == %s".formatted("deposit 1 " , valueDeposit1  ));
        assertTrue(valueInterest2 .compareTo(trInterest2.change ())==0, "Change of %s transaction == %s".formatted("interest 1 ", valueInterest2 ));
        assertTrue(valueDeposit2  .compareTo(trDeposit2 .change ())==0, "Change of %s transaction == %s".formatted("deposit 2 " , valueDeposit2  ));
    }

    @Test
    void testTransfer() {
        // Given
        ZonedDateTime now = ZonedDateTime.now();

        BankService bankService = new BankService();
        Client client1 = new Client("FirstName1", "LastName1", 12345);
        BigDecimal ratePerAnno = new BigDecimal("0.035");

        String accountNo1 = bankService.createAccount(client1, now, ratePerAnno);
        String accountNo2 = bankService.createAccount(client1, now, ratePerAnno);
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
        ZonedDateTime now = ZonedDateTime.now();

        BankService bankService = new BankService();
        String accountNo1 = bankService.createAccount(new Client("FirstName1", "LastName1", 12345), now, new BigDecimal("0.035"));
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