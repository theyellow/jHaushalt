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
import haushalt.gui.DeleteableTextField;
import haushalt.gui.EuroField;
import haushalt.gui.EuroRenderer;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.07.05
 * @since 2.1
 */

/*
 * 2007.07.05 Internationalisierung
 * 2006.04.21 BugFix: Auswahl Unterkategorien/Hochrechnen nach
 * dem Laden richtig setzen
 */
public class PlanungGDP extends AbstractGDPane {

	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	final JTable table;
	final Planung planung;
	final JCheckBox unterkategorien = new JCheckBox(res.getString("use_subcategories"));
	final JCheckBox hochrechnen = new JCheckBox(res.getString("extrapolation_actual_values"));

	public PlanungGDP(final String text, final Planung planung) {
		super(text);

		this.planung = planung;

		this.unterkategorien.setSelected(planung.isUnterkategorien());
		this.hochrechnen.setSelected(planung.isHochrechnen());
		final PlanungTableModel tableModel = new PlanungTableModel(planung);
		this.table = new JTable(tableModel);
		final JPanel paneButton = new JPanel();
		final JButton buttonAlle = new JButton(res.getString("all_categories"));
		final JButton buttonKeine = new JButton(res.getString("no_categories"));

		this.unterkategorien.addItemListener(new ItemListener() {

			public void itemStateChanged(final ItemEvent e) {
				planung.setUnterkategorien(PlanungGDP.this.unterkategorien.isSelected());
				tableModel.fireTableDataChanged();
			}
		});
		this.hochrechnen.addItemListener(new ItemListener() {

			public void itemStateChanged(final ItemEvent e) {
				planung.setHochrechnen(PlanungGDP.this.hochrechnen.isSelected());
			}
		});
		this.table.setSurrendersFocusOnKeystroke(true);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Cell-Editoren erzeugen
		final DeleteableTextField textField = new DeleteableTextField();
		final DefaultCellEditor cellEditor = new DefaultCellEditor(textField);
		this.table.setDefaultEditor(String.class, cellEditor);
		this.table.setDefaultEditor(Euro.class, new DefaultCellEditor(new EuroField()));
		// Cell-Renderer erzeugen
		this.table.setDefaultRenderer(String.class, new DefaultTableCellRenderer());
		this.table.setDefaultRenderer(Euro.class, new EuroRenderer());

		paneButton.add(buttonAlle, null);
		paneButton.add(buttonKeine, null);
		buttonAlle.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				planung.alleVerwenden(true);
				tableModel.fireTableDataChanged();
			}
		});
		buttonKeine.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				planung.alleVerwenden(false);
				tableModel.fireTableDataChanged();
			}
		});

		setLayout(new BorderLayout());
		final JPanel northPane = new JPanel();
		northPane.add(this.unterkategorien);
		northPane.add(this.hochrechnen);
		this.add(northPane, BorderLayout.NORTH);
		this.add(new JScrollPane(this.table), BorderLayout.CENTER);
		this.add(paneButton, BorderLayout.SOUTH);
	}

	@Override
	protected void refreshWert() {
		this.planung.kategorienAbgleichen();
		this.wert = this.planung;
	}

	@Override
	protected JComponent getZentraleKomponente() {
		return this.table;
	}

	@Override
	public void laden(final DataInputStream in) throws IOException {
		this.planung.laden(in);
		this.unterkategorien.setSelected(this.planung.isUnterkategorien());
		this.hochrechnen.setSelected(this.planung.isHochrechnen());
	}

	@Override
	public void speichern(final DataOutputStream out) throws IOException {
		this.planung.speichern(out);
	}

}
