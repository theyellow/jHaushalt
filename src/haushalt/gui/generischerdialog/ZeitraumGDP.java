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

import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.gui.DeleteableTextField;
import haushalt.gui.ZeitraumComboBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */

public class ZeitraumGDP extends AbstractGDPane {
	private static final boolean DEBUG = false;
  private static final long serialVersionUID = 1L;

  private final ZeitraumComboBox comboBox;
  private final DeleteableTextField textField;

  public ZeitraumGDP(String textAufforderung, AbstractZeitraum zeitraum) {
    super(textAufforderung);
		comboBox = new ZeitraumComboBox(zeitraum);
    textField = new DeleteableTextField(zeitraum.getDatenString(), 10) {
      private static final long serialVersionUID = 1L;
      protected Document createDefaultModel() {
        return new ZeitraumDocument();
      }
    };
    add(comboBox);
    add(textField);
    refreshWert();
  }

  protected void refreshWert() {
    wert = comboBox.getZeitraum(textField.getText());
  }

  public JComponent getZentraleKomponente() {
    return textField;
  }

  public void laden(DataInputStream in) throws IOException {
    final AbstractZeitraum zeitraum = AbstractZeitraum.laden(in);
    comboBox.waehleZeitraum(zeitraum);
    textField.setText(zeitraum.getDatenString());
  }

  public void speichern(DataOutputStream out) throws IOException {
    AbstractZeitraum zeitraum = comboBox.getZeitraum(textField.getText());
    zeitraum.speichern(out);
  }

  private static class ZeitraumDocument extends PlainDocument {
    private static final long serialVersionUID = 1L;
    public void insertString(int offs, String str, AttributeSet a)
      throws BadLocationException {
      char[] source = str.toCharArray();
      char[] result = new char[source.length];
      int j = 0;

      for (int i = 0; i < source.length; i++) {
        if ((Character.isDigit(source[i])) ||
            (source[i] == '/') ||
            (source[i] == '-') ||
            (source[i] == '.'))
          result[j++] = source[i];
        else if(DEBUG)
					System.out.println("-I- Falsches Zeichen: " + source[i]);
      }

      super.insertString(offs, new String(result, 0, j), a);
    }
  }
}