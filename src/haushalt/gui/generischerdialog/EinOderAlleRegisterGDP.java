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
 * 2006.01.27 Übergabe der Datenbasis, statt der Register-Namen
 */

public class EinOderAlleRegisterGDP extends AbstractGDPane {

	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();

	private final JComboBox comboBox;
	private final JCheckBox checkBox;
	private final Datenbasis db;

	public EinOderAlleRegisterGDP(final String textAufforderung, final Datenbasis datenbasis, final String auswahl) {
		super(textAufforderung);
		this.db = datenbasis;
		this.comboBox = new JComboBox(this.db.getRegisterNamen());
		this.comboBox.addItemListener(new ItemListener() {

			public void itemStateChanged(final ItemEvent e) {
				refreshWert();
			}
		});
		if (auswahl != null) {
			this.comboBox.setSelectedItem(auswahl);
		}
		this.comboBox.setEnabled(false);
		this.checkBox = new JCheckBox(RES.getString("all_registers"), true);
		this.checkBox.addItemListener(new ItemListener() {

			public void itemStateChanged(final ItemEvent e) {
				if (EinOderAlleRegisterGDP.this.checkBox.isSelected()) {
					EinOderAlleRegisterGDP.this.comboBox.setEnabled(false);
				} else {
					EinOderAlleRegisterGDP.this.comboBox.setEnabled(true);
				}
				refreshWert();
			}
		});
		add(this.checkBox);
		add(this.comboBox);
		refreshWert();
	}

	@Override
	protected void refreshWert() {
		if (this.checkBox.isSelected()) {
			setWert(null);
		} else {
			setWert(this.comboBox.getSelectedItem());
		}
	}

	@Override
	public void refreshRegisterUndKategorien() {
		final String[] register = this.db.getRegisterNamen();
		final String regname = (String) this.comboBox.getSelectedItem();
		this.comboBox.removeAllItems();
		for (int i = 0; i < register.length; i++) {
			this.comboBox.addItem(register[i]);
		}
		if (regname != null) {
			this.comboBox.setSelectedItem(regname);
		}
	}

	@Override
	public JComponent getZentraleKomponente() {
		return this.checkBox;
	}

	@Override
	public void laden(final DataInputStream in) throws IOException {
		this.checkBox.setSelected(in.readBoolean());
		this.comboBox.setSelectedItem(in.readUTF());
	}

	@Override
	public void speichern(final DataOutputStream out) throws IOException {
		out.writeBoolean(this.checkBox.isSelected());
		out.writeUTF((String) this.comboBox.getSelectedItem());
	}

}
