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

import haushalt.gui.DeleteableTextField;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * Dialog zur Einrichtung beim ersten Starten
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5.2/2008.03.11
 * @since 2.5
 */

/*
 * 2008.03.11 BugFix: Falsche Ressourcen-Referenz korrigiert
 * 2007.08.07 Erste Version
 */
public class DlgEinrichtung extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();
	private static final Logger LOGGER = Logger.getLogger(DlgEinrichtung.class.getName());

	private final Locale[] listeLocales = Locale.getAvailableLocales();
	private final JComboBox sprache;
	private final DeleteableTextField waehrung = new DeleteableTextField();
	private final JTextPane textPane = new JTextPane();
	private final JPanel auswahlPane = new JPanel();
	private final JPanel buttonPane = new JPanel();
	private final JButton buttonOK = new JButton(RES.getString("button_ok"));

	public DlgEinrichtung(final JFrame frame, final Properties properties) {
		super(frame, RES.getString("options"), true); // = modal
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.textPane.setEditable(false);
		final StyledDocument doc = this.textPane.getStyledDocument();
		final Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		final Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(regular, "SansSerif");
		Style s = doc.addStyle("large", regular);
		StyleConstants.setFontSize(s, 16);
		StyleConstants.setBold(s, true);
		s = doc.addStyle("icon", regular);
		StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
		final URLClassLoader urlLoader = (URLClassLoader) getClass().getClassLoader();
		final URL imageURL = urlLoader.findResource("RES/jhh-image.png");
		final ImageIcon icon = new ImageIcon(imageURL);
		if (icon != null) {
			StyleConstants.setIcon(s, icon);
		}
		try {
			doc.insertString(0, " \n", doc.getStyle("icon"));
			doc.insertString(doc.getLength(), RES.getString("message_installation1") + "\n", doc.getStyle("large"));
			doc.insertString(doc.getLength(), RES.getString("message_installation2"), doc.getStyle("regular"));
		} catch (final BadLocationException e1) {
			LOGGER.warning(e1.getMessage());
		}
		final String[] sprachen = new String[this.listeLocales.length];
		for (int i = 0; i < this.listeLocales.length; i++) {
			sprachen[i] = this.listeLocales[i].getDisplayName();
		}
		this.sprache = new JComboBox(sprachen);
		final String localeName = RES.getLocale().getDisplayName();
		for (int i = 0; i < this.sprache.getItemCount(); i++) {
			if (localeName.equals(this.sprache.getItemAt(i))) {
				this.sprache.setSelectedIndex(i);
			}
		}
		this.auswahlPane.setLayout(new GridLayout(0, 2));
		this.auswahlPane.add(new JLabel(RES.getString("language") + ":"));
		this.auswahlPane.add(this.sprache);
		this.waehrung.setText("€");
		this.auswahlPane.add(new JLabel(RES.getString("currency_symbol") + ":"));
		this.auswahlPane.add(this.waehrung);
		this.buttonOK.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				properties.setProperty(
						"jhh.opt.sprache",
						"" + DlgEinrichtung.this.listeLocales[DlgEinrichtung.this.sprache.getSelectedIndex()]);
				properties.setProperty("jhh.opt.waehrung", DlgEinrichtung.this.waehrung.getText());
				setVisible(false);
			}
		});
		this.buttonPane.add(this.buttonOK);
		final Container contentPane = getContentPane();
		contentPane.add(this.textPane, BorderLayout.NORTH);
		contentPane.add(this.auswahlPane, BorderLayout.CENTER);
		contentPane.add(this.buttonPane, BorderLayout.SOUTH);
		getRootPane().setDefaultButton(this.buttonOK);
	}
}
