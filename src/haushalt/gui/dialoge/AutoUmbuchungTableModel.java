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

package haushalt.gui.dialoge;

import haushalt.daten.Datenbasis;
import haushalt.daten.Datum;
import haushalt.daten.Euro;
import haushalt.daten.Umbuchung;
import haushalt.daten.UmbuchungKategorie;
import haushalt.gui.TextResource;

import javax.swing.table.AbstractTableModel;

/**
 * Ermöglicht die Darstellung von wiederkehrenden (automatischen)
 * Umbuchungen in einer Swing-Tabelle.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.02.27
 */

/*
 * 2007.02.27 Internationalisierung
 * 2007.01.30 BugFix: AutoStandardBuchung wurde statt der AutoUmbuchung gelöscht
 * 2006.06.16 Erste Version
 */

public class AutoUmbuchungTableModel extends AbstractTableModel {

	private static final boolean DEBUG = false;
	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	private final String[] spaltenNamen = {
			res.getString("date"),
			res.getString("posting_text"),
			res.getString("amount"),
			res.getString("source_register"),
			res.getString("destination_register"),
			res.getString("interval")
	};

	private final Datenbasis db;

	public AutoUmbuchungTableModel(final Datenbasis db) {
		this.db = db;
	}

	public void entferneZeile(final int row) {
		this.db.entferneAutoUmbuchung(row);
		fireTableRowsDeleted(row, row);
	}

	public int getColumnCount() {
		return this.spaltenNamen.length;
	}

	public int getRowCount() {
		return this.db.getAnzahlAutoUmbuchungen() + 1;
	}

	@Override
	public String getColumnName(final int columnIndex) {
		return this.spaltenNamen[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		switch (columnIndex) {
		case 0:
			return Datum.class;
		case 2:
			return Euro.class;
		default:
			return String.class;
		}
	}

	public Object getValueAt(final int row, final int col) {
		if (DEBUG) {
			System.out.println("AutoUmbuchungTableModel: getValue @ " + row + ", " + col);
		}
		if (row < this.db.getAnzahlAutoUmbuchungen()) {
			final Umbuchung buchung = this.db.getAutoUmbuchung(row);
			final UmbuchungKategorie registerPaar = this.db.getAutoUmbuchungRegister(row);
			switch (col) {
			case 0:
				return buchung.getDatum();
			case 1:
				return buchung.getText();
			case 2:
				return buchung.getWert();
			case 3:
				return registerPaar.getQuelle();
			case 4:
				return registerPaar.getZiel();
			default:
				return res.getAutoBuchungIntervallName(this.db.getAutoUmbuchungIntervall(row));
			}
		}

		// Werte für die letzte Zeile gibt es noch nicht:
		switch (col) {
		case 0:
			return new Datum();
		case 2:
			return new Euro();
		default:
			return "";
		}
	}

	@Override
	public boolean isCellEditable(final int row, final int col) {
		return true;
	}

	@Override
	public void setValueAt(final Object value, final int row, final int col) {
		if (DEBUG) {
			System.out.println("AutoUmbuchungTableModel: setValue (" + value + ") @ " + row + ", " + col);
		}
		if (row == this.db.getAnzahlAutoUmbuchungen()) {
			// Wenn ein Wert in der letzten Zeile eingegeben wurde,
			// muss eine neue Buchung erzeugt werden.
			this.db.addAutoUmbuchung();
			fireTableRowsInserted(row, row);
		}
		final Umbuchung buchung = this.db.getAutoUmbuchung(row);
		final UmbuchungKategorie registerPaar = this.db.getAutoUmbuchungRegister(row);
		switch (col) {
		case 0:
			buchung.setDatum(new Datum("" + value));
			break;
		case 1:
			buchung.setText("" + value);
			break;
		case 2:
			buchung.setWert(new Euro("" + value));
			break;
		case 3:
			registerPaar.setQuelle(this.db.findeOderErzeugeRegister("" + value));
			break;
		case 4:
			registerPaar.setZiel(this.db.findeOderErzeugeRegister("" + value));
			break;
		default:
			this.db.setAutoUmbuchungIntervall(row, res.getAutoBuchungIntervallIndex("" + value));
			break;
		}
		this.db.setGeaendert();
		fireTableRowsUpdated(row, row);
	}

}
