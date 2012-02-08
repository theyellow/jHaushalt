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

import haushalt.gui.ProzentField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JComponent;


/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.02.20
 */

/*
 * 2007.02.20 Erste Version (2.5)
 */

public class ProzentGDP extends AbstractGDPane {
  private static final long serialVersionUID = 1L;

  private final ProzentField prozentField;

  public ProzentGDP(String text) {
    super(text);
    prozentField = new ProzentField("");
    add(prozentField);
    refreshWert();
  }

  protected void refreshWert() {
    Integer prozent = prozentField.getValue();
    wert = prozent;
    prozentField.setText(""+prozent); // neusetzen, da ggf. Fehler beim Parsen
  }

  public JComponent getZentraleKomponente() {
    return prozentField;
  }

  public void laden(DataInputStream in) throws IOException {
    prozentField.setText(in.readUTF());
    refreshWert();
  }

  public void speichern(DataOutputStream out) throws IOException {
    refreshWert();
    out.writeUTF(prozentField.getText());
  }

}
