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

import haushalt.daten.EinzelKategorie;
import haushalt.daten.MehrfachKategorie;
import haushalt.daten.UmbuchungKategorie;

import java.awt.Color;
import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */
public class KategorieRenderer extends DefaultTableCellRenderer {

	private static final boolean DEBUG = false;
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(KategorieRenderer.class.getName());

	@Override
	public Component getTableCellRendererComponent(
		final JTable table,
		final Object value,
		final boolean isSelected,
		final boolean hasFocus,
		final int row,
		final int col) {
		final JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		if (value == null) {
			comp.setBackground(Color.yellow);
		} else if (value.getClass() == EinzelKategorie.class) {
			// OK
			if (DEBUG) {
				LOGGER.info("Nichts zu tun, ist EinzelKategorie");
			}
		} else if (value.getClass() == MehrfachKategorie.class) {
			// OK
			if (DEBUG) {
				LOGGER.info("Nichts zu tun, ist MehrfachKategorie");
			}
		} else if (value.getClass() == UmbuchungKategorie.class) {
			final UmbuchungKategorie kategorie = (UmbuchungKategorie) value;
			final String regname = "" + table.getModel();
			comp.setText("" + kategorie.getPartnerRegister(regname));
		} else {
			comp.setBackground(Color.yellow);
			if (DEBUG) {
				LOGGER.info("KategorieRenderer: Unbekannter Typ - " + value.getClass());
				LOGGER.info("Superclass - " + value.getClass().getSuperclass());
			}
		}
		return comp;
	}

}
