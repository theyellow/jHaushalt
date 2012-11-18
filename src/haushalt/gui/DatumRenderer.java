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

package haushalt.gui;

import haushalt.daten.Datum;

import java.awt.Color;
import java.awt.Component;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.4/2008.04.15
 * @since 2.5.4
 */

/*
 * 2008.04.15 Erste Version
 */
public class DatumRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private final Properties properties;

	public DatumRenderer(final Properties properties) {
		this.properties = properties;
	}

	@Override
	public Component getTableCellRendererComponent(
			final JTable table,
			final Object value,
			final boolean isSelected,
			final boolean hasFocus,
			final int row,
			final int col) {
		final JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		final Color farbeZukunft = new Color(Integer.valueOf(this.properties.getProperty("jhh.opt.zukunft", "16777088"))); // #ffff80
		final Datum heute = new Datum();
		if (!isSelected) {
			if (heute.compareTo((Datum) value) < 0) {
				comp.setBackground(farbeZukunft);
			}
			else {
				comp.setBackground(Color.white);
			}
		}
		return comp;
	}

}
