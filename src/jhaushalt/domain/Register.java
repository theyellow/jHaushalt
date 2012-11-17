/*
 * This file is part of jHaushalt.
 * jHaushalt is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * jHaushalt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */

package jhaushalt.domain;

import java.util.ArrayList;
import java.util.logging.Logger;

import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.StandardBuchung;
import jhaushalt.domain.buchung.Umbuchung;
import jhaushalt.domain.kategorie.UmbuchungKategorie;
import jhaushalt.domain.zeitraum.Datum;

/**
 * Die Register dienen als Container für die Buchung. Beispiele
 * für Register sind "Girokonto" oder "Bargeld".
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.1.3/2006.06.21
 */

/*
 * 2006.06.21 Verbesserung der Performanz beim Einsortieren
 * durch die Annahme, dass meistens an das Ende des
 * Registers eingefügt werden muss
 * 2006.06.21 BugFix: Einsortieren war doch notwendig, da die
 * Umbuchungen tw. schon vorzeitig ins Register
 * geladen werden
 * 2006.06.19 Verbesserung der Performanz durch Verzichten des
 * "Einsortierens" der Buchungen beim Laden
 * 2006.02.14 BugFix: Abfangen des Löschens der "letzten Zeile"
 * 2006.02.10 Implementierung des Interface 'Comparable'
 * 2006.01.31 Neusortieren einer Buchung jetzt unter Angabe
 * der Buchung selbst
 */

public class Register implements Comparable<Register> {

	private static final boolean DEBUG = false;
	private static final Logger LOGGER = Logger.getLogger(Register.class.getName());

	private String name;
	private final ArrayList<Buchung> buchungen;

	public Register(final String name) {
		this.name = name;
		this.buchungen = new ArrayList<Buchung>();
	}

	@Override
	public String toString() {
		return this.name;
	}

	public void setName(final String neuerName) {
		this.name = neuerName;
	}

	/**
	 * Liefert die Anzahl der im Register gespeicherten Buchungen.
	 * 
	 * @return Anzahl der Buchungen
	 */
	public int getAnzahlBuchungen() {
		return this.buchungen.size();
	}

	/**
	 * Liefert die Buchung an der angegebenen Position.
	 * 
	 * @param nr
	 *            Position der gesuchten Buchung
	 * @return gesuchte Buchung
	 */
	public Buchung getBuchung(final int nr) {
		if (nr == getAnzahlBuchungen()) {
			return new StandardBuchung();
		}
		return this.buchungen.get(nr);
	}

	/**
	 * Wird benötigt um alte Buchungen zu löschen.
	 * 
	 * @param nr
	 *            Position bis zu der Buchungen gelöscht werden
	 */
	public void removeBisBuchung(final int nr) {
		for (int i = 0; i <= nr; i++) {
			this.buchungen.remove(0);
			// mit jedem remove verändert sich der Index -> immer erstes Element
			// löschen
		}
	}

	/**
	 * Löscht eine Buchung.
	 * 
	 * @param nr
	 */
	public void entferneBuchung(final int nr) {
		if (nr == this.buchungen.size()) {
			return; // wenn eine neue (Split-)Buchung eingefügt
		}
		// werden soll, kann keine alte Buchung gelöscht
		// werden :-)
		final Buchung buchung = this.buchungen.get(nr);
		if (buchung.getClass() == Umbuchung.class) {
			// Die Buchung muss auch aus dem Partner-Register entfernt werden.
			final UmbuchungKategorie kategorie = (UmbuchungKategorie) buchung.getKategorie();
			kategorie.getPartnerRegister(this).buchungen.remove(buchung);
		}
		this.buchungen.remove(buchung);
	}

	/**
	 * Entfernt eine Umbuchung. Hiermit kann eine Umbuchung bei Änderung seines
	 * Quell- oder Zielregisters sich aus dem alten Register entfernen.
	 * 
	 * @param buchung
	 */
	public void loescheUmbuchung(final Umbuchung buchung) {
		this.buchungen.remove(buchung);
	}

	/**
	 * Sortiert die übergebene Buchung in das Register ein.
	 * Es wird aufsteigend nach Buchungsdatum sortiert.
	 * 
	 * @param buchung
	 *            einzusortierende Buchung
	 * @return Einfüge-Position
	 */
	public int einsortierenBuchung(final Buchung buchung) {
		final int size = this.buchungen.size();
		int pos = 0;
		for (int i = size - 1; i >= 0; i--) {
			if (buchung.compareTo(this.buchungen.get(i)) > 0) {
				pos = i + 1;
				i = 0;
			}
		}
		if (pos == size) { // ans Ende
			this.buchungen.add(buchung);
		} else { // neue Buchung einfuegen
			this.buchungen.add(pos, buchung);
		}
		if (DEBUG) {
			LOGGER.info("Register "
				+ this.name
				+ ": Buchung "
				+ buchung.getDatum()
				+ "/"
				+ buchung.getText()
				+ "/"
				+ buchung.getWert()
				+ " an Positon "
				+ (pos + 1)
				+ " einsortiert.");
		}
		return pos;
	}

	/**
	 * Sortiert die angegebene Buchung neu ein.
	 * Dies wird notwendig, wenn das Datum der Buchung geändert
	 * wurde.
	 * 
	 * @param buchung
	 *            geänderte Buchung
	 * @return Einfüge-Position
	 */
	public int buchungNeusortieren(final Buchung buchung) {
		this.buchungen.remove(buchung);
		return einsortierenBuchung(buchung);
	}

	/**
	 * Fügt die Buchungen aus einem anderen Register diesem
	 * Register hinzu und löscht sie dann.
	 * 
	 * @param registerZumLoeschen
	 *            Register aus dem die Buchungen übernommen werden
	 */
	public void registerVereinigen(final Register registerZumLoeschen) {
		while (registerZumLoeschen.getAnzahlBuchungen() > 0) {
			if (registerZumLoeschen.buchungen.get(0).getClass() == Umbuchung.class) {
				final Umbuchung umbuchung = (Umbuchung) registerZumLoeschen.buchungen.get(0);
				final UmbuchungKategorie alteKategorie = (UmbuchungKategorie) umbuchung.getKategorie();

				if (alteKategorie.isSelbstbuchung()) {
					// Umbuchung: alte Selbstbuchung
					umbuchung.setKategorie(new UmbuchungKategorie(this, this));
					// -> automatisch löschen und neu einfügen
				} else {
					// normale Umbuchung
					Register neueQuelle;
					Register neuesZiel;
					if (alteKategorie.getQuelle() == registerZumLoeschen) {
						neueQuelle = this;
					} else {
						neueQuelle = alteKategorie.getQuelle();
					}
					if (alteKategorie.getZiel() == registerZumLoeschen) {
						neuesZiel = this;
					} else {
						neuesZiel = alteKategorie.getZiel();
					}
					if (neueQuelle != neuesZiel) {
						umbuchung.setKategorie(new UmbuchungKategorie(neueQuelle, neuesZiel));
					} else {
						// sonst loeschen:
						neueQuelle.loescheUmbuchung(umbuchung);
						registerZumLoeschen.buchungen.remove(0);
					}
				}
			} else {
				// StandardBuchung + SplitBuchung
				einsortierenBuchung(registerZumLoeschen.buchungen.get(0));
				registerZumLoeschen.buchungen.remove(0);
			}
		}
	}

	/**
	 * Liefert den Saldo des Registers bis zum angegebenen Datum (exklusiv).
	 * 
	 * @param datum
	 * @return Saldo
	 */
	public Geldbetrag getSaldo(final Datum datum) {
		Geldbetrag saldo = new Geldbetrag();
		int i = 0;
		while ((i < this.buchungen.size()) && (datum.compareTo(getBuchung(i).getDatum()) > 0)) {
			if (getBuchung(i).getClass() == Umbuchung.class) {
				final UmbuchungKategorie kategorie = (UmbuchungKategorie) getBuchung(i).getKategorie();
				if ((kategorie.getQuelle() == this) && !kategorie.isSelbstbuchung()) {
					saldo = saldo.sub(getBuchung(i).getWert());
					// Falls dieses Register Quelle einer Umbuchung ist:
					// Subtrahieren!
					// Selbstbuchugen aber nicht!
				} else {
					saldo.sum(getBuchung(i).getWert());
				}
			} else { // StandardBuchung + SplitBuchung
				saldo.sum(getBuchung(i).getWert());
			}
			i++;
		}
		return saldo;
	}

	/**
	 * Liefert den Saldo des Registers bis zur angegebenen Zeile.
	 * 
	 * @param row
	 * @return Saldo
	 */
	public Geldbetrag getSaldo(final int row) {
		Geldbetrag saldo = new Geldbetrag();
		for (int i = 0; i <= row; i++) {
			if (getBuchung(i).getClass() == Umbuchung.class) {
				final UmbuchungKategorie kategorie = (UmbuchungKategorie) getBuchung(i).getKategorie();
				if ((kategorie.getQuelle() == this) && !kategorie.isSelbstbuchung()) {
					saldo = saldo.sub(getBuchung(i).getWert());
					// Falls dieses Register Quelle einer Umbuchung ist:
					// Subtrahieren!
					// Selbstbuchugen aber nicht!
				} else {
					saldo.sum(getBuchung(i).getWert());
				}
			} else { // StandardBuchung + SplitBuchung
				saldo.sum(getBuchung(i).getWert());
			}
		}
		return saldo;
	}
	
	public String[][] csvExport() {
		final int anzahl = getAnzahlBuchungen();
		final String[][] text = new String[anzahl][3];
		for (int i = 0; i < anzahl; i++) {
			final Buchung buchung = getBuchung(i);
			text[i][0] = "" + buchung.getDatum();
			text[i][1] = buchung.getText();
			text[i][2] = "" + buchung.getKategorie();
			text[i][3] = "" + buchung.getWert();
		}
		return text;
	}

	public int compareTo(final Register register) {
		return this.name.compareTo(register.name);
	}

}
