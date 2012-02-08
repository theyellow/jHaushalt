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
along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.


(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

package haushalt.daten;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Die Register dienen als Container für die Buchung. Beispiele
 * für Register sind "Girokonto" oder "Bargeld".
 * @author Dr. Lars H. Hahn
 * @version 2.1.3/2006.06.21
 */

/*
 * 2006.06.21 Verbesserung der Performanz beim Einsortieren
 *            durch die Annahme, dass meistens an das Ende des
 *            Registers eingefügt werden muss
 * 2006.06.21 BugFix: Einsortieren war doch notwendig, da die
 *            Umbuchungen tw. schon vorzeitig ins Register
 *            geladen werden 
 * 2006.06.19 Verbesserung der Performanz durch Verzichten des
 *            "Einsortierens" der Buchungen beim Laden 
 * 2006.02.14 BugFix: Abfangen des Löschens der "letzten Zeile"
 * 2006.02.10 Implementierung des Interface 'Comparable'
 * 2006.01.31 Neusortieren einer Buchung jetzt unter Angabe 
 *            der Buchung selbst
 */

public class Register implements Comparable<Register> {
	private static final boolean DEBUG = false;

	private String name;
	private final ArrayList<AbstractBuchung> buchungen;

	public Register(String name) {
		this.name = name;
    buchungen = new ArrayList<AbstractBuchung>();
  }

	public String toString() {
		return name;
	}
  
  public void setName(String neuerName) {
    name = neuerName;
  }

	/**
	 * Liefert die Anzahl der im Register gespeicherten Buchungen.
	 * @return Anzahl der Buchungen
	 */
	public int getAnzahlBuchungen() {
		return buchungen.size();
	}
	
	/**
	 * Liefert die Buchung an der angegebenen Position.
	 * @param nr Position der gesuchten Buchung
	 * @return gesuchte Buchung
	 */
	public AbstractBuchung getBuchung(int nr) {
	  if(nr == getAnzahlBuchungen())
	    return new StandardBuchung();
		return buchungen.get(nr);
	}
	
	/**
	 * Wird benötigt um alte Buchungen zu löschen.
	 * @param nr Position bis zu der Buchungen gelöscht werden
	 */
	public void removeBisBuchung(int nr) {
		for(int i=0;i<=nr;i++)
			buchungen.remove(0);
		// mit jedem remove verändert sich der Index -> immer erstes Element löschen
	}

	/**
	 * Löscht eine Buchung.
	 * @param nr
	 */
	public void entferneBuchung(int nr) {
    if(nr == buchungen.size())
      return; // wenn eine neue (Split-)Buchung eingefügt
              // werden soll, kann keine alte Buchung gelöscht
              // werden :-)
    AbstractBuchung buchung = buchungen.get(nr);
		if(buchung.getClass() == Umbuchung.class) {
			// Die Buchung muss auch aus dem Partner-Register entfernt werden.
      UmbuchungKategorie kategorie = (UmbuchungKategorie) buchung.getKategorie();
      kategorie.getPartnerRegister(this).buchungen.remove(buchung);
		}
    buchungen.remove(buchung);
	}
	
	/**
	 * Entfernt eine Umbuchung. Hiermit kann eine Umbuchung bei Änderung seines
	 * Quell- oder Zielregisters sich aus dem alten Register entfernen.
	 * @param buchung
	 */
	public void loescheUmbuchung(Umbuchung buchung) {
		buchungen.remove(buchung);
	}
  
  /**
   * Sortiert die übergebene Buchung in das Register ein.
   * Es wird aufsteigend nach Buchungsdatum sortiert.
   * @param buchung einzusortierende Buchung
   * @return Einfüge-Position 
   */
  public int einsortierenBuchung(AbstractBuchung buchung) {
    int size = buchungen.size();
    int pos = 0;
    for(int i=size-1; i>=0; i--)
      if(buchung.compareTo(buchungen.get(i)) > 0) {
        pos = i+1;
        i = 0;
      }
    if(pos == size) {  // ans Ende
      buchungen.add(buchung);
    }
    else {              // neue Buchung einfuegen
      buchungen.add(pos, buchung);
    }
		if(DEBUG)
			System.out.println("Register "+name+": Buchung "+buchung.getDatum()+"/"+buchung.getText()+"/"+buchung.getWert()+" an Positon "+(pos+1)+" einsortiert.");
		return pos;
  }
  
  /**
   * Sortiert die angegebene Buchung neu ein.
   * Dies wird notwendig, wenn das Datum der Buchung geändert
   * wurde.
   * @param buchung geänderte Buchung
   * @return Einfüge-Position 
   */
  public int buchungNeusortieren(AbstractBuchung buchung) {
    buchungen.remove(buchung);
    return einsortierenBuchung(buchung);
  }

	/**
	 * Fügt die Buchungen aus einem anderen Register diesem 
   * Register hinzu und löscht sie dann.
	 * @param registerZumLoeschen Register aus dem die Buchungen übernommen werden
	 */
	public void registerVereinigen(Register registerZumLoeschen) {
			while(registerZumLoeschen.getAnzahlBuchungen() > 0) {
				if(registerZumLoeschen.buchungen.get(0).getClass() == Umbuchung.class) {
					Umbuchung umbuchung = (Umbuchung)registerZumLoeschen.buchungen.get(0);
          UmbuchungKategorie alteKategorie = (UmbuchungKategorie) umbuchung.getKategorie();
					
					if(alteKategorie.isSelbstbuchung()) {
					  // Umbuchung: alte Selbstbuchung
						umbuchung.setKategorie(new UmbuchungKategorie(this, this));
						// -> automatisch löschen und neu einfügen
					}
					else {
					  // normale Umbuchung
            Register neueQuelle;
            Register neuesZiel;
            if(alteKategorie.getQuelle() == registerZumLoeschen)
              neueQuelle = this;
            else
              neueQuelle = alteKategorie.getQuelle();
            if(alteKategorie.getZiel() == registerZumLoeschen)
              neuesZiel = this;
            else
              neuesZiel = alteKategorie.getZiel();
						if(neueQuelle != neuesZiel)
							umbuchung.setKategorie(new UmbuchungKategorie(neueQuelle, neuesZiel));
							// nur automatisch (!) einsortieren, wenn keine Selbstbuchung entstanden ist
						else {
						  // sonst loeschen:
						  neueQuelle.loescheUmbuchung(umbuchung);
							registerZumLoeschen.buchungen.remove(0);
						}
					}
				}
				else { 
				  // StandardBuchung + SplitBuchung
					einsortierenBuchung((AbstractBuchung)registerZumLoeschen.buchungen.get(0));
					registerZumLoeschen.buchungen.remove(0);
				}
			}
		}

		/**
		 * Liefert den Saldo des Registers bis zum angegebenen Datum (exklusiv).
		 * @param datum
		 * @return Saldo
		 */
		public Euro getSaldo(Datum datum) {
			Euro saldo = new Euro();
			int i = 0;
			while((i < buchungen.size()) &&
				(datum.compareTo(getBuchung(i).getDatum()) > 0)) {
				if(getBuchung(i).getClass() == Umbuchung.class) {
          UmbuchungKategorie kategorie = (UmbuchungKategorie) getBuchung(i).getKategorie();
					if((kategorie.getQuelle() == this) && !kategorie.isSelbstbuchung())
  					saldo = saldo.sub(getBuchung(i).getWert());
	  				// Falls dieses Register Quelle einer Umbuchung ist: Subtrahieren!
					  // Selbstbuchugen aber nicht!
	  			else
	  			  saldo.sum(getBuchung(i).getWert());
				}
				else { // StandardBuchung + SplitBuchung
					saldo.sum(getBuchung(i).getWert());
				}
				i++;
			}
			return saldo;
		}

		/**
		 * Liefert den Saldo des Registers bis zur angegebenen Zeile.
		 * @param row
		 * @return Saldo
		 */
		public Euro getSaldo(int row) {
			Euro saldo = new Euro();
			for(int i=0; i<=row; i++)
				if(getBuchung(i).getClass() == Umbuchung.class) {
          UmbuchungKategorie kategorie = (UmbuchungKategorie) getBuchung(i).getKategorie();
          if((kategorie.getQuelle() == this) && !kategorie.isSelbstbuchung())
						saldo = saldo.sub(getBuchung(i).getWert());
						// Falls dieses Register Quelle einer Umbuchung ist: Subtrahieren!
						// Selbstbuchugen aber nicht!
					else
					  saldo.sum(getBuchung(i).getWert());
				}
				else { // StandardBuchung + SplitBuchung
					saldo.sum(getBuchung(i).getWert());
				}
			return saldo;
		}

    
    // -- E/A-Funktionen -------------------------------------------------------
    
		public void laden(DataInputStream in, Datenbasis db) throws IOException {
			int size = in.readInt();
			buchungen.ensureCapacity(size);
			for(int i=0; i<size; i++) {
				String typ = in.readUTF();
				if(typ.equals("Umbuchung")) {
					Umbuchung umbuchung = new Umbuchung();
					umbuchung.laden(in, db, this);
				}
				else if(typ.equals("StandardBuchung2")) {
					StandardBuchung standardBuchung = new StandardBuchung();
					standardBuchung.laden(in, db);
					einsortierenBuchung(standardBuchung);
					db.buchungMerken(standardBuchung);
				}
				else if(typ.equals("StandardBuchung")) {
					Datum datum = new Datum();
					datum.laden(in);
					String text = in.readUTF();
					int anz = in.readInt();
					if(anz == 1) {
						EinzelKategorie kategorie = db.findeOderErzeugeKategorie(in.readUTF());
						Euro betrag = new Euro();
						betrag.laden(in);
						StandardBuchung standardBuchung = new StandardBuchung(datum, text, kategorie, betrag); 
						einsortierenBuchung(standardBuchung);
						db.buchungMerken(standardBuchung);
					}
					else {
						SplitBuchung buchung = new SplitBuchung(datum, text);
						for(int j=0; j<anz; j++) {
							String kategorie = in.readUTF();
							Euro betrag = new Euro();
							betrag.laden(in);
							buchung.add(db.findeOderErzeugeKategorie(kategorie), betrag);
						}
						einsortierenBuchung(buchung);
            db.buchungMerken(buchung);
					}
				}
				else if(typ.equals("SplitBuchung")) {
					SplitBuchung splitBuchung = new SplitBuchung();
					splitBuchung.laden(in, db);
          einsortierenBuchung(splitBuchung);
          db.buchungMerken(splitBuchung);
				}
				else if(!typ.equals("Dummy"))
					throw new IOException("Register.laden: Unbekannter Buchungstyp: "+typ);
				// "Dummy"-Buchung wird ignoriert!
			}
		}

		public void speichern(DataOutputStream out)
			throws IOException {
			out.writeUTF(name);
			out.writeInt(buchungen.size());
			for(int i=0; i<buchungen.size(); i++) {
				AbstractBuchung buchung = getBuchung(i);
				if(buchung.getClass() == Umbuchung.class) {
					UmbuchungKategorie kategorie = (UmbuchungKategorie) buchung.getKategorie();
					if((kategorie.getQuelle() == this) && !kategorie.isSelbstbuchung()) {
  					out.writeUTF("Dummy");
  					// Umbuchungen werden nur vom Zielregister gespeichert.
 						// "Dummy"-Buchung wird geschrieben damit die Anzahl stimmt!
  					// Selbstbuchungen dennoch schreiben!
 						if(DEBUG)
 						  System.out.println("Register "+name+": Dummy-Buchung gespeichert.");
					}
  				else
  					buchung.speichern(out);
				}
				else
  				buchung.speichern(out);
			}
		}

    public String[][] csvExport() {
      int anzahl = getAnzahlBuchungen();
      String[][] text = new String[anzahl][3];
      for(int i=0; i<anzahl; i++) {
        AbstractBuchung buchung = getBuchung(i);
        text[i][0] = ""+buchung.getDatum();
        text[i][1] = buchung.getText();
        text[i][2] = ""+buchung.getKategorie();
        text[i][3] = ""+buchung.getWert();
      }
      return text;
    }

    public int compareTo(Register register) {
      return this.name.compareTo(register.name);
    }

}
