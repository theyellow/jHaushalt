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

package haushalt.auswertung;

import haushalt.auswertung.planung.BAPlanung;
import haushalt.daten.Datenbasis;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.07.02
 */

/*
 * 2007.07.02 Internationalisierung
 * 2006.02.02 Doppelklick wählt Auswertung aus
 */
public class DlgAuswertungAuswaehlen extends JDialog {
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  private final Haushalt haushalt;
  private final Datenbasis db;
  private final JList liste;
  private JPanel paneButton = new JPanel();
  private final JButton buttonOK = new JButton(res.getString("button_ok"));
  private final JButton buttonAbbruch = new JButton(res.getString("button_cancel"));
  protected boolean OK = false;

	private final String[] namen = {
		BABalkenDiagramm.ueberschrift,
		BAEinnahmenAusgaben.ueberschrift,
		BATortenDiagramm.ueberschrift,
		BAVermoegenDiagramm.ueberschrift,
		BAKategorieSummen.ueberschrift,
		BAAbsoluterVergleich.ueberschrift,
		BARelativerVergleich.ueberschrift,
    BAVermoegenUebersicht.ueberschrift,
    BAVermoegenUebersichtHeute.ueberschrift,
		BAKategorieEntwicklung.ueberschrift,
    BAKategorieAusgabe.ueberschrift,
    BAPlanung.ueberschrift
	};
		
  public DlgAuswertungAuswaehlen(Haushalt haushalt, Datenbasis db) {
    super(haushalt.getFrame(), res.getString("select_report_type"), true);
    this.haushalt = haushalt;
    this.db = db;
    liste = new JList(namen);
    paneButton.add(buttonOK, null);
    paneButton.add(buttonAbbruch, null);
    this.getContentPane().add(new JScrollPane(liste), BorderLayout.CENTER);
    this.getContentPane().add(paneButton, BorderLayout.SOUTH);

    liste.addMouseListener(
      new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
            OK = true;
            setVisible(false);
          }
        }
      }
    );
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        OK = true;
        setVisible(false);
      }
    });
    buttonAbbruch.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        OK = false;
        setVisible(false);
      }
    });
    getRootPane().setDefaultButton(buttonOK);
  }

  public AbstractAuswertung showDialog() {
    setLocationRelativeTo(this.getOwner());
    pack();
    setVisible(true);
    int nr = liste.getSelectedIndex();
    if(!OK || (nr == -1))
      return null;
  	AbstractAuswertung auswertung = null;
    switch(nr) {
      case 0: auswertung = new BABalkenDiagramm(haushalt, db, null); break;
      case 1: auswertung = new BAEinnahmenAusgaben(haushalt, db, null); break;
  		case 2: auswertung = new BATortenDiagramm(haushalt, db, null); break;
  		case 3: auswertung = new BAVermoegenDiagramm(haushalt, db, null); break;
  		case 4: auswertung = new BAKategorieSummen(haushalt, db, null); break;
  		case 5: auswertung = new BAAbsoluterVergleich(haushalt, db, null); break;
  		case 6: auswertung = new BARelativerVergleich(haushalt, db, null); break;
      case 7: auswertung = new BAVermoegenUebersicht(haushalt, db, null); break;
      case 8: auswertung = new BAVermoegenUebersichtHeute(haushalt, db, null); break;
  		case 9: auswertung = new BAKategorieEntwicklung(haushalt, db, null); break;
      case 10: auswertung = new BAKategorieAusgabe(haushalt, db, null); break;
      case 11: auswertung = new BAPlanung(haushalt, db, null); break;
    }
    return auswertung;
  }

}

