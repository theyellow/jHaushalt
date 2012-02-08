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

/**
 * @author Dr. Lars H. Hahn
 * @version 2.1/2006.02.10
 * @since 2.1
 */

/*
 * 2006.02.10 Erste Version
 */

public class UmbuchungKategorie implements Kategorie, Cloneable {
  private static final long serialVersionUID = 1L;

  private Register quelle;
  private Register ziel;

  public UmbuchungKategorie(Register quelle, Register ziel) {
    this.quelle = quelle;
    this.ziel = ziel;
  }

  public Register getPartnerRegister(Register register) {
    return (register==quelle)?ziel:quelle;
  }
  
  public Register getPartnerRegister(String regname) {
    return regname.equals(""+quelle)?ziel:quelle;
  }

  public boolean isSelbstbuchung() {
    return (quelle == ziel);
  }
  
  public String toString() {
    return "["+quelle+"->"+ziel+"]";
  }
  
  public int compareTo(Kategorie kategorie) {
    return this.toString().compareTo(kategorie.toString());
  }

  public Register getQuelle() {
    return quelle;
  }

  public Register getZiel() {
    return ziel;
  }

  final public Object clone() {
    return new UmbuchungKategorie(quelle, ziel);
  }

  public void setQuelle(Object neuesRegister) {
    quelle = (Register) neuesRegister;
  }

  public void setZiel(Object neuesRegister) {
    ziel = (Register) neuesRegister;
  }
}
