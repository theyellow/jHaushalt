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
 * along with jHaushalt; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
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
		super(haushalt.getFrame(), res.getString("edit_registers"), true); // =
																			// modal
		this.haushalt = haushalt;
		this.db = datenbasis;
		this.list = new JList(this.db.getRegisterNamen());
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.list.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(final ListSelectionEvent arg0) {
				if (DlgRegisterBearbeiten.this.list.isSelectionEmpty()) {
					DlgRegisterBearbeiten.this.alterName.setText("");
				}
				else {
					DlgRegisterBearbeiten.this.alterName.setText(""
							+ DlgRegisterBearbeiten.this.list.getSelectedValue());
				}
			}
		});
		this.scrollPane = new JScrollPane(this.list);
		this.buttonErzeugen = new JButton(res.getString("button_create"), haushalt.bildLaden("New16.png"));
		this.buttonUmbenennen = new JButton(res.getString("button_rename"));
		this.buttonAbbruch = new JButton(res.getString("button_close"));
		this.buttonHoch = new JButton(haushalt.bildLaden("Up16.png"));
		this.buttonRunter = new JButton(haushalt.bildLaden("Down16.png"));
		final Dimension dimensionButton = this.buttonUmbenennen.getPreferredSize();
		this.erzeugenPane.setLayout(new GridLayout(0, 2, 5, 5));
		this.erzeugenPane.setBorder(BorderFactory.createTitledBorder(res.getString("create_register")));
		this.textErzeugen = new DeleteableTextField(15) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Document createDefaultModel() {
				return new RegisterDocument();
			}
		};
		final ActionListener erzeugenActionListener = new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				erzeugen();
			}
		};
		this.textErzeugen.addActionListener(erzeugenActionListener);
		this.erzeugenPane.add(new JLabel(res.getString("new_register_name") + ":"));
		this.erzeugenPane.add(this.textErzeugen);
		this.erzeugenPane.add(new JLabel(res.getString("opening_balance") + ":"));
		this.erzeugenPane.add(this.openingBalance);
		this.erzeugenPane.add(Box.createHorizontalBox());
		this.erzeugenPane.add(this.buttonErzeugen);
		this.buttonErzeugen.addActionListener(erzeugenActionListener);
		this.buttonErzeugen.setPreferredSize(dimensionButton);
		this.umbenennenPane.setLayout(new GridLayout(0, 2, 5, 5));
		this.umbenennenPane.setBorder(BorderFactory.createTitledBorder(res.getString("rename_register")));
		this.textUmbenennen = new DeleteableTextField(15) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Document createDefaultModel() {
				return new RegisterDocument();
			}
		};
		final ActionListener umbenennenActionListener = new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				umbenennen();
			}
		};
		this.textUmbenennen.addActionListener(umbenennenActionListener);
		this.umbenennenPane.add(new JLabel(res.getString("old_register_name") + ":"));
		this.alterName.setEnabled(false);
		this.umbenennenPane.add(this.alterName);
		this.umbenennenPane.add(new JLabel(res.getString("new_register_name") + ":"));
		this.umbenennenPane.add(this.textUmbenennen);
		this.umbenennenPane.add(Box.createHorizontalBox());
		this.umbenennenPane.add(this.buttonUmbenennen);
		this.buttonUmbenennen.addActionListener(umbenennenActionListener);
		this.sortierenPane.setBorder(BorderFactory.createTitledBorder(res.getString("sort_register")));
		this.sortierenPane.setLayout(new GridLayout(0, 2, 5, 5));
		this.buttonHoch.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				nachOben();
			}
		});
		this.sortierenPane.add(this.buttonHoch);
		this.buttonRunter.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				nachUnten();
			}
		});
		this.sortierenPane.add(this.buttonRunter);
		this.eastPane.setLayout(new BoxLayout(this.eastPane, BoxLayout.Y_AXIS));
		this.eastPane.add(this.erzeugenPane);
		this.eastPane.add(this.umbenennenPane);
		this.eastPane.add(this.sortierenPane);
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

	protected void erzeugen() {
		if (this.textErzeugen.getText().equals("")) {
			JOptionPane.showMessageDialog(this.haushalt.getFrame(),
					res.getString("message_no_register_name"));
			return;
		}
		final String name = this.db.erzeugeRegister("[" + this.textErzeugen.getText() + "]");
		this.haushalt.zeigeRegisterTab(name);
		// falls der Name doppelt ist, hat er eine folgende Ziffer erhalten
		this.db.addUmbuchung(new Datum(), res.getString("opening_balance"), name, name, this.openingBalance.getValue());
		this.list.setListData(this.db.getRegisterNamen());
		this.textErzeugen.setText("");
		this.openingBalance.setText("");
	}

	protected void umbenennen() {
		if (this.textUmbenennen.getText().equals("")) {
			JOptionPane.showMessageDialog(this.haushalt.getFrame(),
					res.getString("message_no_new_register"));
			return;
		}
		if (this.list.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(this.haushalt.getFrame(),
					res.getString("message_no_register_selected"));
			return;
		}
		final String regname = this.db.renameRegister("" + this.list.getSelectedValue(),
				"[" + this.textUmbenennen.getText() + "]");
		this.haushalt.renameRegisterTab("" + this.list.getSelectedValue(), regname);
		this.list.setListData(this.db.getRegisterNamen());
		this.textUmbenennen.setText("");
	}

	protected void nachUnten() {
		if (this.list.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(this.haushalt.getFrame(),
					res.getString("message_no_register_selected"));
			return;
		}
		final int idx = this.list.getSelectedIndex();
		if (idx < this.db.getRegisterNamen().length - 1) {
			final String regname = "" + this.list.getSelectedValue();
			this.db.aendereRegisterIndex(regname, idx + 1);
			this.haushalt.bewegeRegisterNachOben(regname);
			this.list.setListData(this.db.getRegisterNamen());
			this.list.setSelectedIndex(idx + 1);
		}
	}

	protected void nachOben() {
		if (this.list.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(this.haushalt.getFrame(),
					res.getString("message_no_register_selected"));
			return;
		}
		final int idx = this.list.getSelectedIndex();
		if (idx > 0) {
			final String regname = "" + this.list.getSelectedValue();
			this.db.aendereRegisterIndex(regname, idx - 1);
			this.haushalt.bewegeRegisterNachUnten(regname);
			this.list.setListData(this.db.getRegisterNamen());
			this.list.setSelectedIndex(idx - 1);
		}
	}

	private static class RegisterDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;

		@Override
		public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
			final char[] source = str.toCharArray();
			final int anzChar = source.length;
			final char[] result = new char[anzChar];
			int j = 0;

			for (int i = 0; i < anzChar; i++) {
				if (Character.isLetterOrDigit(source[i]) || (source[i] == '-')) {
					result[j++] = source[i];
				}
			}
			super.insertString(offs, new String(result, 0, j), a);
		}
	}
}
