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

import haushalt.daten.Datenbasis;
import haushalt.daten.EinzelKategorie;
import haushalt.gui.DeleteableTextField;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

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
 * neuen IKategorie und Sicherstellen, dass die neue
 * IKategorie sichtbar ist
 * 2006.01.27 Keine globale Änderung der Option
 * "Unterkategorien verwenden" mehr
 */

public class DlgKategorienBearbeiten extends JDialog implements TreeSelectionListener {

	private static final boolean DEBUG = false;
	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();

	private static final Logger LOGGER = Logger.getLogger(DlgKategorienBearbeiten.class.getName());
	private final Haushalt haushalt;
	private final Datenbasis db;
	private JScrollPane scrollPane;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private final DefaultTreeCellRenderer cellRenderer;
	private DefaultMutableTreeNode root;
	private final JPanel eastPane = new JPanel();
	private final JPanel erzeugenPane = new JPanel();
	private final DeleteableTextField textErzeugen;
	private final JButton buttonErzeugen = new JButton(RES.getString("button_create"));
	private final JPanel umbenennenPane = new JPanel();
	private final DeleteableTextField textUmbenennen;
	private final JButton buttonUmbenennen = new JButton(RES.getString("button_rename"));
	private final JCheckBox unterkategorienVerwenden = new JCheckBox(RES.getString("use_subcategories"), true);
	private final JPanel buttonPane = new JPanel();
	private final JButton buttonAbbruch = new JButton(RES.getString("button_close"));

	public DlgKategorienBearbeiten(final Haushalt haushalt, final Datenbasis datenbasis) {
		super(haushalt.getFrame(), RES.getString("edit_category"), true); // =
																			// modal
		this.haushalt = haushalt;
		this.db = datenbasis;
		this.scrollPane = new JScrollPane();
		final ImageIcon unterkatIcon = haushalt.bildLaden("Reifen16.png");
		final ImageIcon hauptkatIcon = haushalt.bildLaden("Auto16.png");
		this.cellRenderer = new DefaultTreeCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getTreeCellRendererComponent(
				final JTree lTree,
				final Object value,
				final boolean sel,
				final boolean expanded,
				final boolean leaf,
				final int row,
				final boolean lHasFocus) {

				super.getTreeCellRendererComponent(lTree, value, sel, expanded, leaf, row, lHasFocus);
				final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				if (node.getParent() == null) {
					setIcon(null);
					setToolTipText(null);
				} else if (node.getParent() == DlgKategorienBearbeiten.this.root) {
					setIcon(hauptkatIcon);
					setToolTipText(RES.getString("major_category"));
				} else {
					setIcon(unterkatIcon);
					setToolTipText(RES.getString("subcategory"));
				}
				return this;
			}
		};
		treeErzeugen();
		final Dimension dimensionButton = this.buttonUmbenennen.getPreferredSize();
		this.erzeugenPane.setBorder(BorderFactory.createTitledBorder(RES.getString("create_category")));
		this.textErzeugen = new DeleteableTextField(15) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Document createDefaultModel() {
				return new KategorieDocument();
			}
		};
		final ActionListener erzeugenActionListener = new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				erzeugen();
			}
		};
		this.textErzeugen.addActionListener(erzeugenActionListener);
		this.erzeugenPane.add(this.textErzeugen);
		this.erzeugenPane.add(this.buttonErzeugen);
		this.buttonErzeugen.addActionListener(erzeugenActionListener);
		this.buttonErzeugen.setPreferredSize(dimensionButton);
		this.umbenennenPane.setBorder(BorderFactory.createTitledBorder(RES.getString("rename_category")));
		this.textUmbenennen = new DeleteableTextField(15) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Document createDefaultModel() {
				return new KategorieDocument();
			}
		};
		final ActionListener umbenennenActionListener = new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				umbenennen();
			}
		};
		this.textUmbenennen.addActionListener(umbenennenActionListener);
		this.umbenennenPane.add(this.textUmbenennen);
		this.umbenennenPane.add(this.buttonUmbenennen);
		this.buttonUmbenennen.addActionListener(umbenennenActionListener);
		this.unterkategorienVerwenden.addItemListener(new ItemListener() {

			public void itemStateChanged(final ItemEvent e) {
				treeErzeugen();
			}
		});
		this.eastPane.setLayout(new BoxLayout(this.eastPane, BoxLayout.Y_AXIS));
		this.eastPane.add(this.erzeugenPane);
		this.eastPane.add(this.umbenennenPane);
		this.eastPane.add(this.unterkategorienVerwenden);
		this.buttonAbbruch.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});
		this.buttonAbbruch.setPreferredSize(dimensionButton);
		this.buttonPane.add(this.buttonAbbruch);
		final Container contentPane = getContentPane();
		contentPane.add(this.scrollPane, BorderLayout.WEST);
		contentPane.add(this.eastPane, BorderLayout.EAST);
		contentPane.add(this.buttonPane, BorderLayout.SOUTH);
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
		if (this.tree != null) {
			this.scrollPane.getViewport().remove(this.tree);
		}
		this.root = new DefaultMutableTreeNode(RES.getString("categories"));
		final EinzelKategorie[] kategorien = this.db.getKategorien(this.unterkategorienVerwenden.isSelected());
		DefaultMutableTreeNode haupt = null;
		for (int i = 0; i < kategorien.length; i++) {
			if (kategorien[i].isHauptkategorie()) {
				haupt = new DefaultMutableTreeNode(kategorien[i].getName());
				this.root.add(haupt);
			} else {
				haupt.add(new DefaultMutableTreeNode(kategorien[i].getName()));
			}
			if (DEBUG) {
				LOGGER.info("" + kategorien[i] + " hinzugefügt.");
			}
		}
		this.treeModel = new DefaultTreeModel(this.root);
		this.tree = new JTree(this.treeModel);
		final TreeSelectionModel selectionModel = this.tree.getSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		selectionModel.addTreeSelectionListener(this);
		this.tree.setCellRenderer(this.cellRenderer);
		this.scrollPane.getViewport().add(this.tree);
	}

	protected void erzeugen() {
		if (this.textErzeugen.getText().equals("")) {
			JOptionPane.showMessageDialog(this.haushalt.getFrame(), RES.getString("message_no_category_name"));
			return;
		}
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
		if ((node == null) || (node.getParent() == null)) {
			if (!this.db.isKategorie(this.textErzeugen.getText(), null)) {
				// Erzeuge neue Hauptkategorie:
				final EinzelKategorie kategorie = this.db.findeOderErzeugeKategorie(this.textErzeugen.getText(), null);
				final DefaultMutableTreeNode child = new DefaultMutableTreeNode(kategorie.getHauptkategorie().getName());
				this.treeModel.insertNodeInto(child, this.root, this.root.getChildCount());
				this.tree.scrollPathToVisible(new TreePath(child.getPath()));
				this.textErzeugen.setText("");
			} else {
				JOptionPane.showMessageDialog(this.haushalt.getFrame(), RES.getString("message_category_exists"));
			}
			return;
		}
		if (node.getParent() != this.root) {
			JOptionPane.showMessageDialog(this.haushalt.getFrame(), RES.getString("message_no_major_category_selected"));
			return;
		}
		if (!this.unterkategorienVerwenden.isSelected()) {
			JOptionPane.showMessageDialog(this.haushalt.getFrame(), RES.getString("message_activate_subcategories"));
			return;
		}
		final EinzelKategorie hauptKategorie = this.db.findeOderErzeugeKategorie((String) node.getUserObject(), null);
		if (!this.db.isKategorie(this.textErzeugen.getText(), hauptKategorie)) {
			// Erzeugt neue Unterkategorie:
			final EinzelKategorie kategorie = this.db.findeOderErzeugeKategorie(this.textErzeugen.getText(), hauptKategorie);
			final DefaultMutableTreeNode child = new DefaultMutableTreeNode(kategorie.getName());
			this.treeModel.insertNodeInto(child, node, node.getChildCount());
			this.tree.scrollPathToVisible(new TreePath(child.getPath()));
			this.textErzeugen.setText("");
		} else {
			JOptionPane.showMessageDialog(this.haushalt.getFrame(), RES.getString("message_category_exists"));
		}
	}

	protected void umbenennen() {
		if (this.textUmbenennen.getText().equals("")) {
			JOptionPane.showMessageDialog(this.haushalt.getFrame(), RES.getString("message_category_is_empty"));
			return;
		}
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
		if ((node == null) || (node.getParent() == null)) {
			return;
		}
		EinzelKategorie hauptkategorie;
		if (node.getParent() == this.root) {
			hauptkategorie = null;
		} else {
			final String name = (String) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
			hauptkategorie = this.db.findeOderErzeugeKategorie(name, null);
		}
		if (this.db.isKategorie(this.textUmbenennen.getText(), hauptkategorie)) {
			JOptionPane.showMessageDialog(this.haushalt.getFrame(), RES.getString("message_category_exists"));
			return;
		}
		final EinzelKategorie kategorie = this.db.findeOderErzeugeKategorie("" + node.getUserObject(), hauptkategorie);
		kategorie.setName(this.textUmbenennen.getText());
		node.setUserObject(this.textUmbenennen.getText());
		this.treeModel.nodeChanged(node);
	}

	private static class KategorieDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;

		@Override
		public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
			final char[] source = str.toCharArray();
			final int anzChar = source.length;
			final char[] result = new char[anzChar];
			int j = 0;

			for (int i = 0; i < anzChar; i++) {
				if (Character.isLetterOrDigit(source[i]) || (source[i] == '-') || (source[i] == ' ')) {
					result[j++] = source[i];
				}
			}
			super.insertString(offs, new String(result, 0, j), a);
		}
	}

	// -- Methoden des Interface 'TreeSelectionListener'
	// ----------------------------

	public void valueChanged(final TreeSelectionEvent e) {
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
		if (node == this.root) {
			this.textUmbenennen.setText("");
		} else {
			this.textUmbenennen.setText("" + node.getUserObject());
		}
	}
}
