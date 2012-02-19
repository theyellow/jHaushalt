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
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Repräsentiert ein Buchungsdatum.
 * @author Dr. Lars H. Hahn
 * @version 2.6/2009.08.05
 * @since 2.0
 */

/*
 * 2009.08.05 addiereTage(int) liefert keine Clone mehr zurück
 * 2007.10.31 Umstellung des Wertes von Date auf GregorianCalendar
 * 2007.10.30 An Tagen mit 25/23 Stunden (wg. Sommerzeitumstellung) hat das 
 *            Inkremetieren/Dekremetieren des Datums nicht funktioniert
 * 2007.08.07 Automatisches Ergänzen eines (internationalen) Datums (> 8 Zeichen) 
 * 2007.07.18 Internationalisierung + Entfernung der überflüssigen Methode 'heute'
 * 2004.08.22 Erste Version
 */

public class Datum implements Comparable<Datum>, Cloneable {
  private static final boolean DEBUG = false;
  private static final TextResource res = TextResource.get();
  
  private GregorianCalendar wert;

  public Datum() {
    wert = new GregorianCalendar();
  }

  public Datum(int tag, int monat, int jahr) {
    wert = new GregorianCalendar(jahr, monat-1, tag);
  }

  public Datum(String datumString) {
    wert = new GregorianCalendar();
    Locale locale = res.getLocale();
    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
    String heute = df.format(wert.getTime());
    if(res.getLocale().getLanguage().equals("de") && (datumString.length() < heute.length()))
      datumString = datumString + heute.substring(datumString.length());
    try {
      this.wert.setTime(df.parse(datumString));
    }
    catch(java.text.ParseException e) {
    }
  }
  
	public String toString() {
    Locale locale = res.getLocale();
    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		return df.format(wert.getTime());
	}

  public long sub(Datum datum) {
    return (wert.getTimeInMillis() - datum.wert.getTimeInMillis()) / 86400000L;    
    // 1 Tag = 24 * 60 * 60 * 1000 = 86400000 ms
  }
  
  public void addiereTage(int tage) {
    wert.add(Calendar.DAY_OF_MONTH, tage);
    if(DEBUG)
      System.out.println("Tage plus: "+tage+" / Tage alt: "+wert.getTimeInMillis()+"="+toString());
  }

  public int getTag() {
    return wert.get(Calendar.DAY_OF_MONTH);
  }

  public int getMonat() {
    return wert.get(Calendar.MONTH)+1;
  }

  public int getJahr() {
    return wert.get(Calendar.YEAR);
  }

  public boolean istImZeitraum(AbstractZeitraum zeitraum) {
    return ((compareTo(zeitraum.getStartDatum()) >= 0) &&
            (compareTo(zeitraum.getEndDatum()) < 0));
  }

	// -- E/A-Funktionen -------------------------------------------------------

	public void laden(DataInputStream in) throws IOException {
    // Das Datum wird immer im deutschen Format gespeichert
    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN);
		try {
			wert.setTime(df.parse(in.readUTF()));
		}
		catch(java.text.ParseException e) {
			throw new IOException();
		}
	}

	public void speichern(DataOutputStream out) throws IOException {
    // Das Datum wird immer im deutschen Format gespeichert
    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN);
		out.writeUTF(df.format(wert.getTime()));
	}
	
	// -- Methoden fuer Interface: Cloneable --------------------

	final public Object clone() {
    Datum datum = new Datum();
    datum.wert = (GregorianCalendar) wert.clone();
		return datum;
	}
	
	// -- Methoden fuer Interface: Comparable -------------------

	public int compareTo(Datum datum) {
    return wert.compareTo(datum.wert);
  }

}