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
 * @version 2.5.1/2008.03.10
 * @since 2.0
 */

 /* 
  * 2008.03.10 BugFix: Falsches Start/End-Datum im Rahmen der Internationalisierung
  * 2004.08.22 Erste Version
  */

public class Quartal extends AbstractZeitraum {

  private int quartal = 1;
  private int jahr = 2000;

  public Quartal(int quartal, int jahr) {
    this.quartal = quartal;
    this.jahr = jahr;
  }

  public Quartal(String text) {
    try {
      StringTokenizer st = new StringTokenizer(text, "/");
      quartal = Integer.parseInt(st.nextToken());
      jahr = Integer.parseInt(st.nextToken());
      if(quartal > 4)
        quartal = 4;
    }
    catch(Exception e) {
      // Fehler! -> Standardwerte.
    }
  }

  public Datum getStartDatum() {
      return getQuartalsBeginn(quartal, jahr);
  }

  public Datum getEndDatum() {
    if(quartal == 4)
      return getQuartalsBeginn(1, jahr+1);
    return getQuartalsBeginn(quartal+1, jahr);
  }

  public AbstractZeitraum folgeZeitraum() {
    if(quartal == 4)
      return new Quartal(1, jahr+1);
    return new Quartal(quartal+1, jahr);
  }

  private static Datum getQuartalsBeginn(int quartal, int jahr) {
    int monat = 1;
    switch(quartal) {
      case 1: monat = 1; break;
      case 2: monat = 4; break;
      case 3: monat = 7; break;
      case 4: monat = 10; break;
    }
    return new Datum(1,monat,jahr);
  }

  public String toString() {
    return ""+quartal+"/"+jahr;
  }

}