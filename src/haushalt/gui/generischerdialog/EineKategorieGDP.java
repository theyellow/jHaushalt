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

import haushalt.daten.*;
import haushalt.gui.TextResource;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2008.01.17
 */

/*
 * 2008.01.17 Internationalisierung
 * 2006.01.25: Verwendung der Unterkategorien einstellbar
 */
public class EineKategorieGDP extends AbstractGDPane {
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  private final JComboBox comboBox;
  private final JCheckBox checkBox = new JCheckBox(res.getString("use_subcategories"), true);
  private final Datenbasis db;

  public EineKategorieGDP(String text, Datenbasis datenbasis, EinzelKategorie kategorie) {
    super(text);
    this.db = datenbasis;
    comboBox = new JComboBox(datenbasis.getKategorien(unterkategorienVerwenden()));
    comboBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        refreshWert();
      }
    });
    if(kategorie != null)
      comboBox.setSelectedItem(kategorie);
    add(comboBox);
    checkBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e)  {
        refreshRegisterUndKategorien();
        refreshWert();
      }
    });
    add(checkBox);
    refreshWert();
  }

  private boolean unterkategorienVerwenden() {
    return checkBox.isSelected();
  }
  
  protected void refreshWert() {
    wert = comboBox.getSelectedItem();
  }
  
  public void refreshRegisterUndKategorien() {
    EinzelKategorie kategorie = (EinzelKategorie) comboBox.getSelectedItem();
    EinzelKategorie[] kategorien = db.getKategorien(unterkategorienVerwenden());
    comboBox.removeAllItems();
    for(int i=0; i<kategorien.length; i++) {
      comboBox.addItem(kategorien[i]);
    }
    if(kategorie != null)
      if(!kategorie.isHauptkategorie() && !unterkategorienVerwenden())
        comboBox.setSelectedItem(kategorie.getHauptkategorie());
      else
       comboBox.setSelectedItem(kategorie);
  }

  public JComponent getZentraleKomponente() {
    return comboBox;
  }

  public void laden(DataInputStream in) throws IOException {
    String kategorie = in.readUTF();
    for(int i=0; i<comboBox.getItemCount(); i++)
      if(kategorie.equals(""+comboBox.getComponent(i)))
        comboBox.setSelectedIndex(i);
    checkBox.setSelected(in.readBoolean());
  }

  public void speichern(DataOutputStream out) throws IOException {
    out.writeUTF(""+comboBox.getSelectedItem());
    out.writeBoolean(unterkategorienVerwenden());
  }

}