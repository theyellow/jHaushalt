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

import haushalt.daten.Datenbasis;
import haushalt.daten.Datum;
import haushalt.daten.Euro;
import haushalt.daten.EinzelKategorie;
import haushalt.gui.DatumField;
import haushalt.gui.DeleteableTextField;
import haushalt.gui.EuroField;
import haushalt.gui.EuroRenderer;
import haushalt.gui.Haushalt;
import haushalt.gui.KategorieCellEditor;
import haushalt.gui.KategorieRenderer;
import haushalt.gui.TextResource;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

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
 * @author Dr. Lars H. Hahn
 * @version 2.5 / 2007.05.24
 */

/* 
 * 2007.05.24 Ausführen von automatischen Buchungen bis zu einem Datum 
 * 2007.02.28 Internationalisierung
 * 2007.01.30 BugFix: Nachdem Löschen wird die Selektierung aufgehoben;
 *            Gleichzeitige Selektierung in beiden Tabellen verhindert
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
    standardTableModel = new AutoStandardBuchungTableModel(db);
    standardTable = new JTable(standardTableModel);
    standardTable.setSurrendersFocusOnKeystroke(true);
    standardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    standardTable.setSelectionBackground(haushalt.getFarbeSelektion());
    standardTable.setGridColor(haushalt.getFarbeGitter());
    standardTable.setPreferredScrollableViewportSize(new Dimension(500,200));
    standardTable.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        umbuchungTable.clearSelection();  // Es kann nur eine (selektierte) Tabelle geben :-)
      }
    });

    // Table für Umbuchungen initialisieren
    umbuchungTableModel = new AutoUmbuchungTableModel(db);
    umbuchungTable = new JTable(umbuchungTableModel);
    umbuchungTable.setSurrendersFocusOnKeystroke(true);
    umbuchungTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    umbuchungTable.setSelectionBackground(haushalt.getFarbeSelektion());
    umbuchungTable.setGridColor(haushalt.getFarbeGitter());
    umbuchungTable.setPreferredScrollableViewportSize(new Dimension(500,200));
    umbuchungTable.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        standardTable.clearSelection();  // Es kann nur eine (selektierte) Tabelle geben :-)
      }
    });

    // Action erzeugen
    Action action = new AbstractAction(res.getString("button_delete"), haushalt.bildLaden("Delete16.gif")) {
      private static final long serialVersionUID = 1L;
  		public void actionPerformed(ActionEvent e) {
  		  if(e.getActionCommand().equals(res.getString("button_delete"))) {
          if(standardTable.getCellEditor() != null)
            standardTable.getCellEditor().cancelCellEditing();
          if(umbuchungTable.getCellEditor() != null)
            umbuchungTable.getCellEditor().cancelCellEditing();
          int rowStd = standardTable.getSelectedRow();
          int rowUmb = umbuchungTable.getSelectedRow();
	        if((rowStd == -1) && (rowUmb == -1)){
	          JOptionPane.showMessageDialog(haushalt.getFrame(),
	            res.getString("no_row_selected"),
	            "Alt-D: " + res.getString("automatic_booking"),
	            JOptionPane.WARNING_MESSAGE);
	        }
	        else if(rowStd != -1) { 
            if(rowStd == standardTableModel.getRowCount()-1) {
              JOptionPane.showMessageDialog(haushalt.getFrame(),
                  res.getString("can_not_delete_input_row"),
                  "Alt-D: " + res.getString("automatic_booking"),
                  JOptionPane.WARNING_MESSAGE);
  	        }
  	        else {
  	          standardTableModel.entferneZeile(rowStd);
  	        }
          }
          else {
            if(rowUmb == umbuchungTableModel.getRowCount()-1) {
              JOptionPane.showMessageDialog(haushalt.getFrame(),
                  res.getString("can_not_delete_input_row"),
                  "Alt-D: " + res.getString("automatic_booking"),
                  JOptionPane.WARNING_MESSAGE);
            }
            else {
              umbuchungTableModel.entferneZeile(rowUmb);
            }
          }
  		  }
  		}      
    };
		action.putValue(Action.SHORT_DESCRIPTION, res.getString("legend_delete_automatic_booking"));
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK);
		action.putValue(Action.ACCELERATOR_KEY, key);
		action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
    
    // Cell-Editoren für Standard-Buchungen erzeugen
    TableColumnModel columnModel = standardTable.getColumnModel();
    DatumField datumField = new DatumField();
    columnModel.getColumn(0).setCellEditor(new DefaultCellEditor(datumField));
    DeleteableTextField textField = new DeleteableTextField();
    columnModel.getColumn(1).setCellEditor(new DefaultCellEditor(textField));
    columnModel.getColumn(2).setCellEditor(new KategorieCellEditor(haushalt, db));
    EuroField euroField = new EuroField();
    columnModel.getColumn(3).setCellEditor(new DefaultCellEditor(euroField));
    JComboBox comboBox1 = new JComboBox(db.getRegisterNamen());
    columnModel.getColumn(4).setCellEditor(new DefaultCellEditor(comboBox1));
    JComboBox comboBox2 = new JComboBox(res.getAutoBuchungIntervallNamen());
    columnModel.getColumn(5).setCellEditor(new DefaultCellEditor(comboBox2));
    
    // Cell-Editoren für Umbuchungen erzeugen
    columnModel = umbuchungTable.getColumnModel();
    columnModel.getColumn(0).setCellEditor(new DefaultCellEditor(datumField));
    columnModel.getColumn(1).setCellEditor(new DefaultCellEditor(textField));
    columnModel.getColumn(2).setCellEditor(new DefaultCellEditor(euroField));
    columnModel.getColumn(3).setCellEditor(new DefaultCellEditor(comboBox1));
    columnModel.getColumn(4).setCellEditor(new DefaultCellEditor(comboBox1));
    columnModel.getColumn(5).setCellEditor(new DefaultCellEditor(comboBox2));
    
    // Cell-Renderer für Standard-Buchungen erzeugen
    standardTable.setDefaultRenderer(EinzelKategorie.class, new KategorieRenderer());
    standardTable.setDefaultRenderer(Euro.class, new EuroRenderer());

    // Cell-Renderer für Umbuchungen erzeugen
    umbuchungTable.setDefaultRenderer(EinzelKategorie.class, new KategorieRenderer());
    umbuchungTable.setDefaultRenderer(Euro.class, new EuroRenderer());

    Container contentPane = getContentPane();
    JScrollPane northPane = new JScrollPane(standardTable);
    northPane.setBorder(BorderFactory.createTitledBorder(res.getString("standard_bookings")));
    contentPane.add(northPane, BorderLayout.NORTH);
    JScrollPane centerPane = new JScrollPane(umbuchungTable);
    centerPane.setBorder(BorderFactory.createTitledBorder(res.getString("rebookings")));
    contentPane.add(centerPane, BorderLayout.CENTER);
    contentPane.add(buttonPane, BorderLayout.SOUTH);
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
    		int anzahl = db.ausfuehrenAutoBuchungen(new Datum(datum.getText()));
    		if(anzahl > 0)
          JOptionPane.showMessageDialog(haushalt.getFrame(),
              res.getString("executed_automatic_bookings1")+" "+anzahl+" "+res.getString("executed_automatic_bookings2"));
      }
    });
    buttonPane.add(new JLabel(res.getString("execute_until")));
    buttonPane.add(datum);
		buttonDelete = new JButton(action);
    buttonPane.add(buttonDelete);
    buttonPane.add(buttonOK);
  }

  public void zeigeDialog() {
    pack();
    setVisible(true);
  }

}