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
 * along with jHaushalt; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */

package haushalt.daten;

import haushalt.daten.zeitraum.AbstractZeitraum;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Basisklasse für die Buchungsarten Umbuchung, SplitBuchung und
 * StandardBuchung. Jede Buchung wird in einem Register abgelegt.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.1/2006.02.10
 */

/*
 * 2006.02.10 Ergänzung der Methode isInKategorie
 * 2004.08.22 Erste Version
 */

public abstract class AbstractBuchung implements Cloneable, Comparable<Object> {

	private Datum datum = new Datum();

	// -- Buchungstext
	// -----------------------------------------------------------

	private String text = "";

	/**
	 * Setzt das Buchungsdatum
	 * 
	 * @param datum
	 *            Buchungsdatum
	 */
	public void setDatum(final Datum datum) {
		this.datum = datum;
	}

	/**
	 * Liefert das Buchungsdatum
	 * 
	 * @return Buchungsdatum
	 */
	public Datum getDatum() {
		return this.datum;
	}

	/**
	 * Setzt den Buchungstext
	 * 
	 * @param text
	 *            Buchungstext
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/**
	 * Liefert den Buchungstext
	 * 
	 * @return Buchungstext
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Sucht nach Text innerhalb des Buchungstextes.
	 * 
	 * @param suchText
	 *            gesuchter Text
	 * @return <code>true</code> falls Text gefunden wurde
	 */
	public boolean sucheText(final String suchText, final boolean grossUndKlein) {
		if (grossUndKlein) {
			return (this.text.indexOf(suchText) > -1);
		}
		return (this.text.toLowerCase().indexOf(suchText.toLowerCase()) > -1);
	}

	/**
	 * Ersetzt Text innerhalb des Buchungstextes.
	 * 
	 * @param alt
	 *            alter Text
	 * @param neu
	 *            neuer Text
	 * @return <code>true</code> falls das Ersetzten erfolgreich war
	 */
	public boolean ersetzeText(final String alt, final String neu) {
		final int idx = this.text.indexOf(alt);
		if (idx != -1) {
			setText(this.text.substring(0, idx) + neu + this.text.substring(idx + alt.length()));
			return true;
		}
		return false;
	}

	// -- IKategorie
	// --------------------------------------------------------------

	/**
	 * Setzt die IKategorie der Buchung.
	 * Im Fall einer gesplitteten Buchung ist die IKategorie ein ListArray der
	 * einzelnen Kategorien. Bei einer Umbuchung ist die IKategorie das
	 * "Partner"-Register.
	 * 
	 * @param kategorie
	 *            neue IKategorie der Buchung
	 */
	public abstract void setKategorie(IKategorie kategorie);

	/**
	 * Liefert die IKategorie der Buchung.
	 * Im Fall einer gesplitteten Buchung ist die IKategorie ein ListArray der
	 * einzelnen Kategorien. Bei einer Umbuchung ist die IKategorie das
	 * "Partner"-Register.
	 * 
	 * @return IKategorie der Buchung
	 */
	public abstract IKategorie getKategorie();

	/**
	 * Ersetzt eine alte IKategorie durch eine neue.
	 * Bei einer gesplitteten Buchung werden alle Einzel-Kategorien überprüft
	 * und
	 * ggf. ersetzt. Bei einer Umbuchung erfolgt keine Ersetzung.<br>
	 * Wenn die alte IKategorie <b>null</b> ist, wird jede
	 * beliebige IKategorie ersetzt.
	 * 
	 * @param alteKategorie
	 *            IKategorie die ersetzt werden soll
	 * @param neueKategorie
	 *            neue IKategorie
	 * @return Anzahl ersetzter Kategorien
	 */
	public abstract int ersetzeKategorie(EinzelKategorie alteKategorie, EinzelKategorie neueKategorie);

	/**
	 * Überprüft ob die Buchung teil einer IKategorie ist
	 * 
	 * @param kategorie
	 *            IKategorie auf die überprüft wird
	 * @param unterkategorienVerwenden
	 *            false, wenn nur Hauptkategorien betrachtet werden
	 * @return true, wenn die Buchung (oder Teile der Buchung) zu der angegeben
	 *         IKategorie gehören
	 */
	public abstract boolean istInKategorie(EinzelKategorie kategorie, boolean unterkategorienVerwenden);

	// -- Buchungswert
	// -----------------------------------------------------------

	/**
	 * Setzt den Buchungswert.
	 * Bei einer gesplitteten Buchung werden alle Einzel-Werte proportional
	 * angepasst.
	 * 
	 * @param wert
	 *            Buchungswert
	 */
	public abstract void setWert(Euro wert);

	/**
	 * Liefert den Buchungswert.
	 * 
	 * @return Buchungswert
	 */
	public abstract Euro getWert();

	// -- Auswertung
	// -------------------------------------------------------------

	/**
	 * Addiert zur der IKategorie / zu den Kategorien der Buchung der Wert der
	 * Buchung hinzu.
	 */
	public abstract void bildeKategorieSumme(AbstractZeitraum zeitraum, boolean unterkat);

	/**
	 * Liefert den Wert der Buchung, wenn die Buchung die passende IKategorie
	 * besitzt.
	 * 
	 * @param kategorie
	 *            gesuchte IKategorie
	 * @param unterkat
	 *            Unterkategorien werden verwendet
	 * @return Wert der Buchung, wenn die Parameter passen
	 */
	public abstract Euro getKategorieWert(EinzelKategorie kategorie, boolean unterkat);

	// -- E/A-Funktionen
	// ---------------------------------------------------------

	/**
	 * Liest die Buchung vom angegeben Stream.
	 * Die Datenbasis wird benötigt, um die Kategorien und Register aufzulösen.
	 * 
	 * @param in
	 *            Eingabe-Stream
	 * @param db
	 *            Datenbasis
	 */
	// abstract public void laden(DataInputStream in, Datenbasis db) throws
	// IOException;

	/**
	 * Schreibt die Buchung in den angegeben Stream.
	 * 
	 * @param out
	 *            Ausgabe-Stream
	 */
	public abstract void speichern(DataOutputStream out) throws IOException;

	// -- Methode des Interface 'Comparable'
	// -------------------------------------

	public int compareTo(final Object obj) {
		if (obj.getClass() == Datum.class) {
			return this.datum.compareTo((Datum) obj);
		}
		final AbstractBuchung buchung = (AbstractBuchung) obj;
		int vergleich = this.datum.compareTo(buchung.datum);
		if (vergleich == 0) {
			vergleich = buchung.getWert().compareTo(getWert()); // absteigend!
		}
		return vergleich;
	}

	// -- Methode des Interface 'Cloneable'
	// --------------------------------------

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	};

}
