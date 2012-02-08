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

package haushalt.gui.dialoge;

import haushalt.daten.AbstractBuchung;
import haushalt.daten.Datenbasis;
import haushalt.gui.DeleteableTextField;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

 /**
  * Dialog zum Suchen und Ersetzen.
  * @author Dr. Lars H. Hahn
  * @version 2.5/2007.05.31
  * @since 2.0
  */
 /*
  * 2007.05.31 Internationalisierung
  * 2004.08.22 Erste Version
  */
public class DlgSuchenErsetzen extends JDialog {
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  // GUI-Komponenten
  private final JButton beenden = new JButton(res.getString("button_close"));
  private final JButton suchen = new JButton(res.getString("button_search"));
  protected final JButton ersetzen = new JButton(res.getString("button_replace"));
  private final JButton alle = new JButton(res.getString("button_replace_all"));
  protected final DeleteableTextField suchenText = new DeleteableTextField(20);
  protected final DeleteableTextField ersetzenText = new DeleteableTextField(20);
  private final JCheckBox grossUndKlein = new JCheckBox(res.getString("case_sensitivity"));

  // Daten
  private final Haushalt haushalt;
  protected Datenbasis db;
  protected AbstractBuchung buchung;  

  /**
   * Einziger Konstruktor.
   * @param haushalt Haupt-Klasse
   */
  public DlgSuchenErsetzen(final Haushalt haushalt) {
    super(haushalt.getFrame(), res.getString("find"));
    this.haushalt = haushalt;
    suchenText.addInputMethodListener(new InputMethodListener() {
      public void inputMethodTextChanged(InputMethodEvent event) {
        db.resetSuchIdx();
        ersetzen.setEnabled(false);
      }
      public void caretPositionChanged(InputMethodEvent event) {
        // wird nicht benötigt
      }
    });
    beenden.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
    suchen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(!suchenText.getText().equals("")) {
          if(suchen())
            ersetzen.setEnabled(true);
          else {
            ersetzen.setEnabled(false);
            JOptionPane.showMessageDialog(haushalt.getFrame(), res.getString("search_completed"));
          }
        }
      }
    });
    ersetzen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(buchung.ersetzeText(suchenText.getText(), ersetzenText.getText()))
          ersetzen.setEnabled(false);
        else
          JOptionPane.showMessageDialog(haushalt.getFrame(), res.getString("replace_not_successful"));
      }
    });
    ersetzen.setEnabled(false);
    alle.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(!suchenText.getText().equals("")) {
          db.resetSuchIdx();
          int z = 0;
          while(suchen()) {
            if(buchung.ersetzeText(suchenText.getText(), ersetzenText.getText()))
              z++;
          }
          ersetzen.setEnabled(false);
          JOptionPane.showMessageDialog(haushalt.getFrame(), 
              res.getString("replace_count1")+" "+z+" "+res.getString("replace_count2"));
        }
      }
    });

    Container contentPane = getContentPane();
    contentPane.setLayout(new GridLayout(0,2));
    contentPane.add(new JLabel(res.getString("search_text")));
    contentPane.add(Box.createGlue());
    contentPane.add(suchenText);
    contentPane.add(suchen);
    contentPane.add(grossUndKlein);
    contentPane.add(Box.createGlue());
    contentPane.add(new JLabel(res.getString("replace_by")));
    contentPane.add(Box.createGlue());
    contentPane.add(ersetzenText);
    contentPane.add(ersetzen);
    contentPane.add(beenden);
    contentPane.add(alle);

    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    getRootPane().setDefaultButton(suchen);
 }

 /**
  * Zeigt den Dialog auf dem Bildschirm an, wenn er noch nicht sichtbar ist.
  */
  public void showDialog(Datenbasis datenbasis) {
    this.db = datenbasis;
    if(!isVisible()) {
      pack();
      setVisible(true);
    }
    toFront();
    db.resetSuchIdx();
  }

	boolean suchen() {
	  String text = suchenText.getText();
		buchung = db.suchen(text, grossUndKlein.isSelected());
		if(buchung == null)
		  return false;
    haushalt.selektiereBuchung(db.getRegisterGefundenerText(), db.getBuchNrGefundenerText());
		return true;
	}

}