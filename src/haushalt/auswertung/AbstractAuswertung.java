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

package haushalt.auswertung;

import haushalt.daten.Datenbasis;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;

import javax.swing.JPanel;

/**
 * Abstrakte Klasse für alle Auswertungen.<br>
 * Eine Auswertung ist ein Panel und kann gedruckt werden.
 * In den abgeleiteten Klassen werden alle Parameter einer 
 * Auswertung gekapselt.
 * Die Parameter werden in einem generischen Dialog 
 * ("Eigenschaften") angezeigt und können verändert werden. 
 * Die Parameter können gespeichert und wieder geladen werden.
 *  
 * @author Dr. Lars H. Hahn
 * @version 2.5/2008.01.17
 * @since 2.0
 */

/*
 * 2008.01.17 Internationalisierung
 * 2004.08.22 Erste Version
 */

public abstract class AbstractAuswertung extends JPanel implements Printable {
  private static final long serialVersionUID = 1L;
  private static final boolean DEBUG = false;
  private static final TextResource res = TextResource.get();

  private static int nr = 1;
  private String name;
  protected final Datenbasis db;
  protected String[][] tabelle = null;
  
  public AbstractAuswertung(Datenbasis db, String name) {
    this.db = db;
    if(name == null) {
	    this.name = res.getString("unnamed")+" ("+nr+")";
	    nr++;
    }
    else
      this.name = name;
  }
  
  public final String toString() {
    return name;
  }

  /**
   * Setzt den Namen der Auswertung neu.
   * @param name neuer Name der Auswertung
   */
  public final void setAuswertungName(String name) {
    if(name != null) {
	    if(DEBUG)
	      System.out.println("AbstractAuswertung: "+this.name+"->"+name);
	    this.name = name;
    }
  }
  
  /**
   * Liefert die zentrale Tabelle der Auswertung.
   * Viele Auswertungen werden in Tabellenform ausgegeben, um den
   * CSV-Export der Tabelle zu ermöglichen. Wird die Tabelle in der
   * Super-Klasse verwaltet und kann mit dieser Methode abgefragt
   * werden.
   * @return zentrale Tabelle
   */
  public String[][] getTabelle() {
    return tabelle;
  }
  
	abstract public int print(Graphics g, PageFormat pageFormat, int seite)
    throws PrinterException;
	
	/**
	 * Ermöglicht die Auswertung bei Bedarf neu zu berechnen. Es wird so verhindert,
	 * dass ständig (z.B. bei Größenänderungen des Fensters) die Auswertung neu
	 * berechnet werden muss.
	 */
	abstract public String berechneAuswertung(); 
	
	/**
	 * Zeigt einen Dialog in dem die Parameter der Auswertungen änderbar sind.
	 * @return <code>false</code> Benutzer hat 'Abbruch' gewählt
	 */
	abstract public boolean zeigeEigenschaften();
	
  abstract public void laden(DataInputStream in)
  throws IOException;

  abstract public void speichern(DataOutputStream out)
  throws IOException;

  /**
   * Erzeugt eine neue Instanz einer Auswertung.
   * @param klassenname Klasse der Auswertung
   * @param haushalt Parameter an die Auswertung
   * @param db Parameter an die Auswertung
   * @param auswertungname Parameter an die Auswertung
   * @return erzeugte Auswertung
   */
  public static AbstractAuswertung erzeugeAuswertung(String klassenname, Haushalt haushalt, Datenbasis db, String auswertungname) {
    AbstractAuswertung auswertung = null;
    Object[] parameters = {haushalt, db, auswertungname};
    Class<?>[] parameterTyp = {Haushalt.class, Datenbasis.class, String.class};
    try {
      Class<?> klasse = Class.forName(klassenname);
      Constructor<?> constructor = klasse.getConstructor(parameterTyp);
      auswertung = (AbstractAuswertung) constructor.newInstance(parameters);
    } catch (Exception e) {
      System.out.println("-E- Fehler beim Erzeugen der Auswertung: "+klassenname);
      e.printStackTrace();
    }
    if(DEBUG)
      System.out.println("Auswertung "+klassenname+" erzeugt.");
    return auswertung;
  }

}
