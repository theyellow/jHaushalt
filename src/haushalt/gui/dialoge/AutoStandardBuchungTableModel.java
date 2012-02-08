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

package haushalt.gui.dialoge;

import haushalt.daten.AbstractBuchung;
import haushalt.daten.Datenbasis;
import haushalt.daten.Datum;
import haushalt.daten.Euro;
import haushalt.daten.EinzelKategorie;
import haushalt.daten.Kategorie;
import haushalt.gui.TextResource;

import javax.swing.table.AbstractTableModel;

/**
 * Erm�glicht die Darstellung der wiederkehrenden (automatischen) 
 * Standard-Buchungen in einer Swing-Tabelle.
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.02.27
 */

/*
 * 2007.02.27 Internationalisierung
 * 2004.08.22 Version 2.0
 */
public class AutoStandardBuchungTableModel extends AbstractTableModel {
  private static final boolean DEBUG = false;
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();
	
	private final String[] spaltenNamen = {
     res.getString("date"),
     res.getString("posting_text"),
     res.getString("category"),
     res.getString("amount"),
     res.getString("register"),
     res.getString("interval")
  };

  private final Datenbasis db;
	
	public AutoStandardBuchungTableModel(Datenbasis db) {
		this.db = db;
	}

  public void entferneZeile(int row) {
    db.entferneAutoStandardBuchung(row);
    fireTableRowsDeleted(row, row);
  }
  
	public int getColumnCount() {
		return spaltenNamen.length;
	}

  public int getRowCount() {
    return db.getAnzahlAutoStandardBuchungen()+1;
  }

  public String getColumnName(int columnIndex) {
    return spaltenNamen[columnIndex];
  }

  public Class<?> getColumnClass(int columnIndex) {
    switch(columnIndex) {
      case 0: return Datum.class;
      case 2: return EinzelKategorie.class;
      case 3: return Euro.class;
      default: return String.class;
    }
  }

	public Object getValueAt(int row, int col) {
    if(DEBUG)
      System.out.println("AutoStandardBuchungTableModel: getValue @ "+row+", "+col);
		if(row < db.getAnzahlAutoStandardBuchungen()) {
	    AbstractBuchung buchung = db.getAutoStandardBuchung(row);
	    switch(col) {
        case 0 : return buchung.getDatum();
        case 1 : return buchung.getText();
        case 2 : return buchung.getKategorie();
        case 3 : return buchung.getWert();
        case 4 : return db.getAutoStandardBuchungRegister(row);
        default: return res.getAutoBuchungIntervallName(db.getAutoStandardBuchungIntervall(row));
      }
    }

    // Werte f�r die letzte Zeile gibt es noch nicht:
    switch(col) {
      case 0: return new Datum();
      case 1: return "";
      case 2: return EinzelKategorie.SONSTIGES;
      case 3: return new Euro();
      case 4 : return "";
      default: return "";
    }
	}

  public boolean isCellEditable(int row, int col) {
    return true;
  }

	public void setValueAt(Object value, int row, int col) {
    if(DEBUG)
      System.out.println("AutoStandardBuchungTableModel: setValue ("+value+") @ "+row+", "+col);
    if(row == db.getAnzahlAutoStandardBuchungen()) {
      // Wenn ein Wert in der letzten Zeile eingegeben wurde,
      // muss eine neue Buchung erzeugt werden.
      db.addAutoStandardBuchung();
      this.fireTableRowsInserted(row,row);
    }  
    AbstractBuchung buchung = db.getAutoStandardBuchung(row);
    switch(col) {
      case 0: 
        buchung.setDatum(new Datum(""+value));
        break;
      case 1: 
        buchung.setText(""+value);
        break;
      case 2:
        buchung.setKategorie((Kategorie) value);
        break;
      case 3: 
        buchung.setWert(new Euro(""+value));
        break;
      case 4:
        db.setAutoStandardBuchungRegister(row, ""+value);
        break;
      default:
        db.setAutoStandardBuchungIntervall(row, res.getAutoBuchungIntervallIndex(""+value));
        break;
    }   
    this.fireTableRowsUpdated(row, row);
	}

}
