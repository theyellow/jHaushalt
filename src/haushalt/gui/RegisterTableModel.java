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
along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.


(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

/*
 * 2005.02.18 BugFix: Das Übernehmen einer auto. Buchung kann zum Neusortieren
 * führen, dies wurde aber nicht angezeigt und es wurde nicht mitgesprungen.
 * 2004.08.25 BugFix: Beim Einfügen einer neuen Buchung wurde teilweise, die 
 * zuvor erzeugte überschrieben.
 * 2004.08.25 BugFix: Bei der Verwendung von gemerkten Buchungen wurde teilweise 
 * die Anzeige nicht aktualisiert.
 */

package haushalt.gui;

import haushalt.daten.AbstractBuchung;
import haushalt.daten.Datenbasis;
import haushalt.daten.Datum;
import haushalt.daten.Euro;
import haushalt.daten.EinzelKategorie;
import haushalt.daten.Kategorie;
import haushalt.daten.StandardBuchung;
import haushalt.daten.Umbuchung;
import haushalt.daten.UmbuchungKategorie;

import javax.swing.table.AbstractTableModel;

/**
 * Ermöglicht die Darstellung eines Registers in einer Swing-Tabelle.
 * @author Dr. Lars H. Hahn
 * @version 2.5/2006.07.04
 */

/*
 * 2006.07.04 Internationalisierung
 * 2006.02.01 BugFix: Umbuchung wird jetzt bei der Änderung
 *            des Datums im zweiten Register neusortiert
 * 2006.02.01 BugFix: Änderung des Wertes einer Umbuchung
 *            im zweiten Register führte zu einem Wechsel
 *            des Vorzeichens
 */

public class RegisterTableModel extends AbstractTableModel {
  private static final boolean DEBUG = false;
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  private static final String[] spaltenNamen = {
    res.getString("date"),
    res.getString("posting_text"),
    res.getString("category"),
    res.getString("amount"),
    res.getString("balance")
  };

  private final Haushalt haushalt;
  private final Datenbasis db;
  private String registerName;
  
  public RegisterTableModel(Haushalt haushalt, Datenbasis db, String name) {
    this.haushalt = haushalt;
  	this.db = db;
    this.registerName = name;
  }

	public void setRegisterName(String registerName) {
   this.registerName = registerName; 
  }
  
  public String toString() {
		return registerName;
	}

	public void entferneBuchung(int row) {
		db.entferneBuchung(registerName, row);
		this.fireTableRowsDeleted(row, row);
	}
	
  public int getColumnCount() {
    return spaltenNamen.length;
  }

  public int getRowCount() {
  	// eine Zeile mehr, da in der letzten Zeile die Eingabe
  	// möglich ist
    return db.getAnzahlBuchungen(registerName) + 1;
  }

  public String getColumnName(int col) {
    return spaltenNamen[col];
  }

  public Class<?> getColumnClass(int col) {
  	switch(col) {
  		case 0 : return Datum.class;
  		case 1 : return String.class; 
  		case 2 : return Object.class;
  		default: return Euro.class;
  	}
  }

  public Object getValueAt(int row, int col) {
    if(DEBUG)
      System.out.println("RegisterTableModel: getValue @ "+row+", "+col);
  	if(row < db.getAnzahlBuchungen(registerName)) {
  	  AbstractBuchung buchung = db.getBuchung(registerName, row);
  		switch(col) {
  			case 0 : return buchung.getDatum();
  			case 1 : return buchung.getText();
  			case 2 : return buchung.getKategorie();
  			case 3 : 
  			  if(buchung.getClass() == Umbuchung.class) {
            // Wenn es eine Umbuchung ist und wir im 
            // 'falschen' Register sind muss der negative 
            // Wert ausgegeben werden
            UmbuchungKategorie kategorie = (UmbuchungKategorie) buchung.getKategorie();
  			    if(registerName.equals(""+kategorie.getQuelle()) && !kategorie.isSelbstbuchung())
  			      return Euro.NULL_EURO.sub(buchung.getWert());
  			  }
 			    return buchung.getWert();
  			default: return db.getRegisterSaldo(registerName, row); 
  		}
  	}
  	
  	// Werte für die letzte Zeile gibt es noch nicht:
  	switch(col) {
  		case 0: return new Datum();
  		case 1: return "";
  		case 2: return EinzelKategorie.SONSTIGES;
  		case 3: return new Euro();
  		default: return db.getRegisterSaldo(registerName, row-1);
  	}
  }

  public boolean isCellEditable(int row, int col) {
    if(col == 4)
      return false;
    return true;
  }

  public void setValueAt(Object value, int row, int col) {
    if(DEBUG)
      System.out.println("RegisterTableModel: setValue ("+value+") @ "+row+", "+col);
    AbstractBuchung buchung;
		if(row == db.getAnzahlBuchungen(registerName)) {
      // Wenn ein Wert in der letzten Zeile eingegeben wurde,
      // muss eine neue Buchung erzeugt werden.
		  buchung = new StandardBuchung();
      row = db.addStandardBuchung(registerName, (StandardBuchung)buchung);
      fireTableRowsInserted(row,row);
    }
		else
		  buchung = db.getBuchung(registerName, row);
		
		int pos;
		switch(col) {
			case 0: 
        buchung.setDatum(new Datum(""+value));
				pos = db.buchungNeusortieren(registerName, buchung);
				fireTableDataChanged();
				haushalt.selektiereBuchung(registerName, pos);
        if(buchung.getClass() == Umbuchung.class) {
          // Falls es eine Umbuchung ist, muss auch das
          // zweite Register neusortiert werden
          UmbuchungKategorie kategorie = (UmbuchungKategorie) buchung.getKategorie();
          String regname2 = registerName.equals(""+kategorie.getQuelle())?""+kategorie.getZiel():""+kategorie.getQuelle();
          db.buchungNeusortieren(regname2, buchung);
          haushalt.registerVeraendert(regname2);
        }
				break;
			case 1:
				AbstractBuchung gemerkteBuchung = db.findeGemerkteBuchung(""+value);
				if(haushalt.gemerkteBuchungen() && (gemerkteBuchung != null)) {
				  AbstractBuchung neueBuchung = (AbstractBuchung) gemerkteBuchung.clone();
				  neueBuchung.setDatum(buchung.getDatum());
				  if(!buchung.getWert().equals(Euro.NULL_EURO))
				    neueBuchung.setWert(buchung.getWert());
				  pos = db.ersetzeBuchung(registerName, row, neueBuchung);
          fireTableDataChanged();
          haushalt.selektiereBuchung(registerName, pos);
				}
				else {
					buchung.setText(""+value);
					db.buchungMerken(buchung);
				}
				break;
			case 2:
        buchung.setKategorie((Kategorie) value);
				db.buchungMerken(buchung);
				break;
			case 3: 
        if(buchung.getClass() == Umbuchung.class) {
          // Wenn es eine Umbuchung ist und wir im 
          // 'falschen' Register sind muss der negative 
          // Wert weitergegeben werden
          UmbuchungKategorie kategorie = (UmbuchungKategorie) buchung.getKategorie();
          if(registerName.equals(""+kategorie.getQuelle()) && !kategorie.isSelbstbuchung())
            buchung.setWert(Euro.NULL_EURO.sub(new Euro(""+value)));
          else
            buchung.setWert(new Euro(""+value));
        }
        else
          buchung.setWert(new Euro(""+value));
				db.buchungMerken(buchung);
				pos = db.buchungNeusortieren(registerName, buchung);
				fireTableDataChanged();
				haushalt.selektiereBuchung(registerName, pos);
				break;
		}
    db.setGeaendert();
  }

}