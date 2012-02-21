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

import haushalt.daten.Datenbasis;
import haushalt.daten.EinzelKategorie;
import haushalt.gui.DeleteableTextField;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Erstellt einen Dialog, um Kategorien zu erzeugen und
 * umzubenennen
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5.3/2007.05.29
 */

/*
 * 2008.03.31 BugFix: Kategoriennamen mit Leerzeichen ermöglicht
 * 2007.05.29 Internationalisierung
 * 2006.02.14 Löschen des TextFields nach an dem Anlegen einer
 *            neuen Kategorie und Sicherstellen, dass die neue
 *            Kategorie sichtbar ist
 * 2006.01.27 Keine globale Änderung der Option
 *            "Unterkategorien verwenden" mehr
 */

public class DlgKategorienBearbeiten extends JDialog implements TreeSelectionListener {
  private static final boolean DEBUG = false;
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  private final Haushalt haushalt;
  protected final Datenbasis db;
  private JScrollPane scrollPane;
  private JTree tree;
  private DefaultTreeModel treeModel;
  private final DefaultTreeCellRenderer cellRenderer;
  protected DefaultMutableTreeNode root;
  private final JPanel eastPane = new JPanel();
  private final JPanel erzeugenPane = new JPanel();
  private final DeleteableTextField textErzeugen;
  private final JButton buttonErzeugen = new JButton(res.getString("button_create"));
  private final JPanel umbenennenPane = new JPanel();
  private final DeleteableTextField textUmbenennen;
  private final JButton buttonUmbenennen = new JButton(res.getString("button_rename"));
  protected final JCheckBox unterkategorienVerwenden = new JCheckBox(res.getString("use_subcategories"), true);
  private final JPanel buttonPane = new JPanel();
  private final JButton buttonAbbruch = new JButton(res.getString("button_close"));
  
  public DlgKategorienBearbeiten(Haushalt haushalt, final Datenbasis datenbasis) throws HeadlessException {
    super(haushalt.getFrame(), res.getString("edit_category"), true); // = modal
    this.haushalt = haushalt;
    this.db = datenbasis;
    scrollPane = new JScrollPane();
    final ImageIcon unterkatIcon = haushalt.bildLaden("Reifen16.png");
    final ImageIcon hauptkatIcon = haushalt.bildLaden("Auto16.png");
    cellRenderer = new DefaultTreeCellRenderer(){
      private static final long serialVersionUID = 1L;
      public Component getTreeCellRendererComponent(
        JTree l_tree,
        Object value,
        boolean sel,
        boolean expanded,
        boolean leaf,
        int row,
        boolean l_hasFocus) {

      	super.getTreeCellRendererComponent(l_tree, value, sel, expanded, leaf, row, l_hasFocus);
      	DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
      	if(node.getParent() == null) {
        	setIcon(null);
        	setToolTipText(null);
      	}
      	else if(node.getParent() == root) {
        	setIcon(hauptkatIcon);
        	setToolTipText(res.getString("major_category"));
      	}
      	else {
        	setIcon(unterkatIcon);
        	setToolTipText(res.getString("subcategory"));
       	}
      	return this;        	
      }
    };
    treeErzeugen();
    Dimension dimensionButton = buttonUmbenennen.getPreferredSize();
    erzeugenPane.setBorder(BorderFactory.createTitledBorder(res.getString("create_category")));
    textErzeugen = new DeleteableTextField(15) {
      private static final long serialVersionUID = 1L;
      protected Document createDefaultModel() {
        return new KategorieDocument();
      }
    };
    ActionListener erzeugenActionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        erzeugen();
      }
    };
    textErzeugen.addActionListener(erzeugenActionListener);
    erzeugenPane.add(textErzeugen);
    erzeugenPane.add(buttonErzeugen);
    buttonErzeugen.addActionListener(erzeugenActionListener);
    buttonErzeugen.setPreferredSize(dimensionButton);
    umbenennenPane.setBorder(BorderFactory.createTitledBorder(res.getString("rename_category")));
    textUmbenennen = new DeleteableTextField(15) {
      private static final long serialVersionUID = 1L;
      protected Document createDefaultModel() {
        return new KategorieDocument();
      }
    };
    ActionListener umbenennenActionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        umbenennen();
      }
    };
    textUmbenennen.addActionListener(umbenennenActionListener);
    umbenennenPane.add(textUmbenennen);
    umbenennenPane.add(buttonUmbenennen);
    buttonUmbenennen.addActionListener(umbenennenActionListener);
    unterkategorienVerwenden.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e)  {
        treeErzeugen();
      }
    });
    eastPane.setLayout(new BoxLayout(eastPane, BoxLayout.Y_AXIS));
    eastPane.add(erzeugenPane);
    eastPane.add(umbenennenPane);
    eastPane.add(unterkategorienVerwenden);
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

  protected void treeErzeugen() {
    if(tree != null)
      scrollPane.getViewport().remove(tree);
    root = new DefaultMutableTreeNode(res.getString("categories"));
    EinzelKategorie[] kategorien = db.getKategorien(unterkategorienVerwenden.isSelected());
    DefaultMutableTreeNode haupt = null;
    for(int i=0; i<kategorien.length; i++) {
      if(kategorien[i].isHauptkategorie()) {
        haupt = new DefaultMutableTreeNode(kategorien[i].getName());
        root.add(haupt);
      }
      else
        haupt.add(new DefaultMutableTreeNode(kategorien[i].getName()));
      if(DEBUG)
        System.out.println(""+kategorien[i]+" hinzugefügt.");
    }
    treeModel = new DefaultTreeModel(root);
    tree = new JTree(treeModel);
    TreeSelectionModel selectionModel = tree.getSelectionModel();
    selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    selectionModel.addTreeSelectionListener(this);
    tree.setCellRenderer(cellRenderer);
    scrollPane.getViewport().add(tree);
    }

  protected void erzeugen() {
    if(textErzeugen.getText().equals("")) {
      JOptionPane.showMessageDialog(haushalt.getFrame(),
      res.getString("message_no_category_name"));
      return;
    }
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
    if((node == null) || (node.getParent() == null)) {
      if(!db.isKategorie(textErzeugen.getText(), null)) {
        // Erzeuge neue Hauptkategorie:
	      EinzelKategorie kategorie = db.findeOderErzeugeKategorie(textErzeugen.getText(), null);
	      DefaultMutableTreeNode child = new DefaultMutableTreeNode(kategorie.getHauptkategorie().getName());
	      treeModel.insertNodeInto(child, root, root.getChildCount());
        tree.scrollPathToVisible(new TreePath(child.getPath()));
        textErzeugen.setText("");
      }
      else {
        JOptionPane.showMessageDialog(haushalt.getFrame(),
        res.getString("message_category_exists"));
      }
      return;
    }
    if(node.getParent() != root) {
      JOptionPane.showMessageDialog(haushalt.getFrame(),
      res.getString("message_no_major_category_selected"));
      return;
    }
    if(!unterkategorienVerwenden.isSelected()) {
      JOptionPane.showMessageDialog(haushalt.getFrame(),
      res.getString("message_activate_subcategories"));
      return;
    }
    EinzelKategorie hauptKategorie = db.findeOderErzeugeKategorie((String)node.getUserObject(), null);
    if(!db.isKategorie(textErzeugen.getText(), hauptKategorie)) {
      // Erzeugt neue Unterkategorie:
	    EinzelKategorie kategorie = db.findeOderErzeugeKategorie(textErzeugen.getText(), hauptKategorie);
	    DefaultMutableTreeNode child = new DefaultMutableTreeNode(kategorie.getName());
	    treeModel.insertNodeInto(child, node, node.getChildCount());
      tree.scrollPathToVisible(new TreePath(child.getPath()));
      textErzeugen.setText("");
    }
    else {
      JOptionPane.showMessageDialog(haushalt.getFrame(),
          res.getString("message_category_exists"));
    }
  }
  
  protected void umbenennen() {
    if(textUmbenennen.getText().equals("")) {
      JOptionPane.showMessageDialog(haushalt.getFrame(),
      res.getString("message_category_is_empty"));
      return;
    }
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
    if((node == null) || (node.getParent() == null))
      return;
    EinzelKategorie hauptkategorie;
    if(node.getParent() == root) {
      hauptkategorie = null;
    }
    else {
	    String name = (String)((DefaultMutableTreeNode) node.getParent()).getUserObject();
	    hauptkategorie = db.findeOderErzeugeKategorie(name, null);
    }
    if(db.isKategorie(textUmbenennen.getText(), hauptkategorie)) {
      JOptionPane.showMessageDialog(haushalt.getFrame(),
      res.getString("message_category_exists"));
      return;
    }
    EinzelKategorie kategorie = db.findeOderErzeugeKategorie(""+node.getUserObject(), hauptkategorie);
    kategorie.setName(textUmbenennen.getText());
    node.setUserObject(textUmbenennen.getText());
    treeModel.nodeChanged(node);
  }
  
  private static class KategorieDocument extends PlainDocument {
    private static final long serialVersionUID = 1L;
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      char[] source = str.toCharArray();
      int anzChar = source.length;
      char[] result = new char[anzChar];
      int j = 0;

      for (int i = 0; i < anzChar; i++) {
        if (Character.isLetterOrDigit(source[i]) || (source[i] == '-') || (source[i] == ' '))
          result[j++] = source[i];
      }
      super.insertString(offs, new String(result, 0, j), a);
    }
  }

  // -- Methoden des Interface 'TreeSelectionListener' ----------------------------
  
  public void valueChanged(TreeSelectionEvent e) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
    if(node == root)
      textUmbenennen.setText("");
    else
      textUmbenennen.setText(""+node.getUserObject());
  }
}
