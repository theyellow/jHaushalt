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

package haushalt.daten.zeitraum;

import haushalt.daten.Datum;

import java.util.StringTokenizer;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.6/2009.08.05
 */

/*
 * 2009.08.05 BugFix: Korrekter Folgezeitraum
 * 2004.08.22 Version 2.0
 */
public class FreierZeitraum
  extends AbstractZeitraum {

  private Datum startDatum = new Datum();
  private Datum endDatum = new Datum();

  public FreierZeitraum(int tag1, int monat1, int jahr1, int tag2, int monat2, int jahr2) {
    this.startDatum = new Datum(tag1, monat1, jahr1);
    this.endDatum = new Datum(tag2, monat2, jahr2);
  }

  public FreierZeitraum(Datum startDatum, Datum endDatum) {
    this.startDatum = startDatum;
    this.endDatum = endDatum;
  }

  public FreierZeitraum(String text) {
    try {
      StringTokenizer st = new StringTokenizer(text, "-");
      startDatum = new Datum(st.nextToken());
      endDatum = new Datum(st.nextToken());
    }
    catch(Exception e) {
      // Fehler! -> Standardwerte.
    }
  }

  /**
   * @see haushalt.daten.zeitraum.AbstractZeitraum#getStartDatum()
   */
  public Datum getStartDatum() {
    return startDatum;
  }

  /**
   * @see haushalt.daten.zeitraum.AbstractZeitraum#getEndDatum()
   */
  public Datum getEndDatum() {
    return endDatum;
  }

  /**
   * @see haushalt.daten.zeitraum.AbstractZeitraum#folgeZeitraum()
   */
  public AbstractZeitraum folgeZeitraum() {
	Datum neuesStartDatum = (Datum) endDatum.clone();
	Datum neuesEndDatum = (Datum) endDatum.clone();
	neuesEndDatum.addiereTage(getAnzahlTage());
    return new FreierZeitraum(neuesStartDatum, neuesEndDatum);
  }

  public String toString() {
    return startDatum+"-"+endDatum;
  }

}