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

package haushalt.gui.dialoge;

import java.util.ArrayList;

import haushalt.daten.Datenbasis;
import haushalt.daten.Datensatz;
import haushalt.daten.Datum;
import haushalt.daten.Euro;
import haushalt.daten.EinzelKategorie;
import haushalt.daten.Kategorie;
import haushalt.gui.TextResource;

import javax.swing.table.AbstractTableModel;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.02.27
 */

/*
 * 2007.02.27 Internationalisierung
 * 2006.02.10 Erste Version
 */

public class BereinigenTableModel extends AbstractTableModel {
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();
  
  private final String[] spaltenNamen = {
       res.getString("date"),
       res.getString("posting_text"),
       res.getString("category"),
       res.getString("amount"),
       res.getString("register")
    };

  private final Datenbasis db;
  private EinzelKategorie kategorie = EinzelKategorie.SONSTIGES;
  private String buchungstext = "";
  private Boolean unterkategorien = true;
  private ArrayList<Datensatz> cache;
  
  public BereinigenTableModel(Datenbasis db) {
    this.db = db;
    fillCache();
  }

  public int getColumnCount() {
    return spaltenNamen.length;
  }

  public int getRowCount() {
    return cache.size();
  }

  public String getColumnName(int columnIndex) {
    return spaltenNamen[columnIndex];
  }

  public Class<?> getColumnClass(int columnIndex) {
    switch(columnIndex) {
      case 0: return Datum.class;
      case 2: return Kategorie.class;
      case 3: return Euro.class;
      default: return String.class;
    }
  }

  public Object getValueAt(int row, int col) {
    switch(col) {
      case 0 : return cache.get(row).getBuchung().getDatum();
      case 1 : return cache.get(row).getBuchung().getText();
      case 2 : return cache.get(row).getBuchung().getKategorie();
      case 3 : return cache.get(row).getBuchung().getWert();
      case 4 : return cache.get(row).getRegister();
      default: return null;
    }
  }

  public void setBuchungstext(String buchungstext) {
    this.buchungstext = buchungstext;
    fillCache();
    fireTableDataChanged();
  }

  public void setKategorie(EinzelKategorie kategorie) {
    this.kategorie = kategorie;
    fillCache();
    fireTableDataChanged();
  }

  public void setUnterkategorien(Boolean unterkategorien) {
    this.unterkategorien = unterkategorien;
    fillCache();
    fireTableDataChanged();
  }
  
  private void fillCache() {
    cache = db.getBuchungen(buchungstext, kategorie, unterkategorien);
  }

  public void setNeueKategorie(int[] selectedRows, EinzelKategorie neueKategorie) {
    for (int i = 0; i < selectedRows.length; i++)
      cache.get(selectedRows[i]).getBuchung().ersetzeKategorie(kategorie, neueKategorie);
    fillCache();
    fireTableDataChanged();
  }
}
