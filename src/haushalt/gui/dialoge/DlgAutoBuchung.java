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
import haushalt.daten.EinzelKategorie;
import haushalt.daten.Euro;
import haushalt.gui.DatumField;
import haushalt.gui.DeleteableTextField;
import haushalt.gui.EuroField;
import haushalt.gui.EuroRenderer;
import haushalt.gui.Haushalt;
import haushalt.gui.KategorieCellEditor;
import haushalt.gui.KategorieRenderer;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;

/**
 * Dialog zur Bearbeitung der automatischen Buchungen.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5 / 2007.05.24
 */

/*
 * 2007.05.24 Ausführen von automatischen Buchungen bis zu einem Datum
 * 2007.02.28 Internationalisierung
 * 2007.01.30 BugFix: Nachdem Löschen wird die Selektierung aufgehoben;
 * Gleichzeitige Selektierung in beiden Tabellen verhindert
 * 2006.06.16 Erweiterung um Umbuchungen
 * 2004.08.22 Erste Version
 */
public class DlgAutoBuchung extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	// GUI-Komponenten
	private final DatumField datum = new DatumField();
	private final JPanel buttonPane = new JPanel();
	private final JButton buttonOK = new JButton(res.getString("button_ok"));
	private final JButton buttonDelete;
	protected final AutoStandardBuchungTableModel standardTableModel;
	protected final JTable standardTable;
	protected final AutoUmbuchungTableModel umbuchungTableModel;
	protected final JTable umbuchungTable;

	public DlgAutoBuchung(final Haushalt haushalt, final Datenbasis db) {
		super(haushalt.getFrame(), res.getString("automatic_booking"), true);

		// Table für Standard-Buchungen initialisieren
		this.standardTableModel = new AutoStandardBuchungTableModel(db);
		this.standardTable = new JTable(this.standardTableModel);
		this.standardTable.setSurrendersFocusOnKeystroke(true);
		this.standardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.standardTable.setSelectionBackground(haushalt.getFarbeSelektion());
		this.standardTable.setGridColor(haushalt.getFarbeGitter());
		this.standardTable.setPreferredScrollableViewportSize(new Dimension(500, 200));
		this.standardTable.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(final FocusEvent e) {
				DlgAutoBuchung.this.umbuchungTable.clearSelection(); // Es kann
																		// nur
																		// eine
				// (selektierte) Tabelle
				// geben :-)
			}
		});

		// Table für Umbuchungen initialisieren
		this.umbuchungTableModel = new AutoUmbuchungTableModel(db);
		this.umbuchungTable = new JTable(this.umbuchungTableModel);
		this.umbuchungTable.setSurrendersFocusOnKeystroke(true);
		this.umbuchungTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.umbuchungTable.setSelectionBackground(haushalt.getFarbeSelektion());
		this.umbuchungTable.setGridColor(haushalt.getFarbeGitter());
		this.umbuchungTable.setPreferredScrollableViewportSize(new Dimension(500, 200));
		this.umbuchungTable.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(final FocusEvent e) {
				DlgAutoBuchung.this.standardTable.clearSelection(); // Es kann
																	// nur eine
				// (selektierte) Tabelle geben
				// :-)
			}
		});

		// Action erzeugen
		final Action action = new AbstractAction(res.getString("button_delete"), haushalt.bildLaden("Delete16.png")) {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent e) {
				if (e.getActionCommand().equals(res.getString("button_delete"))) {
					if (DlgAutoBuchung.this.standardTable.getCellEditor() != null) {
						DlgAutoBuchung.this.standardTable.getCellEditor().cancelCellEditing();
					}
					if (DlgAutoBuchung.this.umbuchungTable.getCellEditor() != null) {
						DlgAutoBuchung.this.umbuchungTable.getCellEditor().cancelCellEditing();
					}
					final int rowStd = DlgAutoBuchung.this.standardTable.getSelectedRow();
					final int rowUmb = DlgAutoBuchung.this.umbuchungTable.getSelectedRow();
					if ((rowStd == -1) && (rowUmb == -1)) {
						JOptionPane.showMessageDialog(haushalt.getFrame(),
								res.getString("no_row_selected"),
								"Alt-D: " + res.getString("automatic_booking"),
								JOptionPane.WARNING_MESSAGE);
					}
					else if (rowStd != -1) {
						if (rowStd == DlgAutoBuchung.this.standardTableModel.getRowCount() - 1) {
							JOptionPane.showMessageDialog(haushalt.getFrame(),
									res.getString("can_not_delete_input_row"),
									"Alt-D: " + res.getString("automatic_booking"),
									JOptionPane.WARNING_MESSAGE);
						}
						else {
							DlgAutoBuchung.this.standardTableModel.entferneZeile(rowStd);
						}
					}
					else {
						if (rowUmb == DlgAutoBuchung.this.umbuchungTableModel.getRowCount() - 1) {
							JOptionPane.showMessageDialog(haushalt.getFrame(),
									res.getString("can_not_delete_input_row"),
									"Alt-D: " + res.getString("automatic_booking"),
									JOptionPane.WARNING_MESSAGE);
						}
						else {
							DlgAutoBuchung.this.umbuchungTableModel.entferneZeile(rowUmb);
						}
					}
				}
			}
		};
		action.putValue(Action.SHORT_DESCRIPTION, res.getString("legend_delete_automatic_booking"));
		final KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK);
		action.putValue(Action.ACCELERATOR_KEY, key);
		action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));

		// Cell-Editoren für Standard-Buchungen erzeugen
		TableColumnModel columnModel = this.standardTable.getColumnModel();
		final DatumField datumField = new DatumField();
		columnModel.getColumn(0).setCellEditor(new DefaultCellEditor(datumField));
		final DeleteableTextField textField = new DeleteableTextField();
		columnModel.getColumn(1).setCellEditor(new DefaultCellEditor(textField));
		columnModel.getColumn(2).setCellEditor(new KategorieCellEditor(haushalt, db));
		final EuroField euroField = new EuroField();
		columnModel.getColumn(3).setCellEditor(new DefaultCellEditor(euroField));
		final JComboBox comboBox1 = new JComboBox(db.getRegisterNamen());
		columnModel.getColumn(4).setCellEditor(new DefaultCellEditor(comboBox1));
		final JComboBox comboBox2 = new JComboBox(res.getAutoBuchungIntervallNamen());
		columnModel.getColumn(5).setCellEditor(new DefaultCellEditor(comboBox2));

		// Cell-Editoren für Umbuchungen erzeugen
		columnModel = this.umbuchungTable.getColumnModel();
		columnModel.getColumn(0).setCellEditor(new DefaultCellEditor(datumField));
		columnModel.getColumn(1).setCellEditor(new DefaultCellEditor(textField));
		columnModel.getColumn(2).setCellEditor(new DefaultCellEditor(euroField));
		columnModel.getColumn(3).setCellEditor(new DefaultCellEditor(comboBox1));
		columnModel.getColumn(4).setCellEditor(new DefaultCellEditor(comboBox1));
		columnModel.getColumn(5).setCellEditor(new DefaultCellEditor(comboBox2));

		// Cell-Renderer für Standard-Buchungen erzeugen
		this.standardTable.setDefaultRenderer(EinzelKategorie.class, new KategorieRenderer());
		this.standardTable.setDefaultRenderer(Euro.class, new EuroRenderer());

		// Cell-Renderer für Umbuchungen erzeugen
		this.umbuchungTable.setDefaultRenderer(EinzelKategorie.class, new KategorieRenderer());
		this.umbuchungTable.setDefaultRenderer(Euro.class, new EuroRenderer());

		final Container contentPane = getContentPane();
		final JScrollPane northPane = new JScrollPane(this.standardTable);
		northPane.setBorder(BorderFactory.createTitledBorder(res.getString("standard_bookings")));
		contentPane.add(northPane, BorderLayout.NORTH);
		final JScrollPane centerPane = new JScrollPane(this.umbuchungTable);
		centerPane.setBorder(BorderFactory.createTitledBorder(res.getString("rebookings")));
		contentPane.add(centerPane, BorderLayout.CENTER);
		contentPane.add(this.buttonPane, BorderLayout.SOUTH);
		this.buttonOK.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
				final int anzahl = db.ausfuehrenAutoBuchungen(new Datum(DlgAutoBuchung.this.datum.getText()));
				if (anzahl > 0) {
					JOptionPane.showMessageDialog(
							haushalt.getFrame(),
							res.getString("executed_automatic_bookings1") + " " + anzahl + " "
									+ res.getString("executed_automatic_bookings2"));
				}
			}
		});
		this.buttonPane.add(new JLabel(res.getString("execute_until")));
		this.buttonPane.add(this.datum);
		this.buttonDelete = new JButton(action);
		this.buttonPane.add(this.buttonDelete);
		this.buttonPane.add(this.buttonOK);
	}

	public void zeigeDialog() {
		pack();
		setVisible(true);
	}

}