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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */
public class TextArrayGDP extends AbstractGDPane {

  private static final long serialVersionUID = 1L;
  protected final JComboBox comboBox;

  public TextArrayGDP(String textAnweisung, String[] texte, String auswahl) {
    super(textAnweisung);
    comboBox = new JComboBox(texte);
    comboBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        refreshWert();
      }
    });
    if(auswahl != null)
    	comboBox.setSelectedItem(auswahl);
    add(comboBox);
    refreshWert();
  }

  protected void refreshWert() {
    wert = comboBox.getSelectedItem();
  }

  public JComponent getZentraleKomponente() {
    return comboBox;
  }
  
  public void laden(DataInputStream in) throws IOException {
    comboBox.setSelectedItem(in.readUTF());
  }

  public void speichern(DataOutputStream out) throws IOException {
    out.writeUTF((String)comboBox.getSelectedItem());
  }

}
