/*
 * This file is part of jHaushalt.
 * jHaushalt is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * jHaushalt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
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
	private static final TextResource RES = TextResource.get();

	private final Haushalt haushalt;
	private final Datenbasis db;
	private final JList liste;
	private final JPanel paneButton = new JPanel();
	private final JButton buttonOK = new JButton(RES.getString("button_ok"));
	private final JButton buttonAbbruch = new JButton(RES.getString("button_cancel"));
	private boolean ok = false;

	private final String[] namen = {
			BABalkenDiagramm.UEBERSCHRIFT, BAEinnahmenAusgaben.UEBERSCHRIFT, BATortenDiagramm.UEBERSCHRIFT,
			BAVermoegenDiagramm.UEBERSCHRIFT, BAKategorieSummen.UEBERSCHRIFT, BAAbsoluterVergleich.UEBERSCHRIFT,
			BARelativerVergleich.UEBERSCHRIFT, BAVermoegenUebersicht.UEBERSCHRIFT, BAVermoegenUebersichtHeute.UEBERSCHRIFT,
			BAKategorieEntwicklung.UEBERSCHRIFT, BAKategorieAusgabe.UEBERSCHRIFT, BAPlanung.UEBERSCHRIFT};

	public DlgAuswertungAuswaehlen(final Haushalt haushalt, final Datenbasis db) {
		super(haushalt.getFrame(), RES.getString("select_report_type"), true);
		this.haushalt = haushalt;
		this.db = db;
		this.liste = new JList(this.namen);
		this.paneButton.add(this.buttonOK, null);
		this.paneButton.add(this.buttonAbbruch, null);
		getContentPane().add(new JScrollPane(this.liste), BorderLayout.CENTER);
		getContentPane().add(this.paneButton, BorderLayout.SOUTH);

		this.liste.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					DlgAuswertungAuswaehlen.this.ok = true;
					setVisible(false);
				}
			}
		});
		this.buttonOK.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				DlgAuswertungAuswaehlen.this.ok = true;
				setVisible(false);
			}
		});
		this.buttonAbbruch.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				DlgAuswertungAuswaehlen.this.ok = false;
				setVisible(false);
			}
		});
		getRootPane().setDefaultButton(this.buttonOK);
	}

	public AbstractAuswertung showDialog() {
		setLocationRelativeTo(getOwner());
		pack();
		setVisible(true);
		final int nr = this.liste.getSelectedIndex();
		if (!this.ok || (nr == -1)) {
			return null;
		}
		AbstractAuswertung auswertung = null;
		switch (nr) {
			case 0:
				auswertung = new BABalkenDiagramm(this.haushalt, this.db, null);
				break;
			case 1:
				auswertung = new BAEinnahmenAusgaben(this.haushalt, this.db, null);
				break;
			case 2:
				auswertung = new BATortenDiagramm(this.haushalt, this.db, null);
				break;
			case 3:
				auswertung = new BAVermoegenDiagramm(this.haushalt, this.db, null);
				break;
			case 4:
				auswertung = new BAKategorieSummen(this.haushalt, this.db, null);
				break;
			case 5:
				auswertung = new BAAbsoluterVergleich(this.haushalt, this.db, null);
				break;
			case 6:
				auswertung = new BARelativerVergleich(this.haushalt, this.db, null);
				break;
			case 7:
				auswertung = new BAVermoegenUebersicht(this.haushalt, this.db, null);
				break;
			case 8:
				auswertung = new BAVermoegenUebersichtHeute(this.haushalt, this.db, null);
				break;
			case 9:
				auswertung = new BAKategorieEntwicklung(this.haushalt, this.db, null);
				break;
			case 10:
				auswertung = new BAKategorieAusgabe(this.haushalt, this.db, null);
				break;
			case 11:
				auswertung = new BAPlanung(this.haushalt, this.db, null);
				break;
			default:
				break;
		}
		return auswertung;
	}

}
