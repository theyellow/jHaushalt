/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

jHaushalt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.


(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

package haushalt.gui.dialoge;

import haushalt.daten.*;
import haushalt.gui.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
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

  public DlgSplitBuchung(final Haushalt haushalt, Datenbasis db, SplitBuchung buchung) throws HeadlessException {
	super(haushalt.getFrame(), res.getString("split_editor"), true);
	tableModel = new SplitBetragTableModel(buchung);
	table = new JTable(tableModel);
	table.setSurrendersFocusOnKeystroke(true);
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	table.setSelectionBackground(haushalt.getFarbeSelektion());
	table.setGridColor(haushalt.getFarbeGitter());
    table.setPreferredScrollableViewportSize(new Dimension(500,200));

    // Action erzeugen
    Action action = new AbstractAction(res.getString("button_delete"), haushalt.bildLaden("Delete16.png")) {
      private static final long serialVersionUID = 1L;
  		public void actionPerformed(ActionEvent e) {
  		  if(e.getActionCommand().equals(res.getString("button_delete"))) {
	        TableCellEditor cellEditor = table.getCellEditor();
	        if(cellEditor != null)
	          cellEditor.cancelCellEditing();
	        int row = table.getSelectedRow();
	        if(row == -1) {
	          JOptionPane.showMessageDialog(haushalt.getFrame(),
	            res.getString("no_row_selected"),
	            "Alt-D: Split-Buchung",
	            JOptionPane.WARNING_MESSAGE);
	        }
	        else if(row == tableModel.getRowCount()-1) {
	          JOptionPane.showMessageDialog(haushalt.getFrame(),
	            res.getString("can_not_delete_input_row"),
	            "Alt-D: "+res.getString("split_editor"),
	            JOptionPane.WARNING_MESSAGE);
	        }
	        else {
	          tableModel.entferneZeile(row);
	        }
  		  }
  		}      
    };
	action.putValue(Action.SHORT_DESCRIPTION, res.getString("delete_split_booking"));
	KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK);
	action.putValue(Action.ACCELERATOR_KEY, key);
	action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));

	// Cell-Editoren erzeugen
	table.setDefaultEditor(EinzelKategorie.class, new DefaultCellEditor(new JComboBox(db.getKategorien(true))));
	table.setDefaultEditor(Euro.class, new DefaultCellEditor(new EuroField()));
	
	// Cell-Renderer erzeugen
	table.setDefaultRenderer(EinzelKategorie.class, new KategorieRenderer());
	table.setDefaultRenderer(Euro.class, new EuroRenderer());
	table.setPreferredScrollableViewportSize(new Dimension(500, 140));

    Container contentPane = getContentPane();
    contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
    contentPane.add(buttonPane, BorderLayout.SOUTH);
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
	buttonDelete = new JButton(action);
    buttonPane.add(buttonDelete);
    buttonPane.add(buttonOK);
  }

  public void zeigeDialog() {
    pack();
    setVisible(true);
  }

}
