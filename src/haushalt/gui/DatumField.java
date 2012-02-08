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

package haushalt.gui;

import java.awt.event.KeyEvent;

import haushalt.daten.Datum;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Textfeld um das Datum einzugeben.
 * Es muss nur den Anfang des aktuellen Datums eingegeben
 * werden. Auf die Punkte zur Trennung kann verzichtet werden.
 * Das aktuelle Datum kann mit +/- erhöht/erniedrigt werden.
 * @author Dr. Lars H. Hahn
 * @version 2.6/2009-08-05
 */

 /* 
  * 2009.08.05 Anpassung an neue Datum.addiereTage(int)
  * 2007.08.07 Pharsen eines nicht deutschen Datums
  * 2007.07.18 Komma als Trennzeichen
  * 2006.06.15 Verwendung der Plus/Minus-Tasten des 
  *            Nummernblocks ermöglicht
  * 2006.01.28 Plus/Minus-Tasten zum Verändern des Datums
  *            hinzugefügt
  */

public class DatumField extends DeleteableTextField {
  private static final boolean DEBUG = false;
  private static final long serialVersionUID = 1L;

  public DatumField() {
    this(new Datum());
  }

  public DatumField(Datum datum) {
    super(""+datum, 8); // 8 Spalten
  }

  protected Document createDefaultModel() {
    return new DatumDocument();
  }

  public void keyReleased(KeyEvent e) {
    super.keyReleased(e);
    int keycode = e.getKeyCode();
    if((keycode == KeyEvent.VK_PLUS) || (keycode == KeyEvent.VK_ADD)) {
      Datum datum = new Datum(getText());
      datum.addiereTage(1);
      setText(""+datum);
    } else if((keycode == KeyEvent.VK_MINUS) || (keycode == KeyEvent.VK_SUBTRACT)) {
      Datum datum = new Datum(getText());
      datum.addiereTage(-1);
      setText(""+datum);
    }
    else if(DEBUG) {
      System.out.println("DatumField: KeyCode = "+keycode);
    }
  }

  private static class DatumDocument extends PlainDocument {
    private static final long serialVersionUID = 1L;
    public void insertString(int offs, String str, AttributeSet a)
      throws BadLocationException {
      char[] source = str.toCharArray();
      char[] result = new char[11]; // max. 11 Zeichen (Ungarisch): yyyy.mm.tt.
      int j = 0;
      if(TextResource.get().getLocale().getLanguage().equals("de")) {
        int anzChar = (source.length < 8)?source.length:8;
  
        for (int i = 0; i < anzChar; i++) {
          if (((source[i] == '.') || (source[i] == ',') || (source[i] == '-')) && ((offs+i == 2) || (offs+i == 5))) {
            result[j++] = '.';
          }
          else {
            if (Character.isDigit(source[i])) {
              if((offs+i == 2) || (offs+i == 5)) {
                result[j++] = '.';
              }
              result[j++] = source[i];
            }
          }
        }
        if(j+getLength() > 8)
          j = 8 - getLength();
      }
      else { /* anderes Land = anderes Datumsformat */
        int anzChar = source.length;
        for (int i = 0; i < anzChar; i++) {
          if (Character.isDigit(source[i]) || (source[i] == '.') || (source[i] == '/') || (source[i] == '-'))  {
              result[j++] = source[i];
          }
        }
      }
      super.insertString(offs, new String(result, 0, j), a);
    }
  }
}