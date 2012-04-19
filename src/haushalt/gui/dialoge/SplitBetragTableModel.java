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

package haushalt.gui.dialoge;

import haushalt.daten.EinzelKategorie;
import haushalt.daten.Euro;
import haushalt.daten.SplitBuchung;
import haushalt.gui.TextResource;

import javax.swing.table.AbstractTableModel;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.05.31
 */

/*
 * 2007.05.31 Internationalisierung
 * 2005.02.18 BugFix: Summe von Split-Buchungen wurde falsch gebildet.
 */

public class SplitBetragTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	private static final String[] spaltenNamen = {
			res.getString("category"),
			res.getString("amount"),
			res.getString("total")
	};
	private final SplitBuchung buchung;

	public SplitBetragTableModel(final SplitBuchung buchung) {
		this.buchung = buchung;
	}

	public void entferneZeile(final int row) {
		this.buchung.loesche(row);
		fireTableRowsDeleted(row, row);
	}

	public int getColumnCount() {
		return spaltenNamen.length;
	}

	public int getRowCount() {
		return this.buchung.getAnzahl() + 1;
	}

	@Override
	public String getColumnName(final int columnIndex) {
		return spaltenNamen[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(final int col) {
		switch (col) {
		case 0:
			return EinzelKategorie.class;
		default:
			return Euro.class;
		}
	}

	public Object getValueAt(final int row, final int col) {
		if (row < this.buchung.getAnzahl()) {
			switch (col) {
			case 0:
				return this.buchung.getKategorie(row);
			case 1:
				return this.buchung.getWert(row);
			case 2: {
				final Euro summe = new Euro();
				for (int i = 0; i <= row; i++) {
					summe.sum(this.buchung.getWert(i));
				}
				return summe;
			}
			}
		}
		if (col == 0) {
			return EinzelKategorie.SONSTIGES;
		}
		return new Euro();
	}

	@Override
	public boolean isCellEditable(final int row, final int col) {
		if (col == 2) {
			return false;
		}
		return true;
	}

	@Override
	public void setValueAt(final Object value, final int row, final int col) {
		if (row == this.buchung.getAnzahl()) {
			this.buchung.add();
		}

		switch (col) {
		case 0:
			this.buchung.setKategorie(row, (EinzelKategorie) value);
			break;
		case 1:
			this.buchung.setWert(row, new Euro("" + value));
			break;
		}
		fireTableDataChanged(); // Neuzeichnen der gesamten Tabelle
	}

}