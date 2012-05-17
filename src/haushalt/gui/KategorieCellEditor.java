/*
 * This file is part of jHaushalt.
 * jHaushalt is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * jHaushalt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with jHaushalt; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */

package haushalt.gui;

import haushalt.daten.Datenbasis;
import haushalt.daten.EinzelKategorie;
import haushalt.daten.MehrfachKategorie;
import haushalt.daten.UmbuchungKategorie;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 * Cell-Editor für die IKategorie.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.1.1/2006.04.21
 */

/*
 * 2006.04.21 BugFix: Bei der Änderung eine Umbuchung wurden
 * Ziel- und Quellregister vertauscht
 * 2006.02.13 Anpassung an die neue abstrakte Klasse
 * 'IKategorie'
 * 2004.08.22 Erste Version
 */

public class KategorieCellEditor extends DefaultCellEditor {

	private static final boolean DEBUG = false;
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(KategorieCellEditor.class.getName());

	private final Haushalt haushalt;
	private final Datenbasis db;
	private JComboBox comboBox;
	private UmbuchungKategorie umbuchungKategorie = null;
	private boolean quellregister = true;

	public KategorieCellEditor(final Haushalt haushalt, final Datenbasis db) {
		super(new JComboBox());
		this.comboBox = (JComboBox) getComponent();
		this.haushalt = haushalt;
		this.db = db;
		setClickCountToStart(1);
	}

	@Override
	public Object getCellEditorValue() {
		final Object kategorie = this.comboBox.getSelectedItem();
		if (DEBUG) {
			LOGGER.info("KategorieCellEditor: getCellEditorValue = " + kategorie);
		}
		if (kategorie.getClass() == EinzelKategorie.class) {
			return kategorie;
		} else if (kategorie.getClass() == String.class) {
			final Object neuesRegister = this.db.findeOderErzeugeRegister("" + kategorie);
			if (this.quellregister) {
				this.umbuchungKategorie.setZiel(neuesRegister);
			} else {
				this.umbuchungKategorie.setQuelle(neuesRegister);
			}
			this.haushalt.alleRegisterVeraendert();
			return this.umbuchungKategorie;
		} else if (DEBUG) {
			LOGGER.info("-E- KategorieCellEditor: Unerwartete Klasse: " + kategorie.getClass());
		}
		return null;
	}

	@Override
	public Component getTableCellEditorComponent(
		final JTable table,
		Object value,
		final boolean isSelected,
		final int row,
		final int col) {
		this.comboBox = (JComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, col);
		if (value == null) {
			value = EinzelKategorie.SONSTIGES;
		}
		if (value.getClass() == EinzelKategorie.class) {
			this.comboBox = new JComboBox(this.db.getKategorien(true));
			this.comboBox.setSelectedItem(value);
		} else if (value.getClass() == MehrfachKategorie.class) {
			this.haushalt.selektiereBuchung("" + table.getModel(), row);
			this.haushalt.splitten();
			this.comboBox = null;
			cancelCellEditing();
		} else if (value.getClass() == UmbuchungKategorie.class) {
			this.umbuchungKategorie = (UmbuchungKategorie) ((UmbuchungKategorie) value).clone();
			final String regname = "" + table.getModel();
			this.comboBox = new JComboBox(this.db.getRegisterNamen());
			// Ob das Quell- oder Ziel-Register in der Combo-Box
			// ausgewählt werden muss, ist davon abhängig in
			// welchem der beiden Register sich die Umbuchung
			// befindet.
			this.quellregister = (this.db.findeOderErzeugeRegister(regname) == this.umbuchungKategorie.getQuelle());
			this.comboBox.setSelectedItem("" + this.umbuchungKategorie.getPartnerRegister(regname));
		} else if (DEBUG) {
			LOGGER.info("-E- KategorieCellEditor: Unerwartete Klasse: " + value.getClass());
		}
		return this.comboBox;
	}

}
