/*
 * This file is part of jHaushalt.
 * jHaushalt is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
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

import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Dialog mit dem HTML-Seiten angezeigt werden können.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.02.28
 * @since 2.0
 */

/*
 * 2007.02.28 Internationalisierung
 * 2004.08.22 Erste Version
 */
public class DlgHilfe extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	// GUI-Komponenten
	private final JPanel buttonPane = new JPanel();
	private final JButton buttonOK = new JButton(res.getString("button_ok"));
	private final JEditorPane editorPane = new JEditorPane();

	public DlgHilfe(final JFrame frame) {
		super(frame, res.getString("help"), false);
		this.editorPane.setPreferredSize(new Dimension(600, 400));
		this.editorPane.setEditable(false);
		this.editorPane.addHyperlinkListener(new HyperlinkListener() {

			public void hyperlinkUpdate(final HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					final JEditorPane pane = (JEditorPane) e.getSource();
					try {
						pane.setPage(e.getURL());
					}
					catch (final IOException e1) {
						pane.setText(res.getString("help_not_found") + e.getURL());
					}
				}
			}
		});
		final String code = res.getLocale().getLanguage();
		final URLClassLoader urlLoader = (URLClassLoader) getClass().getClassLoader();
		final URL url = urlLoader.findResource("res/jhh-help_" + code + ".html");
		try {
			this.editorPane.setPage(url);
		}
		catch (final IOException e1) {
			this.editorPane.setText(res.getString("help_not_found") + " " + url + "\nCountry-Code: " + code);
		}

		final Container contentPane = getContentPane();
		contentPane.add(new JScrollPane(this.editorPane), BorderLayout.CENTER);
		contentPane.add(this.buttonPane, BorderLayout.SOUTH);
		this.buttonOK.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});
		this.buttonPane.add(this.buttonOK);
	}

}