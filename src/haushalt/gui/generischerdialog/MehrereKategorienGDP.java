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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2008.01.17
 */

/*
 * 2008.01.17 "Alle Hinzufügen" / "Alle Entfernen" ergänzt
 * 2008.01.17 Internationalisierung
 * 2006.01.26 Verwendung der Unterkategorien einstellbar
 */

public class MehrereKategorienGDP extends AbstractGDPane {

	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();

	private final JPanel negPane = new JPanel();
	private final JPanel posPane = new JPanel();
	private final JPanel buttonPane = new JPanel();
	private final JPanel centralPane = new JPanel();
	private final JList negList;
	private DefaultListModel negListModel;
	private final JLabel negLabel = new JLabel(RES.getString("unused_categories"));
	private final JList posList;
	private DefaultListModel posListModel;
	private final JLabel posLabel = new JLabel(RES.getString("used_categories"));
	private final JButton alleHinzufuegen = new JButton(">>");
	private final JButton hinzufuegen = new JButton(">");
	private final JButton entfernen = new JButton("<");
	private final JButton alleEntfernen = new JButton("<<");
	private final JCheckBox checkBox = new JCheckBox(RES.getString("use_subcategories"), true);
	private final Datenbasis db;

	public MehrereKategorienGDP(final String text, final Datenbasis datenbasis) {
		super(text);
		this.db = datenbasis;
		final EinzelKategorie[] posKategorien = this.db.getKategorien(unterkategorienVerwenden());
		this.negListModel = new DefaultListModel();
		this.negList = new JList(this.negListModel);
		this.negList.setPrototypeCellValue("XXXXXXXXXXXXXXXXXXXXXXXXX");
		final JScrollPane negScrollPane = new JScrollPane(this.negList);
		negScrollPane.setAlignmentX(LEFT_ALIGNMENT);
		negScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.negLabel.setLabelFor(this.negList);
		this.negLabel.setAlignmentX(LEFT_ALIGNMENT);
		this.negPane.setLayout(new BoxLayout(this.negPane, BoxLayout.PAGE_AXIS));
		this.negPane.add(this.negLabel);
		this.negPane.add(negScrollPane);

		this.posListModel = new DefaultListModel();
		this.posListModel.setSize(posKategorien.length);
		for (int i = 0; i < posKategorien.length; i++) {
			this.posListModel.set(i, posKategorien[i]);
		}
		this.posList = new JList(this.posListModel);
		this.posList.setPrototypeCellValue("XXXXXXXXXXXXXXXXXXXXXXXXX");
		final JScrollPane posScrollPane = new JScrollPane(this.posList);
		posScrollPane.setAlignmentX(LEFT_ALIGNMENT);
		posScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.posLabel.setLabelFor(this.posList);
		this.posLabel.setAlignmentX(LEFT_ALIGNMENT);
		this.posPane.setLayout(new BoxLayout(this.posPane, BoxLayout.PAGE_AXIS));
		this.posPane.add(this.posLabel);
		this.posPane.add(posScrollPane);

		this.alleEntfernen.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				MehrereKategorienGDP.this.posListModel.removeAllElements();
				MehrereKategorienGDP.this.negListModel.removeAllElements();
				final EinzelKategorie[] kategorien = MehrereKategorienGDP.this.db.getKategorien(unterkategorienVerwenden());
				for (int i = 0; i < kategorien.length; i++) {
					MehrereKategorienGDP.this.negListModel.addElement(kategorien[i]);
				}
			}
		});
		this.alleEntfernen.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.buttonPane.add(this.alleEntfernen);
		this.entfernen.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				if (!MehrereKategorienGDP.this.posList.isSelectionEmpty()) {
					final Object[] kategorie = MehrereKategorienGDP.this.posList.getSelectedValues();
					for (int i = 0; i < kategorie.length; i++) {
						MehrereKategorienGDP.this.posListModel.removeElement(kategorie[i]);
						MehrereKategorienGDP.this.negListModel.addElement(kategorie[i]);
					}
				}
			}
		});
		this.entfernen.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.buttonPane.add(this.entfernen);
		this.hinzufuegen.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				if (!MehrereKategorienGDP.this.negList.isSelectionEmpty()) {
					final Object[] kategorie = MehrereKategorienGDP.this.negList.getSelectedValues();
					for (int i = 0; i < kategorie.length; i++) {
						MehrereKategorienGDP.this.negListModel.removeElement(kategorie[i]);
						MehrereKategorienGDP.this.posListModel.addElement(kategorie[i]);
					}
				}
			}
		});
		this.hinzufuegen.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.buttonPane.add(this.hinzufuegen);
		this.alleHinzufuegen.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				MehrereKategorienGDP.this.posListModel.removeAllElements();
				MehrereKategorienGDP.this.negListModel.removeAllElements();
				final EinzelKategorie[] kategorien = MehrereKategorienGDP.this.db.getKategorien(unterkategorienVerwenden());
				for (int i = 0; i < kategorien.length; i++) {
					MehrereKategorienGDP.this.posListModel.addElement(kategorien[i]);
				}
			}
		});
		this.alleHinzufuegen.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.buttonPane.add(this.alleHinzufuegen);
		this.buttonPane.setLayout(new BoxLayout(this.buttonPane, BoxLayout.Y_AXIS));
		this.centralPane.add(this.negPane);
		this.centralPane.add(this.buttonPane);
		this.centralPane.add(this.posPane);

		add(this.centralPane);
		this.checkBox.addItemListener(new ItemListener() {

			public void itemStateChanged(final ItemEvent e) {
				refreshRegisterUndKategorien();
				refreshWert();
			}
		});
		add(this.checkBox);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		refreshWert();
	}

	private boolean unterkategorienVerwenden() {
		return this.checkBox.isSelected();
	}

	@Override
	protected void refreshWert() {
		final EinzelKategorie[] kategorien = new EinzelKategorie[this.posListModel.getSize()];
		this.posListModel.copyInto(kategorien);
		setWert(kategorien);
	}

	@Override
	public void refreshRegisterUndKategorien() {
		final EinzelKategorie[] neueKategorien = this.db.getKategorien(unterkategorienVerwenden());
		final EinzelKategorie[] posKategorien = new EinzelKategorie[this.posListModel.getSize()];
		this.posListModel.copyInto(posKategorien);
		this.negListModel = new DefaultListModel();
		this.posListModel = new DefaultListModel();
		this.negList.setModel(this.negListModel);
		this.posList.setModel(this.posListModel);
		for (int i = 0; i < neueKategorien.length; i++) {
			boolean gefunden = false;
			for (int j = 0; j < posKategorien.length; j++) {
				if (neueKategorien[i] == posKategorien[j]) {
					gefunden = true;
				}
			}
			if (gefunden) {
				this.posListModel.addElement(neueKategorien[i]);
			} else {
				this.negListModel.addElement(neueKategorien[i]);
			}
		}
	}

	@Override
	public JComponent getZentraleKomponente() {
		return this.posList;
	}

	public boolean getUnterkategorienVerwenden() {
		return this.checkBox.isSelected();
	}

	@Override
	public void laden(final DataInputStream in) throws IOException {
		this.checkBox.setSelected(in.readBoolean());
		final EinzelKategorie[] kategorien = this.db.getKategorien(unterkategorienVerwenden());
		this.negListModel = new DefaultListModel();
		this.posListModel = new DefaultListModel();
		this.negList.setModel(this.negListModel);
		this.posList.setModel(this.posListModel);
		final int anzahl = in.readInt();
		final String[] katname = new String[anzahl];
		for (int i = 0; i < anzahl; i++) {
			katname[i] = in.readUTF();
		}
		for (int j = 0; j < kategorien.length; j++) {
			boolean gefunden = false;
			for (int i = 0; i < anzahl; i++) {
				if (katname[i].equals("" + kategorien[j])) {
					gefunden = true;
				}
			}
			if (gefunden) {
				this.posListModel.addElement(kategorien[j]);
			} else {
				this.negListModel.addElement(kategorien[j]);
			}
		}
	}

	@Override
	public void speichern(final DataOutputStream out) throws IOException {
		out.writeBoolean(unterkategorienVerwenden());
		out.writeInt(this.posListModel.size());
		for (int i = 0; i < this.posListModel.size(); i++) {
			out.writeUTF("" + this.posListModel.get(i));
		}
	}

}
