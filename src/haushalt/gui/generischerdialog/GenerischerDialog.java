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

import haushalt.gui.TextResource;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

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

  private JPanel paneContainer = new JPanel();
  private JPanel paneButton = new JPanel();
  private final JButton buttonOK = new JButton(res.getString("button_ok"));
  private final JButton buttonAbbruch = new JButton(res.getString("button_cancel"));
  protected boolean OK = false;
  private final GDFocusTraversalPolicy policy;

  public GenerischerDialog(String text, Frame owner) {
    super(owner, text, true);
		policy = new GDFocusTraversalPolicy();
		setFocusTraversalPolicy(policy);

    paneButton.add(buttonOK);
    paneButton.add(buttonAbbruch);
    paneContainer.setLayout(new BoxLayout(paneContainer, BoxLayout.Y_AXIS));
    
    Container contentPane = getContentPane();
    contentPane.add(paneContainer, BorderLayout.CENTER);
    contentPane.add(paneButton, BorderLayout.SOUTH);

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

  public boolean showDialog() {
    pack();
    setVisible(true);
    return OK;
  }

  public void addPane(final AbstractGDPane pane) {
    paneContainer.add(pane);
    final JComponent neueKomponente = pane.getZentraleKomponente();
    policy.addComponent(neueKomponente);

    neueKomponente.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        pane.refreshWert();
      }
    });
  }

}