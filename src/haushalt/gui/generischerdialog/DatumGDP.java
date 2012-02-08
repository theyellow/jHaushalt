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

import haushalt.daten.Datum;
import haushalt.gui.DatumField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JComponent;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */

public class DatumGDP extends AbstractGDPane {

  private static final long serialVersionUID = 1L;
  private DatumField datumField;

  public DatumGDP(String text, Datum datum) {
    super(text);
    datumField = new DatumField(datum);
    add(datumField);
    refreshWert();
  }

  protected void refreshWert() {
    wert = new Datum(datumField.getText());
    datumField.setText(""+wert); // neusetzen, da ggf. Fehler beim Parsen
  }

  public JComponent getZentraleKomponente() {
    return datumField;
  }

  public void laden(DataInputStream in) throws IOException {
    Datum datum = (Datum)getWert();
    datum.laden(in);
    datumField.setText(""+datum);
  }

  public void speichern(DataOutputStream out) throws IOException {
    refreshWert();
    Datum datum = (Datum)getWert();
    datum.speichern(out);
  }

}