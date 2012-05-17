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
 * 
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
	private static final TextResource RES = TextResource.get();

	// GUI-Komponenten
	private final JButton beenden = new JButton(RES.getString("button_close"));
	private final JButton suchen = new JButton(RES.getString("button_search"));
	private final JButton ersetzen = new JButton(RES.getString("button_replace"));
	private final JButton alle = new JButton(RES.getString("button_replace_all"));
	private final DeleteableTextField suchenText = new DeleteableTextField(20);
	private final DeleteableTextField ersetzenText = new DeleteableTextField(20);
	private final JCheckBox grossUndKlein = new JCheckBox(RES.getString("case_sensitivity"));

	// Daten
	private final Haushalt haushalt;
	private Datenbasis db;
	private AbstractBuchung buchung;

	/**
	 * Einziger Konstruktor.
	 * 
	 * @param haushalt
	 *            Haupt-Klasse
	 */
	public DlgSuchenErsetzen(final Haushalt haushalt) {
		super(haushalt.getFrame(), RES.getString("find"));
		this.haushalt = haushalt;
		this.suchenText.addInputMethodListener(new InputMethodListener() {

			public void inputMethodTextChanged(final InputMethodEvent event) {
				DlgSuchenErsetzen.this.db.resetSuchIdx();
				DlgSuchenErsetzen.this.ersetzen.setEnabled(false);
			}

			public void caretPositionChanged(final InputMethodEvent event) {
				// wird nicht benötigt
			}
		});
		this.beenden.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});
		this.suchen.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				if (!DlgSuchenErsetzen.this.suchenText.getText().equals("")) {
					if (suchen()) {
						DlgSuchenErsetzen.this.ersetzen.setEnabled(true);
					} else {
						DlgSuchenErsetzen.this.ersetzen.setEnabled(false);
						JOptionPane.showMessageDialog(haushalt.getFrame(), RES.getString("search_completed"));
					}
				}
			}
		});
		this.ersetzen.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				if (DlgSuchenErsetzen.this.buchung.ersetzeText(
						DlgSuchenErsetzen.this.suchenText.getText(),
						DlgSuchenErsetzen.this.ersetzenText.getText())) {
					DlgSuchenErsetzen.this.ersetzen.setEnabled(false);
				} else {
					JOptionPane.showMessageDialog(haushalt.getFrame(), RES.getString("replace_not_successful"));
				}
			}
		});
		this.ersetzen.setEnabled(false);
		this.alle.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				if (!DlgSuchenErsetzen.this.suchenText.getText().equals("")) {
					DlgSuchenErsetzen.this.db.resetSuchIdx();
					int z = 0;
					while (suchen()) {
						if (DlgSuchenErsetzen.this.buchung.ersetzeText(
								DlgSuchenErsetzen.this.suchenText.getText(),
								DlgSuchenErsetzen.this.ersetzenText.getText())) {
							z++;
						}
					}
					DlgSuchenErsetzen.this.ersetzen.setEnabled(false);
					JOptionPane.showMessageDialog(
							haushalt.getFrame(),
							RES.getString("replace_count1") + " " + z + " " + RES.getString("replace_count2"));
				}
			}
		});

		final Container contentPane = getContentPane();
		contentPane.setLayout(new GridLayout(0, 2));
		contentPane.add(new JLabel(RES.getString("search_text")));
		contentPane.add(Box.createGlue());
		contentPane.add(this.suchenText);
		contentPane.add(this.suchen);
		contentPane.add(this.grossUndKlein);
		contentPane.add(Box.createGlue());
		contentPane.add(new JLabel(RES.getString("replace_by")));
		contentPane.add(Box.createGlue());
		contentPane.add(this.ersetzenText);
		contentPane.add(this.ersetzen);
		contentPane.add(this.beenden);
		contentPane.add(this.alle);

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		getRootPane().setDefaultButton(this.suchen);
	}

	/**
	 * Zeigt den Dialog auf dem Bildschirm an, wenn er noch nicht sichtbar ist.
	 */
	public void showDialog(final Datenbasis datenbasis) {
		this.db = datenbasis;
		if (!isVisible()) {
			pack();
			setVisible(true);
		}
		toFront();
		this.db.resetSuchIdx();
	}

	boolean suchen() {
		final String text = this.suchenText.getText();
		this.buchung = this.db.suchen(text, this.grossUndKlein.isSelected());
		if (this.buchung == null) {
			return false;
		}
		this.haushalt.selektiereBuchung(this.db.getRegisterGefundenerText(), this.db.getBuchNrGefundenerText());
		return true;
	}

}
