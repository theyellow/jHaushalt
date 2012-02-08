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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2008.01.17
 */

/*
 * 2008.01.17 BugFix: Übergabe der Überschrift an den ColorChooser
 * 2006.02.02 Erste Version
 */

public class FarbwahlGDP extends AbstractGDPane {
  private static final long serialVersionUID = 1L;

  protected final JButton farbeSelektion = new JButton();

  public FarbwahlGDP(final String textAufforderung, final JFrame frame, Color color) {
    super(textAufforderung);
    farbeSelektion.setText(Integer.toHexString(color.getRGB()).toUpperCase());
    farbeSelektion.setBackground(color);
    farbeSelektion.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Color farbe = JColorChooser.showDialog(frame, textAufforderung, farbeSelektion.getBackground());
        if(farbe != null) {
          farbeSelektion.setText(Integer.toHexString(farbe.getRGB()).toUpperCase());
          farbeSelektion.setBackground(farbe);
        }
      }
    });
    add(farbeSelektion);
    refreshWert();
  }
  
  protected void refreshWert() {
    wert = farbeSelektion.getBackground();
  }

  protected JComponent getZentraleKomponente() {
    return farbeSelektion;
  }

  public void laden(DataInputStream in) throws IOException {
    farbeSelektion.setBackground(new Color(in.readInt()));
    refreshWert();
  }

  public void speichern(DataOutputStream out) throws IOException {
    refreshWert();
    out.writeInt(farbeSelektion.getBackground().getRGB());
  }

}
