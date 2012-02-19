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

import haushalt.gui.TextResource;

/**
 * Verwaltet alle Kategorien.
 * Standardmäßig steht die die Kategorie "Sonstiges" zur Verfügung.
 * @author Dr. Lars H. Hahn
 * @version 2.5/2008.01.22
 */

/*
 * 2008.01.22 Internationalisierung
 * 2004.08.22 Version 2.0
 */

public class EinzelKategorie implements Kategorie {
  private static final TextResource res = TextResource.get();
	public static final EinzelKategorie SONSTIGES = new EinzelKategorie(res.getString("miscellaneous"), null);
  
  private final EinzelKategorie hauptkategorie;
	private String name;

  public EinzelKategorie(String name, EinzelKategorie hauptkategorie) {
    this.name = name;
    this.hauptkategorie = hauptkategorie;
  }

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String toString() {
    if(hauptkategorie != null)
      return ""+hauptkategorie+":"+name;
    return name;
  }

	public boolean isHauptkategorie() {
		return hauptkategorie == null;
	}

  public EinzelKategorie getHauptkategorie() {
    if(isHauptkategorie())
      return this;
    return hauptkategorie.getHauptkategorie();
  }
  
  public boolean istInKategorie(EinzelKategorie kategorie, boolean unterkategorienVerwenden) {
  	if(kategorie == this)
  	  return true;
  	// wenn die Unterkatgorien NICHT verwendet werden, dann
  	// muss überprüft werden, ob die Hauptkategorie passt:
  	if((!unterkategorienVerwenden) && (getHauptkategorie() == kategorie))
  	  return true;
  	return false;
  }
  
  // -- Kategorie-Summe -------------------------------------------------------
  
  private Euro summe = new Euro();
  
  public void addiereWert(Euro wert, boolean unterkat) {
    if(unterkat)
      summe.sum(wert);
    else
      getHauptkategorie().summe.sum(wert);
  }
  
  public Euro getSumme() {
    return summe;
  }
  
  public void loescheSumme() {
    summe = new Euro();
  }
  
  // -- Methoden fuer Interface: Comparable -----------------------------------

  public int compareTo(Kategorie kategorie) {
    return this.toString().compareToIgnoreCase(kategorie.toString());
  }

 }