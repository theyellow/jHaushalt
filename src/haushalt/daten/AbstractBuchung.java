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

package haushalt.daten;

import haushalt.daten.zeitraum.AbstractZeitraum;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Basisklasse für die Buchungsarten Umbuchung, SplitBuchung und
 * StandardBuchung. Jede Buchung wird in einem Register abgelegt.
 * @author Dr. Lars H. Hahn
 * @version 2.1/2006.02.10
 */

/*
 * 2006.02.10 Ergänzung der Methode isInKategorie
 * 2004.08.22 Erste Version
 */

abstract public class AbstractBuchung implements Cloneable, Comparable<Object> {
 
  // -- Buchungsdatum ----------------------------------------------------------
  
  private Datum datum = new Datum();

	/**
	 * Setzt das Buchungsdatum
	 * @param datum Buchungsdatum
	 */
	public void setDatum(Datum datum) {
		this.datum = datum;
	}

	/**
	 * Liefert das Buchungsdatum
	 * @return Buchungsdatum
	 */
	public Datum getDatum() {
		return datum;
	}

  // -- Buchungstext -----------------------------------------------------------
  
  private String text  = "";

	/**
	 * Setzt den Buchungstext
	 * @param text Buchungstext
	 */
	public void setText(String text) {
		this.text = text;
	}

  /**
   * Liefert den Buchungstext
   * @return Buchungstext
   */
	public String getText() {
		return text;
	}

	/**
	 * Sucht nach Text innerhalb des Buchungstextes.
	 * @param suchText gesuchter Text
	 * @return <code>true</code> falls Text gefunden wurde
	 */
	public boolean sucheText(String suchText, boolean grossUndKlein) {
	  if(grossUndKlein)
			return (text.indexOf(suchText) > -1);
		return (text.toLowerCase().indexOf(suchText.toLowerCase()) > -1);
	}

  /**
   * Ersetzt Text innerhalb des Buchungstextes.
   * @param alt alter Text
   * @param neu neuer Text
   * @return <code>true</code> falls das Ersetzten erfolgreich war
   */
	public boolean ersetzeText(String alt, String neu) {
		int idx = text.indexOf(alt);
		if(idx != -1) {
			setText(text.substring(0, idx)+neu+text.substring(idx+alt.length()));
			return true;
		}
		return false;
	}

  // -- Kategorie --------------------------------------------------------------
  
	/**
   * Setzt die Kategorie der Buchung.
   * Im Fall einer gesplitteten Buchung ist die Kategorie ein ListArray der
   * einzelnen Kategorien. Bei einer Umbuchung ist die Kategorie das 
   * "Partner"-Register.
   * @param kategorie neue Kategorie der Buchung 
	 */
  abstract public void setKategorie(Kategorie kategorie);
  
  /**
   * Liefert die Kategorie der Buchung.
   * Im Fall einer gesplitteten Buchung ist die Kategorie ein ListArray der
   * einzelnen Kategorien. Bei einer Umbuchung ist die Kategorie das 
   * "Partner"-Register.
   * @return Kategorie der Buchung 
   */
  abstract public Kategorie getKategorie();

  /**
   * Ersetzt eine alte Kategorie durch eine neue.
   * Bei einer gesplitteten Buchung werden alle Einzel-Kategorien überprüft und
   * ggf. ersetzt. Bei einer Umbuchung erfolgt keine Ersetzung.<br>
   * Wenn die alte Kategorie <b>null</b> ist, wird jede 
   * beliebige Kategorie ersetzt.
   * @param alteKategorie Kategorie die ersetzt werden soll
   * @param neueKategorie neue Kategorie
   * @return Anzahl ersetzter Kategorien
   */
  abstract public int ersetzeKategorie(EinzelKategorie alteKategorie, EinzelKategorie neueKategorie);
  
  /**
   * Überprüft ob die Buchung teil einer Kategorie ist
   * @param kategorie Kategorie auf die überprüft wird
   * @param unterkategorienVerwenden false, wenn nur Hauptkategorien betrachtet werden
   * @return true, wenn die Buchung (oder Teile der Buchung) zu der angegeben Kategorie gehören
   */
  abstract public boolean istInKategorie(EinzelKategorie kategorie, boolean unterkategorienVerwenden);
  

  // -- Buchungswert -----------------------------------------------------------
  
  /**
   * Setzt den Buchungswert.
   * Bei einer gesplitteten Buchung werden alle Einzel-Werte proportional
   * angepasst.
   * @param wert Buchungswert
   */
  abstract public void setWert(Euro wert);
	
  /**
   * Liefert den Buchungswert.
   * @return Buchungswert
   */
  abstract public Euro getWert();
	
	// -- Auswertung -------------------------------------------------------------

  /**
   * Addiert zur der Kategorie / zu den Kategorien der Buchung der Wert der
   * Buchung hinzu.   
   */
  abstract public void bildeKategorieSumme(AbstractZeitraum zeitraum, boolean unterkat);

  /**
   * Liefert den Wert der Buchung, wenn die Buchung die passende Kategorie
   * besitzt.
   * @param kategorie gesuchte Kategorie
   * @param unterkat Unterkategorien werden verwendet
   * @return Wert der Buchung, wenn die Parameter passen
   */
  abstract public Euro getKategorieWert(EinzelKategorie kategorie, boolean unterkat);
  
  // -- E/A-Funktionen ---------------------------------------------------------

  /**
   * Liest die Buchung vom angegeben Stream.
   * Die Datenbasis wird benötigt, um die Kategorien und Register aufzulösen.
   * @param in Eingabe-Stream
   * @param db Datenbasis
   */
//  abstract public void laden(DataInputStream in, Datenbasis db) throws IOException;

  /**
   * Schreibt die Buchung in den angegeben Stream.
   * @param out Ausgabe-Stream
   */
  abstract public void speichern(DataOutputStream out) throws IOException;

  // -- Methode des Interface 'Comparable' -------------------------------------

  public int compareTo(Object obj) throws ClassCastException {
    if(obj.getClass() == Datum.class)
      return datum.compareTo((Datum) obj);
    AbstractBuchung buchung = (AbstractBuchung)obj;
    int vergleich = datum.compareTo(buchung.datum);
    if(vergleich == 0)
      vergleich = buchung.getWert().compareTo(getWert()); // absteigend!
    return vergleich;
  }

  // -- Methode des Interface 'Cloneable' --------------------------------------

  abstract public Object clone();

}