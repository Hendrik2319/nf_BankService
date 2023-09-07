package org.example.bankservice;

import java.math.BigDecimal;
import java.time.Instant;

/*
        ---------------------------------------
        ----  Transaktionen

        "Einen Kontoauszug bitte!"
        Schreibt Eure Klassen so um, dass es nicht mehr einen fixen Kontostand gibt, sondern eine
        Liste von Transaktionen. Jede Transaktion hat einen Betrag, einen Saldo (neuer Kontostand
        nach der Veränderung), eine Beschreibung (optional) und ein Datum. Die Transaktionen sollen
        als Record implementiert werden. Um den aktuellen Kontostand zu ermitteln soll der BankService
        das Saldo der letzten Transaktion zurückgeben.
 */
public record Transaction(BigDecimal change, BigDecimal balance, Instant timestamp, String description) {
}
