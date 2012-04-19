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

package haushalt.gui.generischerdialog;

import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2008.01.17
 */

/*
 * 2008.01.17 Internationalisierung
 * 2004.08.22 Version 2.0
 */

public class GenerischerDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	private final JPanel paneContainer = new JPanel();
	private final JPanel paneButton = new JPanel();
	private final JButton buttonOK = new JButton(res.getString("button_ok"));
	private final JButton buttonAbbruch = new JButton(res.getString("button_cancel"));
	protected boolean OK = false;
	private final GDFocusTraversalPolicy policy;

	public GenerischerDialog(final String text, final Frame owner) {
		super(owner, text, true);
		this.policy = new GDFocusTraversalPolicy();
		setFocusTraversalPolicy(this.policy);

		this.paneButton.add(this.buttonOK);
		this.paneButton.add(this.buttonAbbruch);
		this.paneContainer.setLayout(new BoxLayout(this.paneContainer, BoxLayout.Y_AXIS));

		final Container contentPane = getContentPane();
		contentPane.add(this.paneContainer, BorderLayout.CENTER);
		contentPane.add(this.paneButton, BorderLayout.SOUTH);

		this.buttonOK.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				GenerischerDialog.this.OK = true;
				setVisible(false);
			}
		});
		this.buttonAbbruch.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				GenerischerDialog.this.OK = false;
				setVisible(false);
			}
		});
		getRootPane().setDefaultButton(this.buttonOK);
	}

	public boolean showDialog() {
		pack();
		setVisible(true);
		return this.OK;
	}

	public void addPane(final AbstractGDPane pane) {
		this.paneContainer.add(pane);
		final JComponent neueKomponente = pane.getZentraleKomponente();
		this.policy.addComponent(neueKomponente);

		neueKomponente.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				pane.refreshWert();
			}
		});
	}

}