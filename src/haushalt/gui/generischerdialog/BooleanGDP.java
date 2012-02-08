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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.1/2006.02.02
 */

/*
 * 2006.02.02 Erste Version
 */

public class BooleanGDP extends AbstractGDPane {
  private static final long serialVersionUID = 1L;

  private final JCheckBox checkBox;

  public BooleanGDP(String textAufforderung, Boolean wertEingabe, String textAuswahl) {
    super(textAufforderung);
    checkBox = new JCheckBox(textAuswahl, wertEingabe.booleanValue());
    add(checkBox);
    refreshWert();
  }

  protected void refreshWert() {
    wert = Boolean.valueOf(checkBox.isSelected());
  }

  protected JComponent getZentraleKomponente() {
    return checkBox;
  }

  public void laden(DataInputStream in) throws IOException {
    checkBox.setSelected(in.readBoolean());
    refreshWert();
  }

  public void speichern(DataOutputStream out) throws IOException {
    refreshWert();
    out.writeBoolean(checkBox.isSelected());
  }

}
