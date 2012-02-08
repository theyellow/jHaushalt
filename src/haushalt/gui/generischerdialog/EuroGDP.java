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

import haushalt.daten.Euro;
import haushalt.gui.EuroField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JComponent;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */

public class EuroGDP extends AbstractGDPane {

  private static final long serialVersionUID = 1L;
  private final EuroField euroField;

  public EuroGDP(String text, Euro euro) {
    super(text);
    euroField = new EuroField(euro);
    add(euroField);
    refreshWert();
  }

  protected void refreshWert() {
    Euro euro = euroField.getValue();
    wert = euro;
    if(euro.equals(Euro.NULL_EURO))
      euroField.setText("");
    else
      euroField.setText(""+euro); // neusetzen, da ggf. Fehler beim Parsen
  }

  public JComponent getZentraleKomponente() {
    return euroField;
  }

  public void laden(DataInputStream in) throws IOException {
    Euro euro = (Euro)getWert();
    euro.laden(in);
    if(euro.equals(Euro.NULL_EURO))
      euroField.setText("");
    else
      euroField.setText(""+euro);
  }

  public void speichern(DataOutputStream out) throws IOException {
    refreshWert();
    Euro euro = (Euro)getWert();
    euro.speichern(out);
  }

}