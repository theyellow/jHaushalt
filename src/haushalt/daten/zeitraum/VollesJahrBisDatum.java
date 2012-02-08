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

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */

public class VollesJahrBisDatum
  extends AbstractZeitraum {

  private int tag;
  private int monat;
  private int jahr;

  public VollesJahrBisDatum(String datum) {
    Datum d = new Datum(datum);
    this.tag = d.getTag();
    this.monat = d.getMonat();
    this.jahr = d.getJahr();
  }
  
  public VollesJahrBisDatum(int tag, int monat, int jahr) {
    this.tag = tag;
    this.monat = monat;
    this.jahr = jahr;
  }

  public VollesJahrBisDatum(Datum datum) {
    this.tag = datum.getTag();
    this.monat = datum.getMonat();
    this.jahr = datum.getJahr();
  }

  public Datum getStartDatum() {
    return new Datum(tag, monat, jahr-1);
  }

  public Datum getEndDatum() {
    return new Datum(tag, monat, jahr);
  }

  public AbstractZeitraum folgeZeitraum() {
    return new VollesJahrBisDatum(tag, monat, jahr+1);
  }

  public String toString() {
    return ""+getStartDatum()+"-"+getEndDatum();
  }

  public String getDatenString() {
    return ""+getEndDatum();
  }
}