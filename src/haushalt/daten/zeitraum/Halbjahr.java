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
 * @version 2.6/2010.08.16
 * @since 2.0
 */

/*
 * 2010.08.16 BugFix: Berechnung des Zeitraums für ein halbes Jahr korrigiert (1.6. -> 1.7.)
 *            (entdeckt von Daniel Seither)
 * 2004.08.22 Erste Version
 */

public class Halbjahr
  extends AbstractZeitraum {

  private int haelfte = 1;
  private int jahr    = 2000;

  public Halbjahr(int haelfte, int jahr) {
    this.haelfte = haelfte;
    this.jahr = jahr;
  }

  public Halbjahr(String text) {
    try {
      StringTokenizer st = new StringTokenizer(text, "/");
      haelfte = Integer.parseInt(st.nextToken());
      jahr = Integer.parseInt(st.nextToken());
      if(haelfte > 2)
        haelfte = 2;
    }
    catch(Exception e) {
      // Fehler! -> Standardwerte.
    }
  }

  public Datum getStartDatum() {
    if(haelfte == 1)
      return new Datum(1,1,jahr);
    return new Datum(1,7,jahr);
  }

  public Datum getEndDatum() {
    if(haelfte == 1)
      return new Datum(1,7,jahr);
    return new Datum(1,1,jahr+1);
  }

  public AbstractZeitraum folgeZeitraum() {
    if(haelfte == 1)
      return new Halbjahr(2, jahr);
    return new Halbjahr(1, jahr+1);
  }

  public String toString() {
    return ""+haelfte+"/"+jahr;
  }

}