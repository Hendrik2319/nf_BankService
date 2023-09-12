package org.example.bankservice;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

public class Main {
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


        ---------------------------------------
        ----  Zinsrechner

        "Eine Bank ohne Zinsen? Das geht ja gar nicht!"
        Erweitert den 'BankService' um eine Methode, die Zinsen für alle Kundenkonten basierend
        auf einem Zinssatz berechnet und gutschreibt. (Zinsen = Kontostand * (Zinssatz / 100).

        ###    Stupid calculation:  with current balance only and without time range


        ---------------------------------------
        ----  Transaktionen

        "Einen Kontoauszug bitte!"
        Schreibt Eure Klassen so um, dass es nicht mehr einen fixen Kontostand gibt, sondern eine
        Liste von Transaktionen. Jede Transaktion hat einen Betrag, einen Saldo (neuer Kontostand
        nach der Veränderung), eine Beschreibung (optional) und ein Datum. Die Transaktionen sollen
        als Record implementiert werden. Um den aktuellen Kontostand zu ermitteln soll der BankService
        das Saldo der letzten Transaktion zurückgeben.


    #################


        ---------------------------------------
        ----  Tagesgenaue Berechnung von Zinsen

        "Unterjährige Berechnung von Zinsen"
        Bei jeder Abheben- oder Einzahlen-Aktion sollen die Zinsen, die seit der letzten Transaktion
        angefallen sind berechnet werden. Der im BankService hinterlegte Zinssatz soll dabei den jährlichen
        Zins darstellen. Achtet bei der Berechnung auf den Zinseszinseffekt (bei 4% Zins sind nach einem
        halben Jahr noch nicht ganz 2% angefallen). Erstellt für Zins-Gutschriften (oder Zins-Belastungen)
        jeweils pro Transaktion eine zweite Transaktion.

     */
    public static void main(String[] args) {

        System.out.println(0.06 * 0.07);
        System.out.println(215.0 / 120.0);

        double a = 0.0212;
//        double a = Double.NaN;
        double b = Math.round(0.2123254*1000) / 10000.0;
        //noinspection ConstantValue
        System.out.printf(Locale.ENGLISH, "a:%f, b:%f -> a %s b%n", a,b, a==b ? "==" : a<b ? "<" : a>b ? ">" : "??");

        System.out.println();

        BigDecimal n1 = new BigDecimal("100.00");
        BigDecimal n2 = new BigDecimal("100");
        System.out.println("n1: "+n1);
        System.out.println("n2: "+n2);
        //noinspection NumberEquality
        System.out.println("n1==n2: "+(n1==n2));
        System.out.println("n1.equals(n2): "+ n1.equals(n2));
        System.out.println("n1.compareTo(n2): "+ n1.compareTo(n2));
        System.out.println();

        testBanking();
    }

    private static void testBanking() {
        ZonedDateTime timeCreated  = ZonedDateTime.of(2023, 7, 20, 12, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime timeDeposit1 = ZonedDateTime.of(2023, 8,  1, 15, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime timeDeposit2 = ZonedDateTime.of(2023, 8,  5,  8, 0, 0, 0, ZoneId.systemDefault());
        BigDecimal valueDeposit1 = new BigDecimal("100.34");
        BigDecimal valueDeposit2 = new BigDecimal( "23.42");

        BankService bankService = new BankService();
        Client client = new Client("FirstName1", "LastName1", 12345);
        BigDecimal ratePerAnno = new BigDecimal("0.035");
        String accountNo = bankService.createAccount(client, timeCreated, ratePerAnno);

        // When
        bankService.deposit(accountNo, valueDeposit1, timeDeposit1, "First Transaction");
        bankService.deposit(accountNo, valueDeposit2, timeDeposit2, "Second Transaction");

        bankService.showListOfTransactions(accountNo);
    }
}
