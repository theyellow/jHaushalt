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
import haushalt.daten.SplitBuchung;
import haushalt.gui.EuroField;
import haushalt.gui.EuroRenderer;
import haushalt.gui.Haushalt;
import haushalt.gui.KategorieRenderer;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.6/2009.09.07
 * @since 2.0
 */

/*
 * 2009.09.07 Bug-Fix: Löschen der Eingabe-Zeile verhindert
 * 2007.05.31 Internationalisierung
 * 2004.08.22 Erste Version
 */
public class DlgSplitBuchung extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	// GUI-Komponenten
	private final JPanel buttonPane = new JPanel();
	private final JButton buttonOK = new JButton(res.getString("button_ok"));
	private final JButton buttonDelete;
	protected final SplitBetragTableModel tableModel;
	protected final JTable table;

	public DlgSplitBuchung(final Haushalt haushalt, final Datenbasis db, final SplitBuchung buchung)
			throws HeadlessException {
		super(haushalt.getFrame(), res.getString("split_editor"), true);
		this.tableModel = new SplitBetragTableModel(buchung);
		this.table = new JTable(this.tableModel);
		this.table.setSurrendersFocusOnKeystroke(true);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setSelectionBackground(haushalt.getFarbeSelektion());
		this.table.setGridColor(haushalt.getFarbeGitter());
		this.table.setPreferredScrollableViewportSize(new Dimension(500, 200));

		// Action erzeugen
		final Action action = new AbstractAction(res.getString("button_delete"), haushalt.bildLaden("Delete16.png")) {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent e) {
				if (e.getActionCommand().equals(res.getString("button_delete"))) {
					final TableCellEditor cellEditor = DlgSplitBuchung.this.table.getCellEditor();
					if (cellEditor != null) {
						cellEditor.cancelCellEditing();
					}
					final int row = DlgSplitBuchung.this.table.getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(haushalt.getFrame(),
								res.getString("no_row_selected"),
								"Alt-D: Split-Buchung",
								JOptionPane.WARNING_MESSAGE);
					}
					else if (row == DlgSplitBuchung.this.tableModel.getRowCount() - 1) {
						JOptionPane.showMessageDialog(haushalt.getFrame(),
								res.getString("can_not_delete_input_row"),
								"Alt-D: " + res.getString("split_editor"),
								JOptionPane.WARNING_MESSAGE);
					}
					else {
						DlgSplitBuchung.this.tableModel.entferneZeile(row);
					}
				}
			}
		};
		action.putValue(Action.SHORT_DESCRIPTION, res.getString("delete_split_booking"));
		final KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK);
		action.putValue(Action.ACCELERATOR_KEY, key);
		action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));

		// Cell-Editoren erzeugen
		this.table
				.setDefaultEditor(EinzelKategorie.class, new DefaultCellEditor(new JComboBox(db.getKategorien(true))));
		this.table.setDefaultEditor(Euro.class, new DefaultCellEditor(new EuroField()));

		// Cell-Renderer erzeugen
		this.table.setDefaultRenderer(EinzelKategorie.class, new KategorieRenderer());
		this.table.setDefaultRenderer(Euro.class, new EuroRenderer());
		this.table.setPreferredScrollableViewportSize(new Dimension(500, 140));

		final Container contentPane = getContentPane();
		contentPane.add(new JScrollPane(this.table), BorderLayout.CENTER);
		contentPane.add(this.buttonPane, BorderLayout.SOUTH);
		this.buttonOK.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});
		this.buttonDelete = new JButton(action);
		this.buttonPane.add(this.buttonDelete);
		this.buttonPane.add(this.buttonOK);
	}

	public void zeigeDialog() {
		pack();
		setVisible(true);
	}

}
