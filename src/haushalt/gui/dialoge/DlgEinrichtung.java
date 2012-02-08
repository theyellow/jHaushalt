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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.Properties;

import haushalt.gui.DeleteableTextField;
import haushalt.gui.TextResource;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * Dialog zur Einrichtung beim ersten Starten
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
  private static final TextResource res = TextResource.get();

  private final Locale liste_locales[] = Locale.getAvailableLocales();
  private final JComboBox sprache;
  private final DeleteableTextField waehrung = new DeleteableTextField();
  private final JTextPane textPane = new JTextPane();
  private final JPanel auswahlPane = new JPanel();
  private final JPanel buttonPane = new JPanel();
  private final JButton buttonOK = new JButton(res.getString("button_ok"));

  public DlgEinrichtung(final JFrame frame, final Properties properties) {
    super(frame, res.getString("options"), true); // = modal
    setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    textPane.setEditable(false);
    StyledDocument doc = textPane.getStyledDocument();
    Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
    Style regular = doc.addStyle("regular", def);
    StyleConstants.setFontFamily(regular, "SansSerif");
    Style s = doc.addStyle("large", regular);
    StyleConstants.setFontSize(s, 16);
    StyleConstants.setBold(s, true);
    s = doc.addStyle("icon", regular);
    StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
    URLClassLoader urlLoader = (URLClassLoader)getClass().getClassLoader();
    URL imageURL = urlLoader.findResource("res/jhh-image.png");
    ImageIcon icon = new ImageIcon(imageURL);
    if (icon != null) {
      StyleConstants.setIcon(s, icon);
    }
    try {
      doc.insertString(0, " \n", doc.getStyle("icon"));
      doc.insertString(doc.getLength(), res.getString("message_installation1")+"\n", doc.getStyle("large"));
      doc.insertString(doc.getLength(), res.getString("message_installation2"), doc.getStyle("regular"));
    } catch (BadLocationException e1) {
      e1.printStackTrace();
    }
    String[] sprachen = new String[liste_locales.length];
    for (int i = 0; i < liste_locales.length; i++)
        sprachen[i] = liste_locales[i].getDisplayName();
    sprache = new JComboBox(sprachen);
    String locale_name = res.getLocale().getDisplayName();
    for (int i = 0; i < sprache.getItemCount(); i++)
      if(locale_name.equals(sprache.getItemAt(i)))
        sprache.setSelectedIndex(i);
    auswahlPane.setLayout(new GridLayout(0,2));
    auswahlPane.add(new JLabel(res.getString("language")+":"));
    auswahlPane.add(sprache);
    waehrung.setText("€");
    auswahlPane.add(new JLabel(res.getString("currency_symbol")+":"));
    auswahlPane.add(waehrung);
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        properties.setProperty("jhh.opt.sprache", ""+liste_locales[sprache.getSelectedIndex()]);
        properties.setProperty("jhh.opt.waehrung", waehrung.getText());
        setVisible(false);
      }
    });
    buttonPane.add(buttonOK);
    Container contentPane = getContentPane();
    contentPane.add(textPane, BorderLayout.NORTH);
    contentPane.add(auswahlPane, BorderLayout.CENTER);
    contentPane.add(buttonPane, BorderLayout.SOUTH);
    getRootPane().setDefaultButton(buttonOK);
  }
}
