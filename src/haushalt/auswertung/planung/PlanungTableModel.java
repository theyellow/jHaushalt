/*
 * This file is part of jHaushalt.
 * jHaushalt is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * jHaushalt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */

package haushalt.auswertung.planung;

import haushalt.daten.Euro;
import haushalt.gui.TextResource;

import javax.swing.table.AbstractTableModel;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.07.05
 * @since 2.1
 */

/*
 * 2007.07.05 Internationalisierung
 * 2006.02.03 Erste Version
 */

public class PlanungTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();

	private static final String[] SPALTEN_NAMEN = {
			RES.getString("category"), RES.getString("amount"), RES.getString("use_category")};

	private final Planung planung;

	public PlanungTableModel(final Planung planung) {
		this.planung = planung;
	}

	public int getRowCount() {
		return this.planung.getAnzahlKategorien() + 1;
	}

	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(final int col) {
		return SPALTEN_NAMEN[col];
	}

	@Override
	public Class<?> getColumnClass(final int col) {
		switch (col) {
			case 0:
				return String.class;
			case 1:
				return Euro.class;
			case 2:
				return Boolean.class;
			default:
				return null;
		}
	}

	@Override
	public boolean isCellEditable(final int row, final int col) {
		if ((col == 0)
			|| ((col == 1) && !this.planung.kategorieVerwenden(row).booleanValue())
			|| (row == this.planung.getAnzahlKategorien())) {
			return false;
		}
		return true;
	}

	public Object getValueAt(final int row, final int col) {
		if (row == this.planung.getAnzahlKategorien()) {
			switch (col) {
				case 0:
					return RES.getString("total");
				case 1:
					return this.planung.getSumme();
				case 2:
					return Boolean.FALSE;
				default:
					break;
			}
		} else {
			switch (col) {
				case 0:
					return this.planung.getKategorie(row);
				case 1:
					if (this.planung.kategorieVerwenden(row)) {
						return this.planung.getBetrag(row);
					} else {
						return new Euro();
					}
				case 2:
					return this.planung.kategorieVerwenden(row);
				default:
					break;
			}
		}
		return null;
	}

	@Override
	public void setValueAt(final Object value, final int row, final int col) {
		switch (col) {
			case 1:
				this.planung.setBetrag(row, new Euro("" + value));
				break;
			case 2:
				this.planung.setVerwenden(row, (Boolean) value);
				break;
			default:
				break;
		}
		fireTableCellUpdated(this.planung.getAnzahlKategorien(), 1);
	}

}
