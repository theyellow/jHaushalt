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
import haushalt.daten.EinzelKategorie;
import haushalt.daten.Euro;
import haushalt.daten.Kategorie;
import haushalt.gui.DeleteableTextField;
import haushalt.gui.EuroRenderer;
import haushalt.gui.Haushalt;
import haushalt.gui.KategorieRenderer;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

/**
 * Ermöglicht das nachträgliche Ändern der Kategorien von
 * Buchungen unabhängig von den Registern
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.02.28
 * @since 2.1
 */

/*
 * 2007.02.28 Internationalisierung
 * 2006.02.10 Erste Version
 */

public class DlgBereinigen extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	private final JComboBox comboBox = new JComboBox();
	private final DefaultListModel listModel = new DefaultListModel();
	private final JList list = new JList(this.listModel);
	private final JCheckBox checkBox = new JCheckBox(res.getString("use_subcategories"), true);
	private final Datenbasis db;

	public DlgBereinigen(final Haushalt haushalt, final Datenbasis db) {
		super(haushalt.getFrame(), res.getString("clean_categories"), true);
		this.db = db;
		final JPanel northPane = new JPanel();
		final JPanel southPane = new JPanel();
		final JPanel eastPane = new JPanel();
		final BereinigenTableModel tableModel = new BereinigenTableModel(db);
		final TableSorter sorter = new TableSorter(tableModel);
		final JTable table = new JTable(sorter);
		sorter.setTableHeader(table.getTableHeader());
		final JLabel label1 = new JLabel(res.getString("category") + ":");
		final JLabel label2 = new JLabel(res.getString("posting_text") + ":");
		this.comboBox.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				if (DlgBereinigen.this.comboBox.getSelectedIndex() == 0) {
					tableModel.setKategorie(null);
				}
				else {
					tableModel.setKategorie((EinzelKategorie) DlgBereinigen.this.comboBox.getSelectedItem());
				}
			}
		});
		final DeleteableTextField buchungstext = new DeleteableTextField(20);
		buchungstext.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				tableModel.setBuchungstext(buchungstext.getText());
			}
		});
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setDefaultRenderer(Kategorie.class, new KategorieRenderer());
		table.setDefaultRenderer(Euro.class, new EuroRenderer());
		this.checkBox.addItemListener(new ItemListener() {

			public void itemStateChanged(final ItemEvent e) {
				kategorienEinlesen();
				tableModel.setUnterkategorien(DlgBereinigen.this.checkBox.isSelected());
			}
		});
		final JButton buttonZuordnen = new JButton(res.getString("reassign_category"));
		buttonZuordnen.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				if (table.getSelectedRowCount() == 0) {
					JOptionPane.showMessageDialog(haushalt.getFrame(),
							res.getString("no_bookings_selected"));
					return;
				}
				if (DlgBereinigen.this.list.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(haushalt.getFrame(),
							res.getString("no_category_selected"));
					return;
				}
				final EinzelKategorie neueKategorie = (EinzelKategorie) DlgBereinigen.this.list.getSelectedValue();
				final int[] selectedRows = table.getSelectedRows();
				final int[] modelRows = new int[selectedRows.length];
				for (int i = 0; i < modelRows.length; i++) {
					modelRows[i] = sorter.modelIndex(selectedRows[i]);
				}
				tableModel.setNeueKategorie(modelRows, neueKategorie);
			}
		});
		final JButton buttonBearbeiten = new JButton(res.getString("edit_category_button"),
				haushalt.bildLaden("Auto16.png"));
		buttonBearbeiten.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				final DlgKategorienBearbeiten dlg = new DlgKategorienBearbeiten(haushalt, db);
				dlg.showDialog();
				kategorienEinlesen();
			}
		});
		final JButton buttonAbbruch = new JButton(res.getString("button_close"));
		buttonAbbruch.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});
		final JScrollPane scrollPane = new JScrollPane(this.list);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		northPane.setBorder(BorderFactory.createTitledBorder(res.getString("selection_filter")));
		northPane.setLayout(new BoxLayout(northPane, BoxLayout.LINE_AXIS));
		northPane.add(label1);
		northPane.add(Box.createRigidArea(new Dimension(5, 0)));
		northPane.add(this.comboBox);
		northPane.add(Box.createRigidArea(new Dimension(10, 0)));
		northPane.add(label2);
		northPane.add(Box.createRigidArea(new Dimension(5, 0)));
		northPane.add(buchungstext);
		southPane.add(buttonAbbruch);
		southPane.add(buttonBearbeiten);
		southPane.add(buttonZuordnen);
		eastPane.add(scrollPane);
		eastPane.add(this.checkBox);
		eastPane.setBorder(BorderFactory.createTitledBorder(res.getString("new_category")));
		eastPane.setLayout(new BoxLayout(eastPane, BoxLayout.Y_AXIS));
		getContentPane().add(northPane, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		getContentPane().add(southPane, BorderLayout.SOUTH);
		getContentPane().add(eastPane, BorderLayout.EAST);
		kategorienEinlesen();
	}

	private void kategorienEinlesen() {
		final Object selectedItem = this.comboBox.getSelectedItem();
		this.comboBox.removeAllItems();
		this.listModel.removeAllElements();
		final EinzelKategorie[] kategorien = this.db.getKategorien(this.checkBox.isSelected());
		this.comboBox.addItem(res.getString("any_category"));
		for (int i = 0; i < kategorien.length; i++) {
			this.comboBox.addItem(kategorien[i]);
			this.listModel.addElement(kategorien[i]);
		}
		if (selectedItem == null) {
			this.comboBox.setSelectedItem(EinzelKategorie.SONSTIGES);
		}
		else {
			this.comboBox.setSelectedItem(selectedItem);
		}
	}

}
