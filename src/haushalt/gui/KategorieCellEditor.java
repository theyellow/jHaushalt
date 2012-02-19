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

package haushalt.gui;

import haushalt.daten.Datenbasis;
import haushalt.daten.EinzelKategorie;
import haushalt.daten.MehrfachKategorie;
import haushalt.daten.UmbuchungKategorie;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 * Cell-Editor für die Kategorie.
 * @author Dr. Lars H. Hahn
 * @version 2.1.1/2006.04.21
 */

/*
 * 2006.04.21 BugFix: Bei der Änderung eine Umbuchung wurden
 *            Ziel- und Quellregister vertauscht
 * 2006.02.13 Anpassung an die neue abstrakte Klasse 
 *            'Kategorie'
 * 2004.08.22 Erste Version
 */

public class KategorieCellEditor extends DefaultCellEditor {
  private static final boolean DEBUG = false;
  private static final long serialVersionUID = 1L;

  private final Haushalt haushalt;
  private final Datenbasis db;
  private JComboBox comboBox;
  private UmbuchungKategorie umbuchungKategorie = null;
  private boolean quellregister = true;

  public KategorieCellEditor(Haushalt haushalt, Datenbasis db) {
    super(new JComboBox());
    comboBox = (JComboBox) getComponent();
    this.haushalt = haushalt;
    this.db = db;
    setClickCountToStart(1);
  }

  public Object getCellEditorValue() {
    Object kategorie = comboBox.getSelectedItem();
    if(DEBUG) 
      System.out.println("KategorieCellEditor: getCellEditorValue = "+kategorie);
    if(kategorie.getClass() == EinzelKategorie.class)
      return kategorie;
    else if (kategorie.getClass() == String.class) {
      Object neuesRegister = db.findeOderErzeugeRegister(""+kategorie);
      if(quellregister)
        umbuchungKategorie.setZiel(neuesRegister);
      else
        umbuchungKategorie.setQuelle(neuesRegister);
      haushalt.alleRegisterVeraendert();
      return umbuchungKategorie;
    }
    else if(DEBUG) // (SplitBuchungen werden vorher umgeleitet.)
      System.out.println("-E- KategorieCellEditor: Unerwartete Klasse: "+kategorie.getClass());
    return null;
  }

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
		comboBox = (JComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, col);
		if(value == null)
		  value = EinzelKategorie.SONSTIGES;
		if(value.getClass() == EinzelKategorie.class) {
			comboBox = new JComboBox(db.getKategorien(true));
			comboBox.setSelectedItem(value);
		}
		else if(value.getClass() == MehrfachKategorie.class) {
			haushalt.selektiereBuchung(""+table.getModel(), row);
 			haushalt.splitten();
 			comboBox = null;
			cancelCellEditing();
    }
		else if(value.getClass() == UmbuchungKategorie.class) {
		  umbuchungKategorie = (UmbuchungKategorie) ((UmbuchungKategorie)value).clone();
			String regname = ""+table.getModel();
			comboBox = new JComboBox(db.getRegisterNamen());
			// Ob das Quell- oder Ziel-Register in der Combo-Box
			// ausgewählt werden muss, ist davon abhängig in
			// welchem der beiden Register sich die Umbuchung
			// befindet. 
      quellregister = (db.findeOderErzeugeRegister(regname) == umbuchungKategorie.getQuelle());
      comboBox.setSelectedItem(""+umbuchungKategorie.getPartnerRegister(regname));      
		}
		else if(DEBUG)
			System.out.println("-E- KategorieCellEditor: Unerwartete Klasse: "+value.getClass());
		return comboBox;
  }
  
}