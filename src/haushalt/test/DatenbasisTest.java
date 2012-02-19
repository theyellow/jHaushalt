/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

jHaushalt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jHaushalt; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

package haushalt.test;

import static org.junit.Assert.*;
import haushalt.daten.*;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.MehrereJahre;
import haushalt.daten.zeitraum.Monat;

import org.junit.BeforeClass;
import org.junit.Test;

public class DatenbasisTest {
  
  private static Datenbasis db;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    db = new Datenbasis();
  }

  @Test
  public void testFindeOderErzeugeRegister() {
    Register reg1 = db.findeOderErzeugeRegister("Konto1");
    assertTrue("Das 1.Register sollte 'Konto1' sein.",
        reg1.toString().equals("Konto1"));
    Register reg2 = db.findeOderErzeugeRegister("Konto2");
    assertTrue("Das 2.Register sollte 'Konto2' sein.",
        reg2.toString().equals("Konto2"));
    Register reg3 = db.findeOderErzeugeRegister("Konto3");
    assertTrue("Das 3.Register sollte 'Konto3' sein.",
        reg3.toString().equals("Konto3"));
    Register reg4 = db.findeOderErzeugeRegister("Konto1");
    assertTrue("Das 1.+4.Register sollten gleich sein.",
        reg1 == reg4);
  }

  @Test
  public void testAendereRegisterIndex() {
    db.aendereRegisterIndex("Konto3", 1);
    String[] liste = db.getRegisterNamen();
    assertTrue("Das 2.Register sollte jetzt 'Konto3' sein.",
        liste[1].equals("Konto3"));
  }

  @Test
  public void testFindeOderErzeugeKategorieStringEinzelKategorie() {
    EinzelKategorie kat1 = db.findeOderErzeugeKategorie("MeineKategorie1");
    assertTrue("Die 1.Kategorie sollte 'MeineKategorie1' sein.",
        kat1.toString().equals("MeineKategorie1") && kat1.isHauptkategorie());
    EinzelKategorie kat2 = db.findeOderErzeugeKategorie("MeineKategorie2");
    assertTrue("Die 2.Kategorie sollte 'MeineKategorie2' sein.",
        kat2.toString().equals("MeineKategorie2") && kat2.isHauptkategorie());
    EinzelKategorie kat3 = db.findeOderErzeugeKategorie("MeineKategorie1:Unterkategorie");
    assertTrue("Die Unterkategorie sollte 'MeineKategorie1:Unterkategorie' sein.",
        kat3.toString().equals("MeineKategorie1:Unterkategorie") && !kat3.isHauptkategorie());
    EinzelKategorie kat4 = db.findeOderErzeugeKategorie("MeineKategorie1");
    assertTrue("Die 1.+4.Kategorie sollten gleich sein.",
        kat1 == kat4);
  }

  @Test
  public void testAddStandardBuchung() {
    EinzelKategorie kat1 = db.findeOderErzeugeKategorie("MeineKategorie1");
    StandardBuchung buchung1 = new StandardBuchung(
        new Datum(24,4,1971),
        "Testbuchung1",
        kat1,
        new Euro(24.4D)
        );
    int idx = db.addStandardBuchung("Konto1", buchung1);
    EinzelKategorie kat2 = db.findeOderErzeugeKategorie("MeineKategorie2");
    StandardBuchung buchung2 = new StandardBuchung(
        new Datum(28,3,1970),
        "Testbuchung2",
        kat2,
        new Euro(28.3D)
        );
    idx = db.addStandardBuchung("Konto1", buchung2);
    assertTrue("Die 2. Buchung sollte vor der 1. einsortiert werden.",
        (db.getAnzahlBuchungen("Konto1") == 2) & (idx == 0));
  }

  @Test
  public void testAddUmbuchung() {
    // Wichtig ist das neue Objekte 'Datum' verwendet werden, da diese nicht geklont werden!
    db.addUmbuchung(new Datum(1,1,2008), "Umbuchung1", "Konto1", "Konto2", new Euro(50.0D));
    assertTrue("In Konto1 / Konto2 müssen jetzt 3 / 1 Buchungen sein.",
        (db.getAnzahlBuchungen("Konto1") == 3) &&
        (db.getAnzahlBuchungen("Konto2") == 1));
    Euro saldo1 = db.getRegisterSaldo("Konto1", new Datum(1,1,2008));
    assertTrue("Der Saldo sollte 52,70€ sein.",
        saldo1.compareTo(new Euro(52.7D)) == 0);
    Euro saldo2 = db.getRegisterSaldo("Konto1", new Datum(2,1,2008));
    assertTrue("Der Saldo sollte 2,70€ sein.",
        saldo2.compareTo(new Euro(2.7D)) == 0);
    Euro saldo3 = db.getRegisterSaldo("Konto2", new Datum(2,1,2008));
    assertTrue("Der Saldo sollte 50€ sein.",
        saldo3.compareTo(new Euro(50.0D)) == 0);
  }

  @Test
  public void testErsetzeKategorie() {
    EinzelKategorie kat1 = db.findeOderErzeugeKategorie("MeineKategorie1");
    int anz = db.ersetzeKategorie(kat1, EinzelKategorie.SONSTIGES); // alteKategorie, neueKategorie)
    assertTrue("Es sollte eine Kategorie ersetzt worden sein.",
        anz == 1);
  }

  @Test
  public void testRegisterVereinigen() {
    EinzelKategorie kat1 = db.findeOderErzeugeKategorie("MeineKategorie1");
    StandardBuchung buchung = new StandardBuchung(
        new Datum(1,1,1970),
        "Testbuchung3",
        kat1,
        new Euro(1.0D)
        );
    db.addStandardBuchung("Konto3", buchung);
    db.registerVereinigen("Konto3", "Konto1");
    Euro saldo2 = db.getRegisterSaldo("Konto1", new Datum(2,1,2008));
    assertTrue("Der Saldo sollte 3,70€ sein.",
        saldo2.compareTo(new Euro(3.7D)) == 0);
    AbstractZeitraum zeitraum = new MehrereJahre(1970,2008);
    Euro euro = db.getEinnahmen(zeitraum, "Konto1");
    assertTrue("Die Einnahmen sollen 53,70€ sein.",
      euro.compareTo(new Euro(53.7D)) == 0);
    zeitraum = new Monat(1,1970);
    euro = db.getEinnahmen(zeitraum, "Konto1");
    assertTrue("Die Einnahmen sollen 1€ sein.",
      euro.compareTo(new Euro(1.0D)) == 0);
  }

  @Test
  public void testEntferneAlteBuchungen() {
    db.entferneAlteBuchungen(new Datum(27,3,1970));
    Euro saldo2 = db.getRegisterSaldo("Konto1", new Datum(2,1,2008));
    assertTrue("Der Saldo sollte 3,70€ sein.",
        saldo2.compareTo(new Euro(3.7D)) == 0);
    AbstractZeitraum zeitraum = new MehrereJahre(1970,2008);
    Euro euro = db.getEinnahmen(zeitraum, "Konto1");
    assertTrue("Die Einnahmen sollen 52,70€ sein.",
      euro.compareTo(new Euro(52.7D)) == 0);
    zeitraum = new Monat(1,1970);
    euro = db.getEinnahmen(zeitraum, "Konto1");
    assertTrue("Die Einnahmen sollen 0€ sein.",
      euro.compareTo(Euro.NULL_EURO) == 0);
  }

  @Test
  public void testGetAusgaben() {
    AbstractZeitraum zeitraum = new MehrereJahre(1970,2008);
    Euro euro = db.getAusgaben(zeitraum, "Konto1");
    assertTrue("Die Ausgaben sollen 0€ sein.",
      euro.compareTo(Euro.NULL_EURO) == 0);
  }

  @Test
  public void testGetSaldo() {
    Euro saldo = db.getSaldo(new Datum(2,1,1971));
    assertTrue("Der Saldo sollte 29,30€ sein.",
        saldo.compareTo(new Euro(29.3D)) == 0);
    saldo = db.getSaldo(new Datum(2,1,2008));
    assertTrue("Der Saldo sollte 53,70€ sein.",
        saldo.compareTo(new Euro(53.7D)) == 0);
  }

  @Test
  public void testAusfuehrenAutoBuchungen() {
    db.addAutoStandardBuchung();
    db.setAutoStandardBuchungIntervall(0, new Integer(1));
    db.setAutoStandardBuchungRegister(0, "Konto1");
    StandardBuchung buchung = db.getAutoStandardBuchung(0);
    buchung.setDatum(new Datum(15,1,2008));
    buchung.setKategorie(db.findeOderErzeugeKategorie("MeineKategorie1:Unterkategorie"));
    buchung.setWert(new Euro(1.0D));
    db.addAutoUmbuchung();
    db.setAutoUmbuchungIntervall(0, new Integer(1));
    db.setAutoUmbuchungRegister(0, new UmbuchungKategorie(
        db.findeOderErzeugeRegister("Konto1"),
        db.findeOderErzeugeRegister("Konto2")
        ));
    Umbuchung umbuchung = db.getAutoUmbuchung(0);
    umbuchung.setDatum(new Datum(17,1,2008));
    umbuchung.setWert(new Euro(1.0D));
    db.ausfuehrenAutoBuchungen(new Datum(1,1,2009));
    Euro saldo = db.getRegisterSaldo("Konto1", new Datum(1,1,2009));
    assertTrue("Der Saldo sollte 3,70€ sein.",
        saldo.compareTo(new Euro(3.7D)) == 0);
    saldo = db.getRegisterSaldo("Konto2", new Datum(1,1,2009));
    assertTrue("Der Saldo sollte 62€ sein.",
        saldo.compareTo(new Euro(62.0D)) == 0);
  }

}
