/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

jHaushalt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.


(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

package haushalt.daten;

import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.gui.TextResource;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * Die Datenbasis beinhaltet alle Buchungen und dient zum 
 * Zugriff auf alle Daten. Sie verwaltet die Register und 
 * Kategorien. In der Datenbasis sind auch die gemerkten
 * Buchungen gespeichert. 
 * @author Dr. Lars H. Hahn
 * @version 2.5.3/2008.03.31
 */

 /* 
  * 2008.03.31 BugFix: Beim Laden der Register richtige Reihenfolge beachten
  * 2008.02.06 Verbesserter Test auf geaenderte Daten;
  *            Internationalisierung
  * 2008.02.04 Umstellung der Intervalle bei den autom. Buchungen auf Index
  * 2008.01.15 BugFix: Falls der Name des Intervall bei der automatischen Buchung
  *            unbekannt ist, wird jetzt "Jahr" angenommen 
  * 2007.05.24 Ausführen von automatischen Buchungen bis zu einem Datum 
  * 2007.03.28 BugFix: getKategorieSalden summierte, wenn die Unterkategorien
  *            NICHT verwendet werden, falsch auf (Lösung durch Nils op den Winkel)
  * 2007.02.22 Erweiterung: Ausgabe aller Buchungen
  * 2007.02.14 Verschieben eines Registers in der Register-Liste um ein
  *            Offset hinzugefügt.
  * 2007.02.13 Automatisches Einsortieren eines Registers entfernt
  * 2007.02.12 Umbenennen von Registern hinzugefügt
  * 2006.06.16 Erweiterung um automatische Umbuchung
  * 2006.01.27 Die Entscheidung, ob Unterkategorien verwendet
  *            werden fällt nicht mehr hier global, sondern
  *            individuell in den Auswertungen 
  * 2005.03.10 Erweiterung: Gemerkte Buchungen ab Datum
  * 2005.02.18 Erweiterung: Ersetzen einer Buchung liefert Position zurück.
  * 2004.08.25 BugFix: Einfügen einer StandardBuchung liefert Position zurück.
  */

public class Datenbasis {
	private static final boolean DEBUG = false;
  private static final TextResource res = TextResource.get();

	public final static String VERSION_DATENBASIS = "2.1.2";
	private boolean geaendert = false;

  public Datenbasis() {
		kategorieListe = new ArrayList<EinzelKategorie>();
		kategorieListe.add(EinzelKategorie.SONSTIGES);
  }
  
  /**
   * Ist <code>true</code>, wenn Daten geändert wurden.
   * @return geändert oder nicht geändert
   */
  public boolean isGeaendert() {
  	return geaendert;
  }
  
  /**
   * Wird aufgerufen, wenn die Daten geaendert wurden
   */
  public void setGeaendert() {
    geaendert = true;
  }
  
	// -- Kategorien --------------------------------------------
	 
  private final ArrayList<EinzelKategorie> kategorieListe;

	/**
	 * Liefert die Kategorie mit dem angegebene Namen zurück.
	 * Wenn sie noch nicht existiert, wird sie erzeugt.
	 * @param name Name der Kategorie
	 * @param hauptkategorie Hauptkategorie
	 * @return gesuchte Kategorie
	 */
	public EinzelKategorie findeOderErzeugeKategorie(String name, EinzelKategorie hauptkategorie) {
		EinzelKategorie kategorie = new EinzelKategorie(name, hauptkategorie);
		int pos = Collections.binarySearch(kategorieListe, kategorie);
		if (pos < 0) {
			kategorieListe.add(-pos-1, kategorie);
      geaendert = true;
			return kategorie;
		}
		return (EinzelKategorie)kategorieListe.get(pos);
	}
	
	public EinzelKategorie findeOderErzeugeKategorie(String vollerName) {
	  int n = vollerName.indexOf(":");
	  if(n == -1)
	    return findeOderErzeugeKategorie(vollerName, null);
	  EinzelKategorie hauptkategorie = findeOderErzeugeKategorie(vollerName.substring(0,n), null);
	  return findeOderErzeugeKategorie(vollerName.substring(n+1), hauptkategorie);
	}

	public boolean isKategorie(String name, EinzelKategorie hauptkategorie) {
		EinzelKategorie kategorie = new EinzelKategorie(name, hauptkategorie);
		int pos = Collections.binarySearch(kategorieListe, kategorie);
		if (pos < 0)
		  return false;
		return true;
	}

  /**
   * Liefert ein Array mit allen Kategorien.
   * Wenn die Unterkategorien verwendet werden sollen,
   * werden alle Kategorien geliefert, sonst nur die
   * Haupt-Kategorien
   * @return Array mit den Kategorien
   */
  public EinzelKategorie[] getKategorien(boolean unterkategorienVerwenden) {
    if(unterkategorienVerwenden)
      return (EinzelKategorie[])kategorieListe.toArray(new EinzelKategorie[kategorieListe.size()]);
    // Es sollen nur die Hauptkategorien ausgegeben werden:
    final int anzahl = kategorieListe.size();
    EinzelKategorie[] kategorien = new EinzelKategorie[anzahl];
    int katZaehler = 0;
    for(int i=0; i<anzahl; i++) {
      EinzelKategorie kategorie = (EinzelKategorie)kategorieListe.get(i);
      if(kategorie.isHauptkategorie())
        kategorien[katZaehler++] = kategorie;
    }
    EinzelKategorie[] haupt = new EinzelKategorie[katZaehler];
    System.arraycopy(kategorien,0,haupt,0,katZaehler);
    return haupt;
  }

	/**
   * Ersetzt in allen Registern die angegebne Kategorie durch eine neue.
	 * @param alteKategorie
	 * @param neueKategorie
	 * @return Anzahl ersetzter Kategorien
	 */
  public int ersetzeKategorie(EinzelKategorie alteKategorie, EinzelKategorie neueKategorie) {
    int zaehler = 0;
		for(int i=0; i<registerListe.size(); i++) {
			Register register = (Register)registerListe.get(i);
			for(int j=0;j<register.getAnzahlBuchungen();j++)
				zaehler += register.getBuchung(j).ersetzeKategorie(alteKategorie, neueKategorie);
		}
		geaendert = true;
		cacheAktuell = false;
		return zaehler;
	}

  // -- Register ----------------------------------------------

	private final ArrayList<Register> registerListe = new ArrayList<Register>();
	
  /**
   * Prüft, ob der Registername schon existiert und hängt ggf. Ziffern an.
   * @param regname gewünschter Name
   * @return generierter Name
   */
  private String generiereRegistername(String regname) {
    String generierterName = regname;
    boolean nameVorhanden;
    int count = 0;
    do {
      nameVorhanden = false;
      for(int i=0; i<registerListe.size(); i++)
        if(generierterName.equalsIgnoreCase(""+registerListe.get(i)))
          nameVorhanden = true;
      if(nameVorhanden)
        generierterName = regname + " (" + ++count + ")";
    } while(nameVorhanden);
    return generierterName;
  }
  
  /**
   * Erzeugt ein Register mit dem angegebenen Namen.
   * Wenn es schon ein Register mit gleichem Namen gibt, wird ein neues Register
   * mit fortlaufender Nummerierung erzeugt.
   * @param regname Name des neuen Registers
   * @return tatsächlich verwendeter Name
   */
  public String erzeugeRegister(String regname) {
    String generierterName = generiereRegistername(regname);
    Register register = new Register(generierterName);
    registerListe.add(register);
    if(DEBUG)
      System.out.println("Register "+generierterName+" erzeugt.");
    geaendert = true;
    return generierterName;
  }

  /**
   * Liefert das passende Register zum angegebenen Namen.
   * @param regname Name des gesuchen Registers
   * @return gesuchtes Register
   */
  private Register findeRegister(String regname) {
    for(int i=0; i<registerListe.size(); i++) {
      if(regname.equals(""+registerListe.get(i)))
        return (Register)registerListe.get(i);
    }
    return null;
  }

  /**
   * Liefert das passende Register zum angegebenen Namen.
   * Wenn das Register noch nicht existiert, wird es erzeugt.
   * @param regname Name des gesuchen Registers
   * @return gesuchtes oder erzeugtes Register 
   */
	public Register findeOderErzeugeRegister(String regname) {
    Register register = findeRegister(regname);
    if(register == null) {
      erzeugeRegister(regname);
      register = findeRegister(regname);
    }
    return register;
  }
  
  /**
   * Umbenennen eines Register.
   * @param alterName
   * @param neuerName
   */
  public String renameRegister(String alterName, String neuerName) {
    Register altesRegister = findeRegister(alterName);
    String generierterName = generiereRegistername(neuerName);
    altesRegister.setName(generierterName);
    geaendert = true;
    return generierterName;
  }
  
  /**
   * Verschiebt ein Register innerhalb der Register-Liste.
   * @param register Register
   * @param indexNeu Neuer Index des Registers
   */
  private void aendereRegisterIndex(Register register, int indexNeu) {
    registerListe.remove(register);
    registerListe.add(indexNeu, register);
    if(DEBUG)
      for(int i=0; i<registerListe.size(); i++)
        System.out.println(""+registerListe.get(i));
  }
  
  /**
   * Verschiebt ein Register innerhalb der Register-Liste.
   * @param regname Name des Registers
   * @param indexNeu Neuer Index des Registers
   */
  public void aendereRegisterIndex(String regname, int indexNeu) {
    Register register = null;
    for(int i=0; i<registerListe.size(); i++) {
      if(regname.equals(""+registerListe.get(i))) {
        register = registerListe.get(i);
      }
    }
    if(register != null)
      aendereRegisterIndex(register, indexNeu);
  }
  
	/**
   * Liefert eine Liste mit alle Register-Namen.
   * Diese wird für die Combo-Boxen zur Auswahl eines Registers benötigt.
	 * @return Array mit den Register-Namen
	 */
  public String[] getRegisterNamen() {
		int anzahl = registerListe.size();
		String[] namen = new String[anzahl];
		for(int i=0; i<anzahl; i++)
			namen[i] = ""+registerListe.get(i);
		return namen;
	}

  /**
   * Überträgt die Buchungen aus dem Quell-Register ins Ziel-Register.
   * Das Quell-Register wird anschließend gelöscht.
   * @param quelle Name des Quell-Registers
   * @param ziel Name des Ziel-Registers
   */
  public void registerVereinigen(String quelle, String ziel) {
    Register quellRegister = findeRegister(quelle);
    Register zielRegister = findeRegister(ziel);
    zielRegister.registerVereinigen(quellRegister);
    registerListe.remove(quellRegister);
    geaendert = true;
  }

	// -- Buchungen ---------------------------------------------

  /**
   * Erzeugt im angegebenen Register eine neue Buchung.
   * Dies ist in der Regel die noch leere Buchung am Ende des Registers.
   * @param regname Name des Registers
   * @param buchung Neue Buchung
   * @return Index an der die neue Buchung in das Register eingefügt wurde
   */
  public int addStandardBuchung(String regname, StandardBuchung buchung) {
    Register register = findeRegister(regname);
    geaendert = true;
		cacheAktuell = false;
    return register.einsortierenBuchung(buchung);
  }

	/**
   * Erzeugt eine Umbuchung und fügt diese im Quell- und Zielregister ein.
	 * @param datum Buchungdatum
	 * @param buchungstext Buchungstext
	 * @param quelle Quellregister
	 * @param ziel Zielregister
	 * @param betrag Betrag
	 */
  public void addUmbuchung(Datum datum, String buchungstext, String quelle, String ziel, Euro betrag) {
    UmbuchungKategorie kategorie = new UmbuchungKategorie(
        findeRegister(quelle),
        findeRegister(ziel)
    );
    new Umbuchung(datum, buchungstext, kategorie, betrag);
    // beim Erzeugen der Umbuchung wird diese automatisch in die beiden Register
    // eingefügt.
    geaendert = true;
  }
  
  /**
   * Liefert die Anzahl der Buchungen im angegebenen Register.
   * @param regname Name des Registers
   * @return Anzahl der Buchungen
   */
  public int getAnzahlBuchungen(String regname) {
    Register register = findeRegister(regname);
    if(register == null) {
      if(DEBUG)
        System.out.println("Datenbasis.getAnzahlBuchungen: "+regname+" gibt es nicht.");
      return 0;
    }
    return register.getAnzahlBuchungen();
  }

  /**
   * Entfernt eine Buchung aus einem Register.
   * @param regname Name des Registers
   * @param index Position der Buchung
   */
  public void entferneBuchung(String regname, int index) {
    Register register = findeRegister(regname);
    register.entferneBuchung(index);
    geaendert = true;
		cacheAktuell = false;
  }
  
  /**
   * Ersetzt eine Buchung durch eine andere.
   * @param regname Name des Registers
   * @param index Position der Buchung
   * @param buchung Neue Buchung
   * @return Einfüge-Position
   */
  public int ersetzeBuchung(String regname, int index, AbstractBuchung buchung) {
		Register register = findeRegister(regname);
		register.entferneBuchung(index);
    geaendert = true;
		cacheAktuell = false;
    return register.einsortierenBuchung(buchung);
  }

  /**
   * Liefert eine Buchung aus einem Register.
   * @param regname Name des Registers
   * @param index Position der Buchung
   */
  public AbstractBuchung getBuchung(String regname, int index) {
    Register register = findeRegister(regname);
    return register.getBuchung(index);
  }

  /**
   * Sortiert die angegebene Buchung neu ins Register ein.
   * Dies wird notwendig, wenn das Datum der Buchung geändert wurde.
   * @param regname Name des Registers
   * @param buchung geänderte Buchung
   * @return Einfüge-Position 
   */
  public int buchungNeusortieren(String regname, AbstractBuchung buchung) {
    Register register = findeRegister(regname);
    return register.buchungNeusortieren(buchung);
  }

	/**
   * Löscht alle Buchungen vor dem angegebenen Datum.
	 * @param datum Datum
	 */
  public void entferneAlteBuchungen(Datum datum) {
		int anzahlRegister = registerListe.size();
		int[] pos = new int[anzahlRegister];
		Euro[] salden = new Euro[anzahlRegister];
		// Erstmal die Salden aller Register ermitteln ...
		for(int i=0; i<anzahlRegister; i++) {
			Register register = (Register)registerListe.get(i);
			pos[i] = -1; // -1 = keine Buchungen löschen
			for(int j=0; j<register.getAnzahlBuchungen();j++)
				if(register.getBuchung(j).compareTo(datum) <= 0)
					pos[i] = j;
			if(pos[i] > -1)
				salden[i] = register.getSaldo(pos[i]);
		}
		// ... und dann ggf. alte Buchungen löschen und Selbstbuchung einfügen.
		for(int i=0; i<anzahlRegister; i++) {
			if(pos[i] > -1) {
				Register register = (Register)registerListe.get(i);
        register.removeBisBuchung(pos[i]);
				new Umbuchung(
            (Datum)datum.clone(),
            res.getString("opening_balance"),
            new UmbuchungKategorie(register, register),
            salden[i]);
				// Umbuchung werden automatisch einsortiert
				if(DEBUG)
				  System.out.println("-I- Im Register "+register+" wurden "+pos[i]+" Buchungen gelöscht!");
			}
		}
    geaendert = true;
		cacheAktuell = false;
	}

  // -- Suchen und Ersetzen -----------------------------------

  private int registerSuchIdx = 0;
  private int buchungSuchIdx = 0;

  /**
   * Beginnt die Suche wieder in der ersten Buchung des ersten Registers.
   *
   */
  public void resetSuchIdx() {
    registerSuchIdx = 0;
    buchungSuchIdx = 0;
  }

  /**
   * Sucht nach dem angegebenen Text in allen Buchungen.
   * @param text gesuchter Text
   * @return Buchung in der der Text gefunden wurde
   */
  public AbstractBuchung suchen(String text, boolean grossUndKlein) {
    for(int i=registerSuchIdx; i<registerListe.size(); i++) {
      Register register = (Register)registerListe.get(i);
      for(int j=buchungSuchIdx; j<register.getAnzahlBuchungen(); j++) {
        AbstractBuchung buchung = register.getBuchung(j);
        if(buchung.sucheText(text, grossUndKlein)) {
          buchungSuchIdx = j+1; // da geht es weiter
          return buchung;
        }
      }
      buchungSuchIdx = 0;
      registerSuchIdx++;
    }
    registerSuchIdx = 0;
    return null;
  }
  
  /**
   * Liefert den Namen des Registers in der der Text gefunden wurde.
   * @return Register-Name
   */
  public String getRegisterGefundenerText() {
    return ""+registerListe.get(registerSuchIdx);
  }

  /**
   * Liefert den Index der Buchung in der der Text gefunden wurde.
   * @return Index der Buchung
   */
  public int getBuchNrGefundenerText() {
    return buchungSuchIdx-1; // der Suchindex ist schon eine Position weiter
  }

  // -- Auswertungen ------------------------------------------
  
  /**
   * Liefert alle Buchungen einer Kategorie mit einem Buchungstext
   * @param buchungstext Buchungstext
   * @param kategorie Kategorie
   * @param unterkategorienVerwenden Die Unterkategorien sollen verwendet werden
   * @return Liste der Buchungen
   */
  public ArrayList<Datensatz> getBuchungen(String buchungstext, EinzelKategorie kategorie, Boolean unterkategorienVerwenden) {
    ArrayList<Datensatz> liste = new ArrayList<Datensatz>();
    for(int i=0; i<registerListe.size(); i++) {
      Register register = registerListe.get(i);
      for(int j=0; j<register.getAnzahlBuchungen(); j++) {
        AbstractBuchung buchung = register.getBuchung(j);
        if((buchung.getText().contains(buchungstext)) &&
            (buchung.getClass() != Umbuchung.class) &&
            ((kategorie == null) || buchung.istInKategorie(kategorie, unterkategorienVerwenden)))
          liste.add(new Datensatz(register, buchung));
      }
    }
    return liste;
  }

  /**
   * Liefert alle Buchungen
   * @return Liste der Buchungen
   */
  
  public ArrayList<String[]> getBuchungen() {
    ArrayList<String[]> liste = new ArrayList<String[]>();
    for(int i=0; i<registerListe.size(); i++) {
      Register register = registerListe.get(i);
      for(int j=0; j<register.getAnzahlBuchungen(); j++) {
        AbstractBuchung buchung = register.getBuchung(j);
        String[] zeile = {
            ""+buchung.getDatum(),
            buchung.getText(),
            ""+buchung.getKategorie(),
            ""+buchung.getWert(),
            ""+register
        };
        liste.add(zeile);
      }
    }
    return liste;
  }
  
  /**
   * Liefert alle Buchungen einer Kategorie in einem Zeitraum.
   * @param zeitraum Zeitraum
   * @param regname Name des Registers
   * @param kategorien Liste mit Kategorien
   * @return Liste der Buchungen
   */
  
  public ArrayList<String[]> getBuchungen(AbstractZeitraum zeitraum, String regname, EinzelKategorie[] kategorien, boolean unterkategorienVerwenden) {
    ArrayList<String[]> liste = new ArrayList<String[]>();
    if(regname == null)
      for(int i=0; i<registerListe.size(); i++)
        getBuchungen(liste, zeitraum, registerListe.get(i), kategorien, unterkategorienVerwenden);
    else
      getBuchungen(liste, zeitraum, findeRegister(regname), kategorien, unterkategorienVerwenden);
    return liste;
  }
  
  private void getBuchungen(ArrayList<String[]> liste, AbstractZeitraum zeitraum, Register register, EinzelKategorie[] kategorien, boolean unterkategorienVerwenden) {
    for(int i=0; i<register.getAnzahlBuchungen(); i++) {
      AbstractBuchung buchung = register.getBuchung(i);
      Datum datum = buchung.getDatum();
      if(datum.istImZeitraum(zeitraum)) {
        for(int j=0; j<kategorien.length; j++) {
         Euro wert = buchung.getKategorieWert(kategorien[j], unterkategorienVerwenden);
         if(!wert.equals(Euro.NULL_EURO)) {
           int anzahl = liste.size();
           int pos = -1;
           for(int k=0; k<anzahl; k++) {
             if(datum.compareTo(new Datum(liste.get(k)[0])) >= 0)
               pos = k;
           }
           String[] zeile = {
               ""+datum,
               buchung.getText(),
               ""+kategorien[j],
               ""+wert
           };
           if(pos == anzahl-1)  // neue Buchung ans Ende
             liste.add(zeile);
           else                 // neue Buchung einfuegen
             liste.add(pos+1, zeile);
         }
        }
      }
    }
  }

  /**
   * Summiert alle Kategorien mit positivem Saldo im angegebenen
   * Register und Zeitraum auf.
   * @param regname Name der Registers, <code>null</code> = alle Register
   * @param zeitraum Zeitraum
   * @return Einnahmen
   */
  public Euro getEinnahmen(AbstractZeitraum zeitraum, String regname) {
    Euro einnahmen = new Euro();
    erneuereKategorieCache(zeitraum, regname, true);
    for(int i=0; i<kategorieListe.size(); i++) {
      Euro wert = ((EinzelKategorie)kategorieListe.get(i)).getSumme();
      if(wert.compareTo(Euro.NULL_EURO) > 0)
        einnahmen.sum(wert);
    }
    return einnahmen;
  }

  /**
   * Summiert alle Kategorien mit negativem Saldo im angegebenen
   * Register und Zeitraum auf.
   * @param regname Name der Registers, <code>null</code> = alle Register
   * @param zeitraum Zeitraum
   * @return Ausgaben
   */
  public Euro getAusgaben(AbstractZeitraum zeitraum, String regname) {
    Euro ausgaben = new Euro();
    erneuereKategorieCache(zeitraum, regname, true);
    for(int i=0; i<kategorieListe.size(); i++) {
      Euro wert = ((EinzelKategorie)kategorieListe.get(i)).getSumme();
      if(wert.compareTo(Euro.NULL_EURO) < 0)
        ausgaben.sum(wert);
    }
    return Euro.NULL_EURO.sub(ausgaben);
  }

	/**
	 * Ermittelt den Saldo an einem bestimmten Datum über alle Register.
	 * @param datum Datum des gesuchten Saldos
	 * @return gesuchter Saldo
	 */
	public Euro getSaldo(Datum datum) {
		Euro saldo = new Euro();
		for(int i=0; i<registerListe.size(); i++) {
			Register register = (Register)registerListe.get(i);
			saldo.sum(register.getSaldo(datum));
		}
		return saldo;
	}

  /**
   * Liefert den Saldo an einer Position im Register.
   * @param regname Name des Registers
   * @param index Position
   */
  public Euro getRegisterSaldo(String regname, int index) {
    Register register = findeRegister(regname);
    return register.getSaldo(index);
  }

	/**
   * Liefert den Saldo an einem bestimmten Datum in einem bestimmten Register.
	 * @param regname Name der Registers
	 * @param datum Datum des gesuchten Saldos
	 * @return gesuchter Saldo
	 */
	public Euro getRegisterSaldo(String regname, Datum datum) {
		Register register = findeRegister(regname);
		return register.getSaldo(datum);
	}

  /**
   * Ermittelt den Saldo über EINE Kategorie in einem bestimmten Register und
   * Zeitraum.
   * @param regname Name des Registers, <code>null</code> = alle Register
   * @param kategorie Name der Kategorie
   * @param zeitraum Zeitraum
   * @return gesuchter Saldo
   */
  public Euro getKategorieSaldo(EinzelKategorie kategorie, AbstractZeitraum zeitraum, String regname, boolean unterkategorienVerwenden) {
    erneuereKategorieCache(zeitraum, regname, unterkategorienVerwenden);
    if(DEBUG)
      System.out.println(""+kategorie+": Saldo @ "+zeitraum+" = "+kategorie.getSumme());
    return kategorie.getSumme();
  }

  /**
   * Liefert die Salden für ALLE Kategorien in einem Zeitraum.
   * @param zeitraum Zeitraum
   * @return Salden aller Kategorien
   */
  public Euro[] getKategorieSalden(AbstractZeitraum zeitraum, boolean unterkategorienVerwenden) {
    erneuereKategorieCache(zeitraum, null, unterkategorienVerwenden);
    if(unterkategorienVerwenden) {
      final int anzahlKategorien = kategorieListe.size();
      Euro[] summen = new Euro[anzahlKategorien];
      for(int i=0; i<anzahlKategorien; i++)
        summen[i] = ((EinzelKategorie)kategorieListe.get(i)).getSumme();
      return summen;
    }
    // Nur die Hauptkategorien:
    return getKategorieSalden(getKategorien(true), zeitraum, null, false);
    // Alle Kategorien werden übergeben, aber nur die Hauptkategorien aufgerufen
  }

  /**
   * Liefert die Salden für BESTIMMTE Kategorien in einem Zeitraum.
   * @param kategorien Kategorien
   * @param zeitraum Zeitraum
   * @param regname Name der Registers, <code>null</code> = alle Register
   * @return Salden der Kategorien
   */
  public Euro[] getKategorieSalden(EinzelKategorie[] kategorien, AbstractZeitraum zeitraum, String regname, boolean unterkategorienVerwenden) {
    erneuereKategorieCache(zeitraum, regname, unterkategorienVerwenden);
    Euro[] summen = new Euro[kategorien.length];
    for(int i=0; i<kategorien.length; i++)
      summen[i] = kategorien[i].getSumme();
    return summen;
  }

  private AbstractZeitraum zeitraumImCache;
  private String registerImCache;
  private boolean cacheAktuell = false;
  private boolean cacheMitUnterkategorien = true;
  public static int cacheHit = 0;
  public static int cacheMiss = 0;
  /**
   * Die Summen der Kategorien werden in einem Cache gehalten,
   * da sich aufeinanderfolgende Abfragen häufig auf die
   * gleichen Daten beziehen.
   * Um zu überprüfen, ob der Cache noch aktuell ist, wird
   * getestet ob 
   * (1) Zeitraum und (2) Registername dem Cache entsprechen
   * (3) der Cache noch aktuell ist und ob
   * (4) die Verwendung der Unterkategorien übereinstimmt
   *
   * @param zeitraum Zeitraum der Abfrage
   * @param regname Register der Abfrage
   * @param unterkategorienVerwenden
   */
  private void erneuereKategorieCache(AbstractZeitraum zeitraum, String regname, boolean unterkategorienVerwenden) {
    if(((zeitraum == zeitraumImCache) || ((zeitraum != null) && zeitraum.equals(zeitraumImCache))) && 
       ((regname == registerImCache) || ((regname != null) && regname.equals(registerImCache))) && 
        (unterkategorienVerwenden == cacheMitUnterkategorien) && cacheAktuell) {
      cacheHit ++;
      return;
    }
    cacheMiss ++;
    zeitraumImCache = zeitraum;
    registerImCache = regname;
    cacheMitUnterkategorien = unterkategorienVerwenden;
    cacheAktuell = true;
    final int anzahlKategorien = kategorieListe.size();
    final int anzahlRegister = registerListe.size();
    for(int i=0; i<anzahlKategorien; i++) {
      EinzelKategorie kategorie = (EinzelKategorie)kategorieListe.get(i);
      kategorie.loescheSumme();
    }
    if(regname == null)
      // = ALLE Register
      for(int i=0; i<anzahlRegister; i++) {
        final Register reg = (Register)registerListe.get(i);
        final int anzahlBuchungen = reg.getAnzahlBuchungen();
        for(int j=0; j<anzahlBuchungen; j++)
          reg.getBuchung(j).bildeKategorieSumme(zeitraum, unterkategorienVerwenden);
      }
    else {
      final Register register = findeRegister(regname);
      final int anzahlBuchungen = register.getAnzahlBuchungen();
      for(int j=0; j<anzahlBuchungen; j++)
        register.getBuchung(j).bildeKategorieSumme(zeitraum, unterkategorienVerwenden);
    }
  }
  
  // -- gemerkte Buchungen ------------------------------------

  private Datum startDatumGemerkteBuchungen = new Datum();
	private final ArrayList<AbstractBuchung> gemerkteBuchungen = new ArrayList<AbstractBuchung>();
	private final ArrayList<String> gemerkteBuchungenText = new ArrayList<String>();

  /**
   * Setzt das Start-Datum, ab dem Buchungen gemerkt werden.
   * @param datum Start-Datum
   */
  public void setStartDatum(Datum datum) {
    startDatumGemerkteBuchungen = datum;
    if(DEBUG)
      System.out.println("Neues Startdatum: "+datum);
  }
  
	/**
	 * Merkt sich die Standard- und SplitBuchungen. Falls es den Buchungstext schon
	 * gibt wird die zuvor gemerkte Buchung überschrieben.
	 * @param buchung Buchung zum Merken
	 */
	public void buchungMerken(AbstractBuchung buchung) {
		if(buchung.getClass() == Umbuchung.class)
		  return;
		// normale Buchungen merken (keine Umbuchungen!)
    if((startDatumGemerkteBuchungen.compareTo(buchung.getDatum()) > 0))
      return;
    // Buchung war zu alt zum Merken :-)
    int pos = Collections.binarySearch(gemerkteBuchungenText, buchung.getText());
		if (pos < 0) { // so eine Buchung gibt es noch nicht: Einfuegen
			gemerkteBuchungen.add(-pos-1, buchung);
			gemerkteBuchungenText.add(-pos-1, buchung.getText());
		}
		else {         // Buchung gibt es schon: Ueberschreiben
			gemerkteBuchungen.set(pos, buchung);
			gemerkteBuchungenText.set(pos, buchung.getText());
		}
    if(DEBUG) {
      System.out.println("-- Gemerkte Buchungen:");
      for(int i=0; i<gemerkteBuchungen.size(); i++) {
        AbstractBuchung dumpBuchung = gemerkteBuchungen.get(i); 
        System.out.println(""+dumpBuchung.getText()+" / "+dumpBuchung.getKategorie());
      }
    }
	}

  /**
   * Liefert eine gemerkte Buchung, die mit dem angegebenen String beginnt.
   * @param prefix Anfang des gesuchten Buchungstextes
   * @return gefundene Buchung oder <code>null</code>, wenn nichts gefunden
   */
	public AbstractBuchung findeGemerkteBuchung(String prefix) {
		for(int i=0; i<gemerkteBuchungenText.size(); i++)
			if(gemerkteBuchungenText.get(i).startsWith(prefix))
				return gemerkteBuchungen.get(i);
		return null;
	}

  // -- Auto-Buchung ------------------------------------------

  static private final String[] legacyIntervallNamen = {
    "Woche",
    "Monat",
    "Quartal",
    "Halbjahr",
    "Jahr"
  };
  private final ArrayList<StandardBuchung> autoStandardBuchungen = new ArrayList<StandardBuchung>();
  private final ArrayList<Register> autoStandardBuchungRegister = new ArrayList<Register>();
  private final ArrayList<Integer> autoStandardBuchungIntervalle = new ArrayList<Integer>();
  private final ArrayList<Umbuchung> autoUmbuchungen = new ArrayList<Umbuchung>();
  private final ArrayList<UmbuchungKategorie> autoUmbuchungRegister = new ArrayList<UmbuchungKategorie>();
  private final ArrayList<Integer> autoUmbuchungIntervalle = new ArrayList<Integer>();

  private Integer getLegacyIntervallIndex(String name) {
    for(int i=0;i<legacyIntervallNamen.length;i++)
      if(name.equals(legacyIntervallNamen[i]))
        return new Integer(i);
    return new Integer(0);
  }

  /**
   * Liefert die Anzahl der vorhandenen wiederkehrenden Standard-Buchungen.
   * @return Anzahl
   */
  public int getAnzahlAutoStandardBuchungen() {
    return autoStandardBuchungen.size();
  }

  /**
   * Liefert die Anzahl der vorhandenen wiederkehrenden Umbuchungen.
   * @return Anzahl
   */
  public int getAnzahlAutoUmbuchungen() {
    return autoUmbuchungen.size();
  }

  /**
   * Liefert eine wiederkehrende Standard-Buchung.
   * @param index Nummer der Buchung
   * @return Buchung
   */
  public StandardBuchung getAutoStandardBuchung(int index) {
    return autoStandardBuchungen.get(index);
  }

  /**
   * Liefert eine wiederkehrende Umuchung.
   * @param index Nummer der Buchung
   * @return Buchung
   */
  public Umbuchung getAutoUmbuchung(int index) {
    return autoUmbuchungen.get(index);
  }

  /**
   * Liefert das Register zu einer wiederkehrenden Standard-Buchung.
   * @param index Nummer der Buchung
   * @return Name des Registers
   */
  public String getAutoStandardBuchungRegister(int index) {
    return ""+autoStandardBuchungRegister.get(index);
  }

  /**
   * Liefert die Register zu einer wiederkehrenden Umbuchung.
   * @param index Nummer der Buchung
   * @return Register-Paar
   */
  public UmbuchungKategorie getAutoUmbuchungRegister(int index) {
    return autoUmbuchungRegister.get(index);
  }

  /**
   * Setzt das Register zu einer wiederkehrenden Standard-Buchung.
   * @param index Nummer der Buchung
   * @param register Name des Registers
   */
  public void setAutoStandardBuchungRegister(int index, String register) {
    autoStandardBuchungRegister.set(index, findeRegister(register));
    geaendert = true;
  }

  /**
   * Setzt das Register zu einer wiederkehrenden Umbuchung.
   * @param index Nummer der Buchung
   * @param register Register-Paar
   */
  public void setAutoUmbuchungRegister(int index, UmbuchungKategorie register) {
    autoUmbuchungRegister.set(index, register);
    geaendert = true;
  }

  /**
   * Liefert den Index des Intervalls (Woche, Monat, Quartal, Halbjahr, Jahr) 
   * zu einer wiederkehrenden Standard-Buchung.
   * @param index Nummer der Buchung
   * @return Name des Intervall
   */
  public Integer getAutoStandardBuchungIntervall(int index) {
    return autoStandardBuchungIntervalle.get(index);
  }

  /**
   * Liefert den Index des Intervalls (Woche, Monat, Quartal, Halbjahr, Jahr) 
   * zu einer wiederkehrenden Umuchung.
   * @param index Nummer der Buchung
   * @return Name des Intervall
   */
  public Integer getAutoUmbuchungIntervall(int index) {
    return autoUmbuchungIntervalle.get(index);
  }

  /**
   * Setzt das Intervall (Monat, Quartal, Halbjahr, Jahr) 
   * zu einer wiederkehrenden Standard-Buchung.
   * @param index Nummer der Buchung
   * @param intervallIndex Index des Intervalls
   */
  public void setAutoStandardBuchungIntervall(int index, Integer intervallIndex) {
    autoStandardBuchungIntervalle.set(index, intervallIndex);
    geaendert = true;
  }

  /**
   * Setzt das Intervall (Monat, Quartal, Halbjahr, Jahr) 
   * zu einer wiederkehrenden Umbuchung.
   * @param index Nummer der Buchung
   * @param intervallIndex Index des Intervalls
   */
  public void setAutoUmbuchungIntervall(int index, Integer intervallIndex) {
    autoUmbuchungIntervalle.set(index, intervallIndex);
    geaendert = true;
  }

  /**
   * Erzeugt eine neue wiederkehrenden Standard-Buchung.
   * Dies geschied, wenn in der letzten Zeile der Tabelle eine Eingabe erfolgt.
   */
  public void addAutoStandardBuchung() {
    autoStandardBuchungen.add(new StandardBuchung());
    autoStandardBuchungRegister.add(registerListe.get(0));
    autoStandardBuchungIntervalle.add(new Integer(0));
    geaendert = true;
  }

  /**
   * Erzeugt eine neue wiederkehrenden Umbuchung.
   * Dies geschied, wenn in der letzten Zeile der Tabelle eine Eingabe erfolgt.
   */
  public void addAutoUmbuchung() {
    // Erzeugen einer Dummy-Umbuchung; Quell- und Ziel-Register
    // sind das erste Register der Liste
    // Problem: Automatisches Einfügen der Umbuchung ==> getrennte Speicherung des Register-Paars
    autoUmbuchungen.add(new Umbuchung());
    autoUmbuchungRegister.add(new UmbuchungKategorie(registerListe.get(0), registerListe.get(0)));
    autoUmbuchungIntervalle.add(new Integer(0));
    geaendert = true;
  }

  /**
   * Löscht eine wiederkehrende Standard-Buchung.
   * @param index Nummer der Buchung
   */
  public void entferneAutoStandardBuchung(int index) {
    if(index < autoStandardBuchungen.size()) {
      autoStandardBuchungen.remove(index);
      autoStandardBuchungRegister.remove(index);
      autoStandardBuchungIntervalle.remove(index);
      geaendert = true;
    }
  }

  /**
   * Löscht eine wiederkehrende Umbuchung.
   * @param index Nummer der Buchung
   */
  public void entferneAutoUmbuchung(int index) {
    if(index < autoUmbuchungen.size()) {
      autoUmbuchungen.remove(index);
      autoUmbuchungIntervalle.remove(index);
      geaendert = true;
    }
  }

	/**
	 * Fügt die fälligen wiederkehrenden Buchungen in das entsprechende Register ein.
   * @param datum Datum bis zum die Buchungen ausgeführt werden sollen
	 * @return Anzahl der eingefügten Buchungen
	 */
	public int ausfuehrenAutoBuchungen(Datum datum) {
	  int zaehler = 0;
    // 1. Standard-Buchungen:
    for(int i=0; i<autoStandardBuchungen.size(); i++) {
      StandardBuchung buchung = autoStandardBuchungen.get(i);
      Register register = autoStandardBuchungRegister.get(i);
      while(buchung.getDatum().compareTo(datum) <= 0) {
        register.einsortierenBuchung((StandardBuchung)buchung.clone());
        if(DEBUG)
          System.out.println("AutoBuchung ausgeführt: "+buchung.getText());
        Integer intervall = autoStandardBuchungIntervalle.get(i);
        buchung.setDatum(getFolgeDatum(buchung.getDatum(), intervall));
        autoStandardBuchungen.set(i, buchung);
        zaehler++;
      }
    }
    
    // 2. Umbuchungen:
    for(int i=0; i<autoUmbuchungen.size(); i++) {
      Umbuchung buchung = autoUmbuchungen.get(i);
      UmbuchungKategorie umbuchungKategorie = autoUmbuchungRegister.get(i); 
      while(buchung.getDatum().compareTo(datum) <= 0) {
        Umbuchung neueUmbuchung = (Umbuchung) buchung.clone();
        neueUmbuchung.setKategorie((UmbuchungKategorie) umbuchungKategorie.clone());
        // das Einsortieren ist damit automatisch erfolgt
        if(DEBUG)
          System.out.println("AutoBuchung ausgeführt: "+buchung.getText());
        Integer intervall = autoUmbuchungIntervalle.get(i);
        buchung.setDatum(getFolgeDatum(buchung.getDatum(), intervall));
        zaehler++;
      }
    }

    if(zaehler > 0) {
			cacheAktuell = false;
		}			 
	  return zaehler;
	}

  /**
   * Berechnet das nächste Datum an dem die wiederkehrende Buchung ausgeführt
   * werden soll.
   * @param datum altes Datum
   * @param intervall Index des Intervalls in den die Buchung ausgeführt wird
   * @return Neues Datum
   */
  static private Datum getFolgeDatum(Datum datum, Integer intervall) {
    int tag = datum.getTag();
    int monat = datum.getMonat();
    int jahr = datum.getJahr();
    switch(intervall) {
    case 0: tag += 7;   break; // Woche
    case 1: monat += 1; break; // Monat
    case 2: monat += 3; break; // Quartal
    case 3: monat += 6; break; // Halbjahr
    default: jahr += 1; break; // Jahr
    }
      
    if (monat > 12) {
      monat -= 12;
      jahr ++;
    }
    // der mögliche Überlauf beim Tag wird in der Klasse
    // 'Datum' korrigiert
    return new Datum(tag, monat, jahr);
  }

  // -- Import ----------------------------------------------------------------
  
	/**
	 * Importiert eine CSV-Datei (Comma Separated Value) in das angegebene Register.
	 * Die übergebene Tabelle muss (mindestens) 4 Spalten besitzen.
	 * @param regname Name des Registers
	 * @param tabelle Tabelle
	 */
	public void importBuchungen(String regname, String[][] tabelle) {
		Register register = findeRegister(regname);
	  for(int i=0; i<tabelle.length; i++) {
	    Datum datum = new Datum(tabelle[i][0]);
	    String text = tabelle[i][1];
	    EinzelKategorie kategorie = findeOderErzeugeKategorie(tabelle[i][2]);
	    Euro wert = new Euro(tabelle[i][3]);
	    AbstractBuchung buchung = new StandardBuchung(datum, text, kategorie, wert);
	    register.einsortierenBuchung(buchung);
      geaendert = true;
			cacheAktuell = false;
	  }
	}
	
	/**
	 * Importiert eine Quicken-Export-Datei (QIF) in das angegebene Register.
	 * @param in Eingabe-Stream
	 * @param regname Name des Registers
	 * @param dm <code>true</code> wenn die Währung der Daten D-Mark ist.
	 */
	public void importQuickenRegister(InputStream in, String regname, boolean dm) throws IOException, QuickenImportException {
	  final Register register = findeRegister(regname);
	  final String typ = leseQIFZeile(in);
	  if(DEBUG)
	    System.out.println("-I- QuickenImport: "+typ);
	  int c;
	  while((c = in.read()) != -1) { // EOF erreicht
	    // Datum einlesen
	    if(c != 'D')
	      throw new QuickenImportException("-E- QuickenImport: Ups! D (Datum) erwartet.");
	    StringTokenizer st = new StringTokenizer(leseQIFZeile(in), ".");
	    String monat = st.nextToken();
	    if(monat.length() == 1)
	      monat = "0" + monat;
	    String tag = st.nextToken();
	    if(tag.length() == 1)
	      tag = "0" + tag;
	    String jahr = st.nextToken();
      // TODO Import überarbeiten: Funktioniert nur mit deutschem Datum!
	    Datum datum = new Datum(tag+"."+monat+"."+jahr);
	    
	    // Betrag einlesen
	    if(in.read() != 'U')
	      throw new QuickenImportException("-E- QuickenImport: Ups! U (Betrag) erwartet.");
	    Euro einzelWert = new Euro(leseQIFZeile(in).replace('.',',')); // US -> deutsch
	    if(in.read() != 'T')
	      throw new QuickenImportException("-E- QuickenImport: Ups! T (Betrag) erwartet.");
	    if(!einzelWert.equals(new Euro(leseQIFZeile(in).replace('.',','))))
	      throw new QuickenImportException("-E- QuickenImport: Ups! U und T Betrag ungleich.");
	    if(dm)
	      einzelWert.umrechnenVonDM();
	    
	    // Abgeglichene Buchung "X" einlesen
	    c = in.read();
	    if(c == 'C') {
	      leseQIFZeile(in);
	      c = in.read();
	    }
	    
	    // Buchungstext einlesen
	    String text = "*";
	    if(c == 'P') {
	      text = leseQIFZeile(in);
	      c = in.read();
	    }
	    
	    // Kategorie einlesen
	    String einzelKategorie;
	    if(c == 'L') {
	      einzelKategorie = leseQIFZeile(in);
	      c = in.read();
	    }
	    else
	      einzelKategorie = ""+EinzelKategorie.SONSTIGES;
	    
	    // Split-Buchungen einlesen
	    StandardBuchung standardBuchung = null;
	    SplitBuchung splitBuchung = null;
	    while(c == 'S') {
	      String kategorie = leseQIFZeile(in);
	      if(in.read() != '$')
	        throw new QuickenImportException("-E- QuickenImport: Ups! $ (Betrag-Splitbuchung) erwartet.");
	      Euro wert = new Euro(leseQIFZeile(in).replace('.',',')); // US -> deutsch
	      if(dm)
	        wert.umrechnenVonDM();
	      // Prüfen, ob Umbuchung:
	      if((kategorie.length() > 2) &&
	          kategorie.startsWith("[") &&
	          kategorie.endsWith("]")) {
	        if(wert.compareTo(Euro.NULL_EURO) > 0) {
	          new Umbuchung(
                datum, 
                text, 
                new UmbuchungKategorie(
                    findeOderErzeugeRegister(kategorie), 
                    register), 
                wert);
	          // Nur positive Umbuchungen erzeugen; Umbuchungen werden automatisch eingefügt
	          if(DEBUG)
	            System.out.println("Umbuchung "+text+" erzeugt.");
	        }
	      }
	      else { // keine Umbuchung:
	        if(standardBuchung == null) 
	          standardBuchung = new StandardBuchung(datum, text, findeOderErzeugeKategorie(kategorie), wert);
	        else if(splitBuchung == null) {
	          splitBuchung = new SplitBuchung(standardBuchung);
	          splitBuchung.add(findeOderErzeugeKategorie(kategorie), wert);
	        }
	        else
	          splitBuchung.add(findeOderErzeugeKategorie(kategorie), wert);
	      }
	      c = in.read();
	    } /* Ende while Split-Buchung*/
	    
	    if(c != '^')
	      throw new QuickenImportException("-E- QuickenImport: Ups! ^ (Datensatzende) erwartet.");
	    leseQIFZeile(in); // CR+LF von '^' lesen
	    
	    if(splitBuchung != null) {
	      register.einsortierenBuchung(splitBuchung);
        if(DEBUG)
          System.out.println("SplitBuchung erzeugt.");
	    }
	    else if(standardBuchung != null) {
	      register.einsortierenBuchung(standardBuchung);
        if(DEBUG)
          System.out.println("StandardBuchung erzeugt.");
	    }
	    else if((einzelKategorie.length() > 2) &&    // Pruefen ob Umbuchung
	        einzelKategorie.startsWith("[") &&
	        einzelKategorie.endsWith("]")) {
	      if(einzelWert.compareTo(Euro.NULL_EURO) > 0) {
	        new Umbuchung(
              datum, 
              text, 
              new UmbuchungKategorie(
                  findeOderErzeugeRegister(einzelKategorie),
                  register), 
              einzelWert
          );
	        // Nur positive Umbuchungen erzeugen; Umbuchungen werden automatisch eingefügt
          if(DEBUG)
            System.out.println("Umbuchung "+text+" erzeugt.");
	      }
	    }
	    else {
	      register.einsortierenBuchung(new StandardBuchung(datum, text, findeOderErzeugeKategorie(einzelKategorie), einzelWert));
        if(DEBUG)
          System.out.println("StandardBuchung "+text+" erzeugt.");
	    }
	  } /* Ende while Datei einlesen*/

		cacheAktuell = false;
    geaendert = true;
	}

	public static class QuickenImportException
  extends Exception {
    private static final long serialVersionUID = 1L;

    public QuickenImportException(String text) {
      super(text);
    }
  }
    
	/**
	 * Liest eine einzelne Zeile aus der QIF-Datei (Quicken).
	 * @param in Eingabe-Stream
	 * @return gelesende Zeile
	 * @throws IOException
	 * @throws QuickenImportException
	 */
	private String leseQIFZeile(InputStream in)
		throws IOException, QuickenImportException {
		String zeile = "";
		int c;
		while((c = in.read()) != 13) { // Bis zum Zeilenende (CR)
			if(c == -1) {
				throw new QuickenImportException("-E- QuickenImport: Ups! Unerwartetes EOF.");
			}
			if(c != ',') // Kommata werden gefiltert; Problem beim Float-Parsen
				zeile = zeile + (char)c;
		}
		if((c = in.read()) != 10) {
			throw new QuickenImportException("-E- QuickenImport: Ups! Nach CR kam kein LF. "+(char)c);
		}
		return zeile;
	}

	// -- E/A-Funktionen -------------------------------------------------------
 
 /**
  * Lädt die Buchungen in die Register. Lädt und führt die 
  * wiederkehrenden Buchungen.
  */
 public void laden(DataInputStream in, String versionInfo) throws IOException {
	 // Register laden
	 int anzahl = in.readInt();
	 for(int i=0; i<anzahl; i++) {
		 Register register = findeOderErzeugeRegister(in.readUTF());
     // Das Register kann schon zuvor (beim Laden eines anderen Registers) erzeugt
     // worden sein, deshalb muss der Index des Registers explizit gesetzt werden:
     aendereRegisterIndex(register, i);
     register.laden(in, this);
	 }
 	 cacheAktuell = false;
	 if(DEBUG)
	   System.out.println(""+anzahl+" Register geladen.");

	 // automatische Buchungen laden und ausführen (in v1.0 noch unbekannt!)
	 if(!versionInfo.equals("jHaushalt1.0")) {
		 int size = in.readInt();
		 autoStandardBuchungen.ensureCapacity(size);
		 for(int i=0; i<size; i++) {
			 // Buchung laden:
			 String typ = in.readUTF();
       if(typ.equals("Umbuchung")) {
         Umbuchung buchung = new Umbuchung();
         buchung.laden(in, this, null);
         // Register laden:
         Register quellRegister = findeOderErzeugeRegister(in.readUTF());
         Register zielRegister = findeOderErzeugeRegister(in.readUTF());
         UmbuchungKategorie registerPaar = new UmbuchungKategorie(quellRegister, zielRegister);
         // Intervall laden:
         Integer zeitraum = getLegacyIntervallIndex(in.readUTF());
  
         // Umbuchung einsortieren
         int anz = autoUmbuchungen.size();
         int pos = -1;
         for(int j=0; j<anz; j++)
           if(buchung.compareTo(autoUmbuchungen.get(j)) >= 0)
             pos = j;
         if(pos == anz-1) {  // ans Ende
           autoUmbuchungen.add(buchung);
           autoUmbuchungRegister.add(registerPaar);
           autoUmbuchungIntervalle.add(zeitraum);
         }
         else {              // neue Buchung einfuegen
           autoUmbuchungen.add(pos+1, buchung);
           autoUmbuchungRegister.add(pos+1, registerPaar);
           autoUmbuchungIntervalle.add(pos+1, zeitraum);
         }
       }
       else if(!typ.equals("StandardBuchung") && !typ.equals("StandardBuchung2"))
         throw new IOException("AutoBuchung: Falscher Buchungstyp: "+typ);
       else {
         StandardBuchung buchung;
  			 if(typ.equals("StandardBuchung")) { // Laden des Legacy-Formats
  				 Datum datum = new Datum();
  				 datum.laden(in);
  				 String text = in.readUTF();
  				 int anz = in.readInt();
  				 if(anz == 1) {
  					 EinzelKategorie kategorie = findeOderErzeugeKategorie(in.readUTF());
  					 Euro betrag = new Euro();
  					 betrag.laden(in);
  					 buchung = new StandardBuchung(datum, text, kategorie, betrag);
  				 }
  				 else
  					 throw new IOException("AutoBuchung: Falscher Buchungstyp: SplitBuchung");
  			 }
         else { // Laden des aktuellen Formats für Standard-Buchungen
  				 buchung = new StandardBuchung();
  				 ((StandardBuchung)buchung).laden(in, this);
         }
        
  			 // Register laden:
  			 Register register = findeOderErzeugeRegister(in.readUTF());
  			 // Intervall laden:
  			 Integer zeitraum = getLegacyIntervallIndex(in.readUTF());
  
         // Buchung einsortieren
         int anz = autoStandardBuchungen.size();
         int pos = -1;
         for(int j=0; j<anz; j++)
           if(buchung.compareTo(autoStandardBuchungen.get(j)) >= 0)
             pos = j;
         if(pos == anz-1) {  // ans Ende
           autoStandardBuchungen.add(buchung);
           autoStandardBuchungRegister.add(register);
           autoStandardBuchungIntervalle.add(zeitraum);
         }
         else {              // neue Buchung einfuegen
           autoStandardBuchungen.add(pos+1, buchung);
           autoStandardBuchungRegister.add(pos+1, register);
           autoStandardBuchungIntervalle.add(pos+1, zeitraum);
         }
  		 }
     }
	 }
   geaendert = false;
  }

  /**
   * Speichert die Register mit den Buchungen und die wiederkehrenden Buchung.
   * @param out Ausgabe-Stream
   * @throws IOException
   */
  public void speichern(DataOutputStream out)
	  throws IOException {
    // 1. Versionsinfo:
    out.writeUTF("jHaushalt"+VERSION_DATENBASIS);
    
    // 2. Buchungen (Kategorien werden NICHT gespeichert)
	  out.writeInt(registerListe.size());
	  for(int i=0; i<registerListe.size(); i++) {
      Register register = (Register)registerListe.get(i);
      register.speichern(out);
    }
    
    // 3. automatische Buchungen
    out.writeInt(autoStandardBuchungen.size()+autoUmbuchungen.size());
    
    // 3a. automatische Standard-Buchungen
    for(int i=0; i<autoStandardBuchungen.size(); i++) {
      AbstractBuchung buchung = (AbstractBuchung)autoStandardBuchungen.get(i);
      buchung.speichern(out);
      out.writeUTF(""+autoStandardBuchungRegister.get(i));
      out.writeUTF(legacyIntervallNamen[autoStandardBuchungIntervalle.get(i)]);
    }

    // 3b. automatische Umbuchungen
    for(int i=0; i<autoUmbuchungen.size(); i++) {
      AbstractBuchung buchung = (AbstractBuchung)autoUmbuchungen.get(i);
      buchung.speichern(out);
      UmbuchungKategorie registerPaar = autoUmbuchungRegister.get(i);
      out.writeUTF(""+registerPaar.getQuelle());
      out.writeUTF(""+registerPaar.getZiel());
      out.writeUTF(legacyIntervallNamen[autoUmbuchungIntervalle.get(i)]);
    }
    
	  out.flush();
	  geaendert = false;
  }

}
