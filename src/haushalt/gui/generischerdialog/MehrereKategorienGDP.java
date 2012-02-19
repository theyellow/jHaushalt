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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JComponent;
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
  private static final TextResource res = TextResource.get();

  private final JPanel negPane = new JPanel();
  private final JPanel posPane = new JPanel();
  private final JPanel buttonPane = new JPanel();
  private final JPanel centralPane = new JPanel();
  protected final JList negList;
  protected DefaultListModel negListModel;
  private final JLabel negLabel = new JLabel(res.getString("unused_categories"));
  protected final JList posList;
  protected DefaultListModel posListModel;
  private final JLabel posLabel = new JLabel(res.getString("used_categories"));
  private final JButton alle_hinzufuegen = new JButton(">>");
  private final JButton hinzufuegen = new JButton(">");
  private final JButton entfernen = new JButton("<");
  private final JButton alle_entfernen = new JButton("<<");
  private final JCheckBox checkBox = new JCheckBox(res.getString("use_subcategories"), true);
  private final Datenbasis db;
  
  public MehrereKategorienGDP(String text, Datenbasis datenbasis) {
    super(text);
    this.db = datenbasis;
    EinzelKategorie[] posKategorien = db.getKategorien(unterkategorienVerwenden());
    negListModel = new DefaultListModel();
    negList = new JList(negListModel);
    negList.setPrototypeCellValue("XXXXXXXXXXXXXXXXXXXXXXXXX");
    final JScrollPane negScrollPane = new JScrollPane(negList);
    negScrollPane.setAlignmentX(LEFT_ALIGNMENT);
    negScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    negLabel.setLabelFor(negList);
    negLabel.setAlignmentX(LEFT_ALIGNMENT);
    negPane.setLayout(new BoxLayout(negPane, BoxLayout.PAGE_AXIS));
    negPane.add(negLabel);
    negPane.add(negScrollPane);
    
    posListModel = new DefaultListModel();
    posListModel.setSize(posKategorien.length);
    for(int i=0; i< posKategorien.length; i++)
      posListModel.set(i, posKategorien[i]);
    posList = new JList(posListModel);
    posList.setPrototypeCellValue("XXXXXXXXXXXXXXXXXXXXXXXXX");
    final JScrollPane posScrollPane = new JScrollPane(posList);
    posScrollPane.setAlignmentX(LEFT_ALIGNMENT);
    posScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    posLabel.setLabelFor(posList);
    posLabel.setAlignmentX(LEFT_ALIGNMENT);
    posPane.setLayout(new BoxLayout(posPane, BoxLayout.PAGE_AXIS));
    posPane.add(posLabel);
    posPane.add(posScrollPane);
    
    alle_entfernen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        posListModel.removeAllElements();
        negListModel.removeAllElements();
        EinzelKategorie[] kategorien = db.getKategorien(unterkategorienVerwenden());
        for(int i=0; i<kategorien.length; i++)
          negListModel.addElement(kategorien[i]);
      }
    });
    alle_entfernen.setAlignmentX(Component.CENTER_ALIGNMENT);
    buttonPane.add(alle_entfernen);
    entfernen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(!posList.isSelectionEmpty()) {
          Object[] kategorie = posList.getSelectedValues();
          for(int i=0; i<kategorie.length; i++) {
            posListModel.removeElement(kategorie[i]);
            negListModel.addElement(kategorie[i]);
          }
        }
      }
    });
    entfernen.setAlignmentX(Component.CENTER_ALIGNMENT);
    buttonPane.add(entfernen);
    hinzufuegen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(!negList.isSelectionEmpty()) {
          Object[] kategorie = negList.getSelectedValues();
          for(int i=0; i<kategorie.length; i++) {
            negListModel.removeElement(kategorie[i]);
            posListModel.addElement(kategorie[i]);
          }
        }
      }
    });
    hinzufuegen.setAlignmentX(Component.CENTER_ALIGNMENT);
    buttonPane.add(hinzufuegen);
    alle_hinzufuegen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        posListModel.removeAllElements();
        negListModel.removeAllElements();
        EinzelKategorie[] kategorien = db.getKategorien(unterkategorienVerwenden());
        for(int i=0; i<kategorien.length; i++)
          posListModel.addElement(kategorien[i]);
      }
    });
    alle_hinzufuegen.setAlignmentX(Component.CENTER_ALIGNMENT);
    buttonPane.add(alle_hinzufuegen);
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.Y_AXIS));
    centralPane.add(negPane);
    centralPane.add(buttonPane);
    centralPane.add(posPane);
    
    add(centralPane);
    checkBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e)  {
        refreshRegisterUndKategorien();
        refreshWert();
      }
    });
    add(checkBox);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    refreshWert();
  }

  private boolean unterkategorienVerwenden() {
    return checkBox.isSelected();
  }
  
  protected void refreshWert() {
    EinzelKategorie[] kategorien = new EinzelKategorie[posListModel.getSize()];
    posListModel.copyInto(kategorien);
    wert = kategorien;
  }

  public void refreshRegisterUndKategorien() {
    EinzelKategorie[] neueKategorien = db.getKategorien(unterkategorienVerwenden());
    EinzelKategorie[] posKategorien = new EinzelKategorie[posListModel.getSize()];
    posListModel.copyInto(posKategorien);
    negListModel = new DefaultListModel();
    posListModel = new DefaultListModel();
    negList.setModel(negListModel);
    posList.setModel(posListModel);
    for(int i=0; i<neueKategorien.length; i++) {
      boolean gefunden = false;
      for(int j=0; j<posKategorien.length; j++)
        if(neueKategorien[i] == posKategorien[j])
          gefunden = true;
      if(gefunden)
        posListModel.addElement(neueKategorien[i]);
      else
        negListModel.addElement(neueKategorien[i]);
    }
  }

  public JComponent getZentraleKomponente() {
    return posList;
  }
  
  public boolean getUnterkategorienVerwenden() {
    return checkBox.isSelected();
  }
  
  public void laden(DataInputStream in) throws IOException {
    checkBox.setSelected(in.readBoolean());
    EinzelKategorie[] kategorien = db.getKategorien(unterkategorienVerwenden());
    negListModel = new DefaultListModel();
    posListModel = new DefaultListModel();
    negList.setModel(negListModel);
    posList.setModel(posListModel);
    int anzahl = in.readInt();
    String[] katname = new String[anzahl];
    for(int i=0; i<anzahl; i++)
      katname[i] = in.readUTF();
    for(int j=0; j<kategorien.length; j++) {
      boolean gefunden = false;
      for(int i=0; i<anzahl; i++)
        if(katname[i].equals(""+kategorien[j]))
          gefunden = true;
      if(gefunden)
        posListModel.addElement(kategorien[j]);
      else
        negListModel.addElement(kategorien[j]);
    }
  }

  public void speichern(DataOutputStream out) throws IOException {
    out.writeBoolean(unterkategorienVerwenden());
    out.writeInt(posListModel.size());
    for(int i=0; i<posListModel.size(); i++)
      out.writeUTF(""+posListModel.get(i));
  }

}
