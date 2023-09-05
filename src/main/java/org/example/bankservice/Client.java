package org.example.bankservice;

/*
        -----------------------------------
        ----  BankService-Setup

        Erstellt eine Record 'Client' mit den Eigenschaften Vorname, Nachname und
        Kundennummer (wähle geeignete englische Feldnamen).

        Erstellt eine Klasse 'Account' (kein Record, soll vorerst veränderlich sein)
        mit den Eigenschaften Kontonummer (String), Kontostand (BigDecimal) und dem zugehörigen Kunden.

        Bitte poste hier den Link zu eurem GitHub-Repository, in dem ihr eure Lösungen teilt.
 */
public record Client(String firstname, String lastName, int customerNumber) {
}
