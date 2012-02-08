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

import haushalt.auswertung.CsvHandler;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Mit der Hilfe des Dialogs werden die Spalten der Import-
 * datei den vier Spalten von jHaushalt zugeordnet.
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.03.08
 * @since 2.1
 */

/*
 * 2007.03.08 Internationalisierung
 * 2006.01.28 Erste Version
 */
public class DlgImport extends JDialog {
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  // GUI-Komponenten
  private final JTabbedPane tabbedPane = new JTabbedPane();
  private final JPanel paneZwei = new JPanel();
  private final JPanel paneMit = new JPanel();
  private final JPanel paneOhne = new JPanel();
  private final JPanel buttonPane = new JPanel();
  private final JButton buttonOK = new JButton(res.getString("button_ok"));
  private final JButton buttonAbbruch = new JButton(res.getString("button_cancel"));
  private final JButton buttonWeiter = new JButton(res.getString("button_continue"));
  private final JLabel labelMit[] = {
      new JLabel(res.getString("date")),
      new JLabel(res.getString("posting_text")),
      new JLabel(res.getString("category")),
      new JLabel(res.getString("amount"))
  };
  private final JLabel labelOhne[] = {
      new JLabel(res.getString("date")),
      new JLabel(res.getString("posting_text")),
      new JLabel(res.getString("category")),
      new JLabel(res.getString("amount"))
  };
  private final JCheckBox checkBox = new JCheckBox(res.getString("first_row_contains_column_names"));
  private final JComboBox[] comboBoxMit = new JComboBox[4];
  private final JComboBox[] comboBoxOhne = new JComboBox[4];

  private final int[] gewaehlt = new int[4];
  private String[][] tabelle = null;
  private boolean ok = false;
  private final CsvHandler csvHandler = new CsvHandler();
  private final CsvHandler.CsvPane csvPane;
  
  public DlgImport(final Haushalt haushalt) throws HeadlessException {
    super(haushalt.getFrame(), res.getString("import_csv"), true);
    final Container contentPane = getContentPane();
    contentPane.add(tabbedPane, BorderLayout.CENTER);
    contentPane.add(buttonPane, BorderLayout.SOUTH);
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ok = true;
        setVisible(false);
      }
    });
    buttonOK.setEnabled(false);
    buttonWeiter.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(csvPane.laden()) {
          tabelle = csvHandler.getTabelle();
          final int anzahlSpalten = tabelle[0].length;
          final String[] listeMit = new String[anzahlSpalten+1];
          final String[] listeOhne = new String[anzahlSpalten+1];
          listeMit[0] = listeOhne[0] = "leer";
          for (int i = 1; i < anzahlSpalten+1; i++) {
            listeMit[i] = tabelle[0][i-1];
            listeOhne[i] = res.getString("csv_import_column")+ " " + i;
          }
          ActionListener actionListener = new ActionListener(){
            public void actionPerformed(ActionEvent event) {
              for(int i = 0; i < 4; i++) {
                if(event.getSource() == comboBoxMit[i])
                  gewaehlt[i] = comboBoxMit[i].getSelectedIndex();
                if(event.getSource() == comboBoxOhne[i])
                  gewaehlt[i] = comboBoxOhne[i].getSelectedIndex();
              }
            }
          };
          for(int i = 0; i < 4; i++) {
            comboBoxMit[i] = new JComboBox(listeMit);
            comboBoxMit[i].addActionListener(actionListener);
            comboBoxOhne[i] = new JComboBox(listeOhne);
            comboBoxOhne[i].addActionListener(actionListener);
            if(comboBoxOhne[i].getItemCount() > i+1)
              comboBoxOhne[i].setSelectedIndex(i+1);
            paneMit.add(labelMit[i]);
            paneMit.add(comboBoxMit[i]);
            paneOhne.add(labelOhne[i]);
            paneOhne.add(comboBoxOhne[i]);
          }
          tabbedPane.setEnabledAt(1, true);
          tabbedPane.setSelectedIndex(1);
          buttonWeiter.setEnabled(false);
          buttonOK.setEnabled(true);
        }
        else
          JOptionPane.showMessageDialog(
              haushalt.getFrame(),
              res.getString("could_not_load"),
              res.getString("error"),
              JOptionPane.INFORMATION_MESSAGE);
      }
    });
    buttonAbbruch.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ok = false;
        setVisible(false);
      }
    });
    buttonPane.add(buttonAbbruch);
    buttonPane.add(buttonWeiter);
    buttonPane.add(buttonOK);
    
    csvPane = csvHandler.new CsvPane(haushalt.getFrame(), haushalt.getOrdner(), true);
    tabbedPane.addTab(res.getString("choose_file"), csvPane);  
    paneMit.setLayout(new GridLayout(4,2));
    paneOhne.setLayout(new GridLayout(4,2));
    paneZwei.setLayout(new BorderLayout());
    paneZwei.add(checkBox, BorderLayout.NORTH);
    paneZwei.add(paneOhne, BorderLayout.CENTER);
    tabbedPane.addTab(res.getString("assign_columns"), paneZwei);
    tabbedPane.setEnabledAt(1, false);
    checkBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e)  {
        JCheckBox checkBox = (JCheckBox) e.getSource();
        if(checkBox.isSelected()) {
          paneZwei.remove(paneOhne);
          paneZwei.add(paneMit, BorderLayout.CENTER);
          for(int i = 0; i < 4; i++) {
            comboBoxMit[i].setSelectedIndex(gewaehlt[i]);
            comboBoxMit[i].setEnabled(true);
          }
        }
        else {
          paneZwei.remove(paneMit);
          paneZwei.add(paneOhne, BorderLayout.CENTER);
          for(int i = 0; i < 4; i++) {
            comboBoxOhne[i].setSelectedIndex(gewaehlt[i]);
            comboBoxOhne[i].setEnabled(true);
          }
        }
        contentPane.validate();
      }
    });
  }

  public String[][] getImportTabelle() {
    if(!ok)
      return null;
    final int start = checkBox.isSelected()?1:0;
    String[][] importTabelle = new String[tabelle.length-start][4];
    for (int i = start; i < tabelle.length; i++) {
      for (int j = 0; j < 4; j++){
        if(gewaehlt[j] != 0)
          importTabelle[i-start][j] = tabelle[i][gewaehlt[j]-1];
        else
          importTabelle[i-start][j] = "";
      }
    }
    return importTabelle;
  }

}
