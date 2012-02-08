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

package haushalt.auswertung.planung;

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

import haushalt.daten.Euro;
import haushalt.gui.DeleteableTextField;
import haushalt.gui.EuroField;
import haushalt.gui.EuroRenderer;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.07.05
 * @since 2.1
 */

/*
 * 2007.07.05 Internationalisierung
 * 2006.04.21 BugFix: Auswahl Unterkategorien/Hochrechnen nach
 *            dem Laden richtig setzen 
 */
public class PlanungGDP extends AbstractGDPane {
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  final JTable table;
  final Planung planung;
  final JCheckBox unterkategorien = new JCheckBox(res.getString("use_subcategories"));
  final JCheckBox hochrechnen = new JCheckBox(res.getString("extrapolation_actual_values"));
  
  public PlanungGDP(String text, final Planung planung) {
    super(text);
    
    this.planung = planung;

    unterkategorien.setSelected(planung.isUnterkategorien());
    hochrechnen.setSelected(planung.isHochrechnen());
    final PlanungTableModel tableModel = new PlanungTableModel(planung);
    table = new JTable(tableModel);
    final JPanel paneButton = new JPanel();
    final JButton buttonAlle = new JButton(res.getString("all_categories"));
    final JButton buttonKeine = new JButton(res.getString("no_categories"));

    unterkategorien.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e)  {
        planung.setUnterkategorien(unterkategorien.isSelected());
        tableModel.fireTableDataChanged();
      }
    });
    hochrechnen.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e)  {
        planung.setHochrechnen(hochrechnen.isSelected());
      }
    });
    table.setSurrendersFocusOnKeystroke(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    // Cell-Editoren erzeugen
    DeleteableTextField textField = new DeleteableTextField();
    DefaultCellEditor cellEditor = new DefaultCellEditor(textField);
    table.setDefaultEditor(String.class, cellEditor);
    table.setDefaultEditor(Euro.class, new DefaultCellEditor(new EuroField()));
    // Cell-Renderer erzeugen
    table.setDefaultRenderer(String.class, new DefaultTableCellRenderer());
    table.setDefaultRenderer(Euro.class, new EuroRenderer());
    
    paneButton.add(buttonAlle, null);
    paneButton.add(buttonKeine, null);
    buttonAlle.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        planung.alleVerwenden(true);
        tableModel.fireTableDataChanged();
      }
    });
    buttonKeine.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        planung.alleVerwenden(false);
        tableModel.fireTableDataChanged();
      }
    });

    setLayout(new BorderLayout());
    JPanel northPane = new JPanel();
    northPane.add(unterkategorien);
    northPane.add(hochrechnen);
    this.add(northPane, BorderLayout.NORTH);
    this.add(new JScrollPane(table), BorderLayout.CENTER);
    this.add(paneButton, BorderLayout.SOUTH);
  }

  protected void refreshWert() {
    planung.kategorienAbgleichen();
    wert = planung;
  }

  protected JComponent getZentraleKomponente() {
    return table;
  }

  public void laden(DataInputStream in) throws IOException {
    planung.laden(in);
    unterkategorien.setSelected(planung.isUnterkategorien());
    hochrechnen.setSelected(planung.isHochrechnen());
  }

  public void speichern(DataOutputStream out) throws IOException {
    planung.speichern(out);
  }

}
