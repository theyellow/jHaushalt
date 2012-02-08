/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
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

import haushalt.gui.DeleteableTextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.JComponent;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.1/2006.02.14
 */

/*
 * 2006.02.14 BugFix: Eingabe von "0" verhindern
 * 2004.08.22 Erste Version
 */

public class ZahlGDP extends AbstractGDPane {
	private static final boolean DEBUG = false;
  private static final long serialVersionUID = 1L;

  private final DeleteableTextField textField;

  public ZahlGDP(String textAufforderung, Integer wertEingabe) {
    super(textAufforderung);
    textField = new DeleteableTextField(""+wertEingabe, 4) {
      private static final long serialVersionUID = 1L;
      protected Document createDefaultModel() {
        return new ZahlDocument();
      }
    };
    add(textField);
    refreshWert();
  }

  protected void refreshWert() {
    String text = textField.getText();
    if(text.equals(""))
      text = "1";
    wert = new Integer(text);
    if(wert.equals(0))
      wert = 1;
    textField.setText(""+wert);
  }

  public JComponent getZentraleKomponente() {
    return textField;
  }

  public void laden(DataInputStream in) throws IOException {
    textField.setText(in.readUTF());
    refreshWert();
  }

  public void speichern(DataOutputStream out) throws IOException {
    refreshWert();
    out.writeUTF(textField.getText());
  }

  private static class ZahlDocument extends PlainDocument {
    private static final long serialVersionUID = 1L;
    public void insertString(int offs, String str, AttributeSet a)
      throws BadLocationException {
      char[] source = str.toCharArray();
      char[] result = new char[source.length];
      int j = 0;

      for (int i = 0; i < source.length; i++) {
        if (Character.isDigit(source[i]))
          result[j++] = source[i];
        else if(DEBUG)
					System.out.println("-I- Falsches Zeichen: " + source[i]);
      }

      super.insertString(offs, new String(result, 0, j), a);
    }
  }

}