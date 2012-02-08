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
 * 2006.01.27 Übergabe der Datenbasis, statt der Register-Namen
 */

public class EinOderAlleRegisterGDP extends AbstractGDPane {
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();
 
  protected final JComboBox comboBox;
  protected final JCheckBox checkBox;
  private final Datenbasis db;

  public EinOderAlleRegisterGDP(String textAufforderung, Datenbasis datenbasis, String auswahl) {
    super(textAufforderung);
    db = datenbasis;
    comboBox = new JComboBox(db.getRegisterNamen());
    comboBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        refreshWert();
      }
    });
    if(auswahl != null)
      comboBox.setSelectedItem(auswahl);
    comboBox.setEnabled(false);
		checkBox = new JCheckBox(res.getString("all_registers"), true);
		checkBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e)  {
		    if(checkBox.isSelected())
		      comboBox.setEnabled(false);
		    else
		      comboBox.setEnabled(true);
		    refreshWert();
		  }
		});
    add(checkBox);
    add(comboBox);
    refreshWert();
  }

  protected void refreshWert() {
    if(checkBox.isSelected())
      wert = null;
    else
      wert = comboBox.getSelectedItem();
  }

  public void refreshRegisterUndKategorien() {
    String[] register = db.getRegisterNamen();
    String regname = (String) comboBox.getSelectedItem();
    comboBox.removeAllItems();
    for(int i=0; i<register.length; i++) {
      comboBox.addItem(register[i]);
    }
    if(regname != null)
      comboBox.setSelectedItem(regname);
  }
  public JComponent getZentraleKomponente() {
    return checkBox;
  }

  public void laden(DataInputStream in) throws IOException {
    checkBox.setSelected(in.readBoolean());
    comboBox.setSelectedItem(in.readUTF());
  }

  public void speichern(DataOutputStream out) throws IOException {
    out.writeBoolean(checkBox.isSelected());
    out.writeUTF((String)comboBox.getSelectedItem());
  }

}
