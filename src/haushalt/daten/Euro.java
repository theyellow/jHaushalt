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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Repräsentiert einen Geldbetrag.
 * Die Währung des Betrags ist in der Regel Euro, das Währungssymbol kann aber
 * geändert werden.
 * @author Dr. Lars H. Hahn
 * @version 2.6/2009.05.17
 */

/*
 * 2009.05.17 Erweiterung (durch Kay Ruhland): 
 *            Mehrere Euro-Stringwerte werden jetzt addiert.
 * 2007.07.24 Internationalisierung
 * 2006.02.02 BugFix: Plus-Zeichen beim Pharsen ignorieren
 */

public class Euro implements Cloneable, Comparable<Euro> {
  private static final TextResource res = TextResource.get();

  public static final Euro NULL_EURO = new Euro();
	private static String symbol = "€";
  private long wert = 0L;

  public Euro() {
  }

  public Euro(double wert) {
    setWert(wert);
  }

  public Euro(String wert) {
    Locale locale = res.getLocale();
    NumberFormat nf = NumberFormat.getInstance(locale);
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
    MathParser mp = new MathParser(nf);
    if(wert != "") {
      setWert(mp.parseExpr(wert));
    }
    else
      this.wert = 0L;
  }

	public String toString() {
    Locale locale = res.getLocale();
    NumberFormat nf = NumberFormat.getInstance(locale);
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
		return nf.format(wert / 100.0D) + " " + symbol;
	}

	public double toDouble() {
		return wert / 100.0D;
	}

  private void setWert(double wert) {
    double help = wert * 100.0D;
    if(help < 0)
      this.wert = (long) (help - 0.5D);
    else
      this.wert = (long) (help + 0.5D);
  }

  public static void setWaehrungssymbol(String waehrungssymbol) {
    symbol = waehrungssymbol;
  }

  public void umrechnenVonDM() { // Wenn der Wert in DM vorliegt -> umrechnen in EURO
    if(wert < 0)
      wert = (long)(wert / 1.95583D - 0.5D);
    else
      wert = (long)(wert / 1.95583D + 0.5D);
  }

  public boolean equals(Object obj) {
    if(obj == null)
      return false;
    if(obj.getClass() != Euro.class)
      return false;
    return ((Euro)obj).wert == wert;
  }

  public int hashCode() {
    assert false : "hashCode not designed";
    return 0;
  }
  
  /**
   * Bildet die Summe aus diesem Euro-Objekt und dem Parameter.
   * Die Summe wird einem neuen Objekt zu gewiesen. Um aufzusummieren sollte
   * deshalb die Funktion <code>sum(Euro)</code> verwendet werden.
   * @see Euro#sum(Euro)
   * @param euro
   * @return Neues Objekt mit der Summe
   */
  public Euro add(Euro euro) {
    Euro ergebnis = new Euro();
    ergebnis.wert = this.wert + euro.wert;
    return ergebnis;
  }

  public Euro sub(Euro euro) {
    Euro ergebnis = new Euro();
    ergebnis.wert = this.wert - euro.wert;
    return ergebnis;
  }

  public Euro durch(int zahl) {
    Euro ergebnis = new Euro();
    ergebnis.wert = this.wert / zahl;
    return ergebnis;
  }
  
  public Euro mal(double zahl) {
		Euro ergebnis = new Euro();
		ergebnis.setWert(toDouble() * zahl);
		return ergebnis;
  }
  
  public void sum(Euro euro) {
    wert += euro.wert;
  }

	// -- E/A-Funktionen -------------------------------------------------------

	public void laden(DataInputStream in)
		throws IOException {
		wert = in.readLong();
	}

	public void speichern(DataOutputStream out)
		throws IOException {
		out.writeLong(wert);
	}

	// -- Methoden fuer Interface: Cloneable --------------------

	final public Object clone() {
		Euro kopie = new Euro();
		kopie.wert = this.wert;
		return kopie;
	}

  // -- Methoden fuer Interface: Comparable -------------------

  public int compareTo(Euro euro) {
    if(wert < euro.wert)
      return -1;
    if(wert > euro.wert)
      return 1;
    return 0;
  }
  
  public static void main(String[] args) {
    Locale list[] = Locale.getAvailableLocales();
    for (int i = 0; i < list.length; i++) {
      NumberFormat nf = NumberFormat.getInstance(list[i]);
      System.out.print(list[i].getDisplayName()+" "+nf.format(1234.56D));
      DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, list[i]);
      System.out.println(" "+df.format(new Date()));
    }

  }

}