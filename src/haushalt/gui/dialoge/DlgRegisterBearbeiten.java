/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

jHaushalt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jHaushalt; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

package haushalt.gui.dialoge;

import haushalt.daten.Datenbasis;
import haushalt.daten.Datum;
import haushalt.gui.DeleteableTextField;
import haushalt.gui.EuroField;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Erstellt einen Dialog, um Kategorien zu erzeugen und
 * umzubenennen
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5.3/2008.03.31
 * @since 2.5
 */

/*
 * 2008.03.31 Icons für Buttons verwenden
 * 2007.02.07 Erste Version
 */

public class DlgRegisterBearbeiten extends JDialog {
  private static final TextResource res = TextResource.get();
  private static final long serialVersionUID = 1L;

  private final Haushalt haushalt;
  protected final Datenbasis db;
  private JScrollPane scrollPane;
  private JList list;
  private final JPanel eastPane = new JPanel();
  private final JPanel erzeugenPane = new JPanel();
  private final DeleteableTextField textErzeugen;
  private final JButton buttonErzeugen;
  private final EuroField openingBalance = new EuroField();
  private final JPanel umbenennenPane = new JPanel();
  private final JTextField alterName = new JTextField();
  private final DeleteableTextField textUmbenennen;
  private final JButton buttonUmbenennen;
  private final JPanel buttonPane = new JPanel();
  private final JButton buttonAbbruch;
  private final JPanel sortierenPane = new JPanel();
  private final JButton buttonHoch;
  private final JButton buttonRunter;
  
  public DlgRegisterBearbeiten(final Haushalt haushalt, final Datenbasis datenbasis) {
    super(haushalt.getFrame(), res.getString("edit_registers"), true); // = modal
    this.haushalt = haushalt;
    this.db = datenbasis;
    list = new JList(db.getRegisterNamen());
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent arg0) {
        if(list.isSelectionEmpty())
          alterName.setText("");
        else
          alterName.setText(""+list.getSelectedValue());
      }
    });
    scrollPane = new JScrollPane(list);
    buttonErzeugen = new JButton(res.getString("button_create"), haushalt.bildLaden("New16.png"));
    buttonUmbenennen = new JButton(res.getString("button_rename"));
    buttonAbbruch = new JButton(res.getString("button_close"));
    buttonHoch = new JButton(haushalt.bildLaden("Up16.png"));
    buttonRunter = new JButton(haushalt.bildLaden("Down16.png"));
    Dimension dimensionButton = buttonUmbenennen.getPreferredSize();
    erzeugenPane.setLayout(new GridLayout(0, 2, 5, 5));
    erzeugenPane.setBorder(BorderFactory.createTitledBorder(res.getString("create_register")));
    textErzeugen = new DeleteableTextField(15) {
      private static final long serialVersionUID = 1L;
      protected Document createDefaultModel() {
        return new RegisterDocument();
      }
    };
    ActionListener erzeugenActionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        erzeugen();
      }
    };
    textErzeugen.addActionListener(erzeugenActionListener);
    erzeugenPane.add(new JLabel(res.getString("new_register_name")+":"));
    erzeugenPane.add(textErzeugen);
    erzeugenPane.add(new JLabel(res.getString("opening_balance")+":"));
    erzeugenPane.add(openingBalance);
    erzeugenPane.add(Box.createHorizontalBox());
    erzeugenPane.add(buttonErzeugen);
    buttonErzeugen.addActionListener(erzeugenActionListener);
    buttonErzeugen.setPreferredSize(dimensionButton);
    umbenennenPane.setLayout(new GridLayout(0, 2, 5, 5));
    umbenennenPane.setBorder(BorderFactory.createTitledBorder(res.getString("rename_register")));
    textUmbenennen = new DeleteableTextField(15) {
      private static final long serialVersionUID = 1L;
      protected Document createDefaultModel() {
        return new RegisterDocument();
      }
    };
    ActionListener umbenennenActionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        umbenennen();
      }
    };
    textUmbenennen.addActionListener(umbenennenActionListener);
    umbenennenPane.add(new JLabel(res.getString("old_register_name")+":"));
    alterName.setEnabled(false);
    umbenennenPane.add(alterName);
    umbenennenPane.add(new JLabel(res.getString("new_register_name")+":"));
    umbenennenPane.add(textUmbenennen);
    umbenennenPane.add(Box.createHorizontalBox());
    umbenennenPane.add(buttonUmbenennen);
    buttonUmbenennen.addActionListener(umbenennenActionListener);
    sortierenPane.setBorder(BorderFactory.createTitledBorder(res.getString("sort_register")));
    sortierenPane.setLayout(new GridLayout(0, 2, 5, 5));
    buttonHoch.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nachOben();
      }
    });
    sortierenPane.add(buttonHoch);
    buttonRunter.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nachUnten();
      }
    });
    sortierenPane.add(buttonRunter);
    eastPane.setLayout(new BoxLayout(eastPane, BoxLayout.Y_AXIS));
    eastPane.add(erzeugenPane);
    eastPane.add(umbenennenPane);
    eastPane.add(sortierenPane);
    buttonAbbruch.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
    buttonAbbruch.setPreferredSize(dimensionButton);
    buttonPane.add(buttonAbbruch);
    Container contentPane = getContentPane();
    contentPane.add(scrollPane, BorderLayout.WEST);
    contentPane.add(eastPane, BorderLayout.EAST);
    contentPane.add(buttonPane, BorderLayout.SOUTH);
  }

  /**
   * Zeigt den Dialog auf dem Bildschirm an, wenn er noch nicht sichtbar ist.
   */
  public void showDialog() {
    setLocationRelativeTo(getOwner());
    pack();
    setVisible(true);
  }

  protected void erzeugen() {
    if(textErzeugen.getText().equals("")) {
      JOptionPane.showMessageDialog(haushalt.getFrame(),
      res.getString("message_no_register_name"));
      return;
    }
    String name = db.erzeugeRegister("["+textErzeugen.getText()+"]");
    haushalt.zeigeRegisterTab(name);
    // falls der Name doppelt ist, hat er eine folgende Ziffer erhalten
    db.addUmbuchung(new Datum(), res.getString("opening_balance"), name, name, openingBalance.getValue());
    list.setListData(db.getRegisterNamen());
    textErzeugen.setText("");
    openingBalance.setText("");
  }
  
  protected void umbenennen() {
    if(textUmbenennen.getText().equals("")) {
      JOptionPane.showMessageDialog(haushalt.getFrame(),
      res.getString("message_no_new_register"));
      return;
    }
    if(list.isSelectionEmpty()) {
      JOptionPane.showMessageDialog(haushalt.getFrame(),
      res.getString("message_no_register_selected"));
      return;
    }
    String regname = db.renameRegister(""+list.getSelectedValue(), "["+textUmbenennen.getText()+"]");
    haushalt.renameRegisterTab(""+list.getSelectedValue(), regname);
    list.setListData(db.getRegisterNamen());
    textUmbenennen.setText("");
  }
  
  protected void nachUnten() {
    if(list.isSelectionEmpty()) {
      JOptionPane.showMessageDialog(haushalt.getFrame(),
      res.getString("message_no_register_selected"));
      return;
    }
    int idx = list.getSelectedIndex();
    if(idx < db.getRegisterNamen().length-1) {
      String regname = ""+list.getSelectedValue();
      db.aendereRegisterIndex(regname, idx+1);
      haushalt.bewegeRegisterNachOben(regname);
      list.setListData(db.getRegisterNamen());
      list.setSelectedIndex(idx+1);
    }
  }
  
  protected void nachOben() {
    if(list.isSelectionEmpty()) {
      JOptionPane.showMessageDialog(haushalt.getFrame(),
      res.getString("message_no_register_selected"));
      return;
    }
    int idx = list.getSelectedIndex();
    if(idx > 0) {
      String regname = ""+list.getSelectedValue();
      db.aendereRegisterIndex(regname, idx-1);
      haushalt.bewegeRegisterNachUnten(regname);
      list.setListData(db.getRegisterNamen());
      list.setSelectedIndex(idx-1);
    }
  }
  
  private static class RegisterDocument extends PlainDocument {
    private static final long serialVersionUID = 1L;
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      char[] source = str.toCharArray();
      int anzChar = source.length;
      char[] result = new char[anzChar];
      int j = 0;

      for (int i = 0; i < anzChar; i++) {
        if (Character.isLetterOrDigit(source[i]) || (source[i] == '-'))
          result[j++] = source[i];
      }
      super.insertString(offs, new String(result, 0, j), a);
    }
  }
}
