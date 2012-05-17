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
 * along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
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
import haushalt.daten.EinzelKategorie;
import haushalt.daten.Euro;
import haushalt.daten.Kategorie;
import haushalt.daten.StandardBuchung;
import haushalt.daten.Umbuchung;
import haushalt.daten.UmbuchungKategorie;

import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

/**
 * Ermöglicht die Darstellung eines Registers in einer Swing-Tabelle.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5/2006.07.04
 */

/*
 * 2006.07.04 Internationalisierung
 * 2006.02.01 BugFix: Umbuchung wird jetzt bei der Änderung
 * des Datums im zweiten Register neusortiert
 * 2006.02.01 BugFix: Änderung des Wertes einer Umbuchung
 * im zweiten Register führte zu einem Wechsel
 * des Vorzeichens
 */

public class RegisterTableModel extends AbstractTableModel {

	private static final boolean DEBUG = false;
	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();
	private static final Logger LOGGER = Logger.getLogger(RegisterTableModel.class.getName());

	private static final String[] SPALTEN_NAMEN = {
			RES.getString("date"), RES.getString("posting_text"), RES.getString("category"), RES.getString("amount"),
			RES.getString("balance")
	};

	private final Haushalt haushalt;
	private final Datenbasis db;
	private String registerName;

	public RegisterTableModel(final Haushalt haushalt, final Datenbasis db, final String name) {
		this.haushalt = haushalt;
		this.db = db;
		this.registerName = name;
	}

	public void setRegisterName(final String registerName) {
		this.registerName = registerName;
	}

	@Override
	public String toString() {
		return this.registerName;
	}

	public void entferneBuchung(final int row) {
		this.db.entferneBuchung(this.registerName, row);
		fireTableRowsDeleted(row, row);
	}

	public int getColumnCount() {
		return SPALTEN_NAMEN.length;
	}

	public int getRowCount() {
		// eine Zeile mehr, da in der letzten Zeile die Eingabe
		// möglich ist
		return this.db.getAnzahlBuchungen(this.registerName) + 1;
	}

	@Override
	public String getColumnName(final int col) {
		return SPALTEN_NAMEN[col];
	}

	@Override
	public Class<?> getColumnClass(final int col) {
		switch (col) {
		case 0:
			return Datum.class;
		case 1:
			return String.class;
		case 2:
			return Object.class;
		default:
			return Euro.class;
		}
	}

	public Object getValueAt(final int row, final int col) {
		if (DEBUG) {
			LOGGER.info("RegisterTableModel: getValue @ " + row + ", " + col);
		}
		if (row < this.db.getAnzahlBuchungen(this.registerName)) {
			final AbstractBuchung buchung = this.db.getBuchung(this.registerName, row);
			switch (col) {
			case 0:
				return buchung.getDatum();
			case 1:
				return buchung.getText();
			case 2:
				return buchung.getKategorie();
			case 3:
				if (buchung.getClass() == Umbuchung.class) {
					// Wenn es eine Umbuchung ist und wir im
					// 'falschen' Register sind muss der negative
					// Wert ausgegeben werden
					final UmbuchungKategorie kategorie = (UmbuchungKategorie) buchung.getKategorie();
					if (this.registerName.equals("" + kategorie.getQuelle()) && !kategorie.isSelbstbuchung()) {
						return Euro.NULL_EURO.sub(buchung.getWert());
					}
				}
				return buchung.getWert();
			default:
				return this.db.getRegisterSaldo(this.registerName, row);
			}
		}

		// Werte für die letzte Zeile gibt es noch nicht:
		switch (col) {
		case 0:
			return new Datum();
		case 1:
			return "";
		case 2:
			return EinzelKategorie.SONSTIGES;
		case 3:
			return new Euro();
		default:
			return this.db.getRegisterSaldo(this.registerName, row - 1);
		}
	}

	@Override
	public boolean isCellEditable(final int row, final int col) {
		if (col == 4) {
			return false;
		}
		return true;
	}

	@Override
	public void setValueAt(final Object value, int row, final int col) {
		if (DEBUG) {
			LOGGER.info("RegisterTableModel: setValue (" + value + ") @ " + row + ", " + col);
		}
		AbstractBuchung buchung;
		if (row == this.db.getAnzahlBuchungen(this.registerName)) {
			// Wenn ein Wert in der letzten Zeile eingegeben wurde,
			// muss eine neue Buchung erzeugt werden.
			buchung = new StandardBuchung();
			row = this.db.addStandardBuchung(this.registerName, (StandardBuchung) buchung);
			fireTableRowsInserted(row, row);
		}
		else {
			buchung = this.db.getBuchung(this.registerName, row);
		}

		int pos;
		switch (col) {
		case 0:
			buchung.setDatum(new Datum("" + value));
			pos = this.db.buchungNeusortieren(this.registerName, buchung);
			fireTableDataChanged();
			this.haushalt.selektiereBuchung(this.registerName, pos);
			if (buchung.getClass() == Umbuchung.class) {
				// Falls es eine Umbuchung ist, muss auch das
				// zweite Register neusortiert werden
				final UmbuchungKategorie kategorie = (UmbuchungKategorie) buchung.getKategorie();
				final String regname2 = this.registerName.equals("" + kategorie.getQuelle()) ? "" + kategorie.getZiel()
						: ""
								+ kategorie.getQuelle();
				this.db.buchungNeusortieren(regname2, buchung);
				this.haushalt.registerVeraendert(regname2);
			}
			break;
		case 1:
			final AbstractBuchung gemerkteBuchung = this.db.findeGemerkteBuchung("" + value);
			if (this.haushalt.gemerkteBuchungen() && (gemerkteBuchung != null)) {
					AbstractBuchung neueBuchung = null;
					try {
						neueBuchung = (AbstractBuchung) gemerkteBuchung.clone();
					} catch (final CloneNotSupportedException e) {
						LOGGER.warning("Cloning error. This should never happen.");
					}
				neueBuchung.setDatum(buchung.getDatum());
				if (!buchung.getWert().equals(Euro.NULL_EURO)) {
					neueBuchung.setWert(buchung.getWert());
				}
				pos = this.db.ersetzeBuchung(this.registerName, row, neueBuchung);
				fireTableDataChanged();
				this.haushalt.selektiereBuchung(this.registerName, pos);
			}
			else {
				buchung.setText("" + value);
				this.db.buchungMerken(buchung);
			}
			break;
		case 2:
			buchung.setKategorie((Kategorie) value);
			this.db.buchungMerken(buchung);
			break;
		case 3:
			if (buchung.getClass() == Umbuchung.class) {
				// Wenn es eine Umbuchung ist und wir im
				// 'falschen' Register sind muss der negative
				// Wert weitergegeben werden
				final UmbuchungKategorie kategorie = (UmbuchungKategorie) buchung.getKategorie();
				if (this.registerName.equals("" + kategorie.getQuelle()) && !kategorie.isSelbstbuchung()) {
					buchung.setWert(Euro.NULL_EURO.sub(new Euro("" + value)));
				}
				else {
					buchung.setWert(new Euro("" + value));
				}
			}
			else {
				buchung.setWert(new Euro("" + value));
			}
			this.db.buchungMerken(buchung);
			pos = this.db.buchungNeusortieren(this.registerName, buchung);
			fireTableDataChanged();
			this.haushalt.selektiereBuchung(this.registerName, pos);
			break;
			default:
				break;
		}
		this.db.setGeaendert();
	}

}