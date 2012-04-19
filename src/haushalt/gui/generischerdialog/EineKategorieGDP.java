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

package haushalt.gui.generischerdialog;

import haushalt.daten.Datenbasis;
import haushalt.daten.EinzelKategorie;
import haushalt.gui.TextResource;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2008.01.17
 */

/*
 * 2008.01.17 Internationalisierung
 * 2006.01.25: Verwendung der Unterkategorien einstellbar
 */
public class EineKategorieGDP extends AbstractGDPane {

	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	private final JComboBox comboBox;
	private final JCheckBox checkBox = new JCheckBox(res.getString("use_subcategories"), true);
	private final Datenbasis db;

	public EineKategorieGDP(final String text, final Datenbasis datenbasis, final EinzelKategorie kategorie) {
		super(text);
		this.db = datenbasis;
		this.comboBox = new JComboBox(datenbasis.getKategorien(unterkategorienVerwenden()));
		this.comboBox.addItemListener(new ItemListener() {

			public void itemStateChanged(final ItemEvent e) {
				refreshWert();
			}
		});
		if (kategorie != null) {
			this.comboBox.setSelectedItem(kategorie);
		}
		add(this.comboBox);
		this.checkBox.addItemListener(new ItemListener() {

			public void itemStateChanged(final ItemEvent e) {
				refreshRegisterUndKategorien();
				refreshWert();
			}
		});
		add(this.checkBox);
		refreshWert();
	}

	private boolean unterkategorienVerwenden() {
		return this.checkBox.isSelected();
	}

	@Override
	protected void refreshWert() {
		this.wert = this.comboBox.getSelectedItem();
	}

	@Override
	public void refreshRegisterUndKategorien() {
		final EinzelKategorie kategorie = (EinzelKategorie) this.comboBox.getSelectedItem();
		final EinzelKategorie[] kategorien = this.db.getKategorien(unterkategorienVerwenden());
		this.comboBox.removeAllItems();
		for (int i = 0; i < kategorien.length; i++) {
			this.comboBox.addItem(kategorien[i]);
		}
		if (kategorie != null) {
			if (!kategorie.isHauptkategorie() && !unterkategorienVerwenden()) {
				this.comboBox.setSelectedItem(kategorie.getHauptkategorie());
			}
			else {
				this.comboBox.setSelectedItem(kategorie);
			}
		}
	}

	@Override
	public JComponent getZentraleKomponente() {
		return this.comboBox;
	}

	@Override
	public void laden(final DataInputStream in) throws IOException {
		final String kategorie = in.readUTF();
		for (int i = 0; i < this.comboBox.getItemCount(); i++) {
			if (kategorie.equals("" + this.comboBox.getComponent(i))) {
				this.comboBox.setSelectedIndex(i);
			}
		}
		this.checkBox.setSelected(in.readBoolean());
	}

	@Override
	public void speichern(final DataOutputStream out) throws IOException {
		out.writeUTF("" + this.comboBox.getSelectedItem());
		out.writeBoolean(unterkategorienVerwenden());
	}

}