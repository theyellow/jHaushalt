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

package haushalt.auswertung;

import haushalt.gui.DeleteableTextField;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.07.03
 */

/*
 * 2007.07.03 Internationalisierung
 * 2007.02.22 Weiterer Konstruktor
 * 2006.02.08 FileFilter für CSV-Dateien hinzugefügt
 * 2006.01.30 Erweiterung: Pane für Laden/Speichern extern
 * nutzbar
 * 2004.08.25 BugFix: OK / Abruch des Im-/Export-Dialogs
 * weitergegeben
 */

public class CsvHandler {

	private static final boolean DEBUG = false;
	private static final TextResource res = TextResource.get();

	private String[][] tabelle = { { "Leer" } };
	protected char datensatzTeiler = ';';

	public CsvHandler() {
		// OK
	}

	public CsvHandler(final String[][] tabelle) {
		this.tabelle = tabelle;
		if (DEBUG) {
			System.out.println("CsvHandler [" + tabelle.length + "][" + tabelle[0].length + "]");
		}
	}

	public CsvHandler(final ArrayList<String[]> tabelle) {
		this.tabelle = new String[tabelle.size()][tabelle.get(0).length];
		tabelle.toArray(this.tabelle);
	}

	public CsvHandler(final DataInputStream in) {
		try {
			read(in);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private String trimZelle(String zelle) {
		if (zelle.equals("\"\"")) {
			zelle = "";
		}
		else if (zelle.startsWith("\"") && zelle.endsWith("\"")) {
			zelle = zelle.substring(1, zelle.length() - 1);
		}
		if (DEBUG) {
			System.out.println(zelle);
		}
		return zelle;
	}

	public String[][] getTabelle() {
		final int ya = this.tabelle.length;
		final int xa = this.tabelle[0].length;
		final String[][] t = new String[ya][xa];
		for (int y = 0; y < ya; y++) {
			for (int x = 0; x < xa; x++) {
				if (this.tabelle[y][x] == null) {
					t[y][x] = "";
				}
				else {
					t[y][x] = this.tabelle[y][x];
				}
			}
		}
		return t;
	}

	protected void read(final DataInputStream in)
			throws IOException, FileNotFoundException {
		ArrayList<String> neueZeile = new ArrayList<String>();
		final ArrayList<ArrayList<String>> zeilen = new ArrayList<ArrayList<String>>();
		String zelle = "";
		int z;
		int zellenProZeile = 0;
		while ((z = in.read()) != -1) {
			final char c = (char) z;
			if ((z == 13) || (z == 10)) {
				if (neueZeile.size() > 0) {
					neueZeile.add(trimZelle(zelle));
					zelle = "";
					zeilen.add(neueZeile);
					if (neueZeile.size() > zellenProZeile) {
						zellenProZeile = neueZeile.size();
					}
					neueZeile = new ArrayList<String>();
				}
			}
			else if (c == this.datensatzTeiler) {
				if (DEBUG) {
					System.out.print(zelle + ", ");
				}
				neueZeile.add(trimZelle(zelle));
				zelle = "";
			}
			else {
				zelle += c;
			}
		}
		this.tabelle = new String[zeilen.size()][zellenProZeile];
		for (int y = 0; y < zeilen.size(); y++) {
			this.tabelle[y] = zeilen.get(y).toArray(this.tabelle[y]);
		}
	}

	protected void write(final DataOutputStream out)
			throws IOException {
		for (int y = 0; y < this.tabelle.length; y++) {
			for (int x = 0; x < this.tabelle[y].length; x++) {
				if (x > 0) {
					out.writeByte(this.datensatzTeiler);
				}
				out.writeBytes("\"" + this.tabelle[y][x] + "\"");
				if (DEBUG) {
					System.out.print(" " + this.tabelle[y][x]);
				}
			}
			if (DEBUG) {
				System.out.println("");
			}
			out.writeByte(13);
			out.writeByte(10);
		}
	}

	public void setDatensatzTeiler(final char datensatzTeiler) {
		this.datensatzTeiler = datensatzTeiler;
	}

	public boolean importDlg(final JFrame frame, final String path) {
		final CsvDateiDialog dlg = new CsvDateiDialog(frame, path, true);
		dlg.pack();
		dlg.setVisible(true);
		return dlg.ok;
	}

	public boolean exportDlg(final JFrame frame, final String path) {
		final CsvDateiDialog dlg = new CsvDateiDialog(frame, path, false);
		dlg.pack();
		dlg.setVisible(true);
		return dlg.ok;
	}

	public class CsvPane extends JPanel {

		private static final long serialVersionUID = 1L;

		protected final DeleteableTextField dateiname = new DeleteableTextField(20);
		protected final DeleteableTextField trennzeichen = new DeleteableTextField(
				"" + CsvHandler.this.datensatzTeiler, 2);
		private final JButton buttonAuswahl = new JButton(res.getString("button_selection"));
		private final FileFilter fileFilter = new FileFilter() {

			@Override
			public boolean accept(final File file) {
				if (file.isDirectory()) {
					return true;
				}
				if (file.getName().toLowerCase().endsWith(".csv")) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return res.getString("csv_files") + " (*.csv)";
			}
		};

		public CsvPane(final JFrame frame, final String path, final boolean laden) {
			setLayout(new GridLayout(0, 2));
			add(new JLabel(res.getString("filename") + ":"));
			add(this.dateiname);
			add(Box.createGlue());
			add(this.buttonAuswahl);
			add(new JLabel(res.getString("separation_char") + ":"));
			add(this.trennzeichen);

			this.buttonAuswahl.addActionListener(new ActionListener() {

				public void actionPerformed(final ActionEvent e) {
					final JFileChooser dateidialog = new JFileChooser(path);
					dateidialog.setFileFilter(CsvPane.this.fileFilter);
					if (laden) {
						if (dateidialog.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
							CsvPane.this.dateiname.setText(dateidialog.getSelectedFile().getAbsolutePath());
						}
					}
					else {
						if (dateidialog.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
							String text = dateidialog.getSelectedFile().getAbsolutePath();
							if (!text.toLowerCase().endsWith(".csv")) {
								text += ".csv";
							}
							CsvPane.this.dateiname.setText(text);
						}
					}
				}
			});
		}

		public boolean laden() {
			boolean ok = true;
			CsvHandler.this.datensatzTeiler = this.trennzeichen.getText().toCharArray()[0];
			try {
				final FileInputStream fis = new FileInputStream(this.dateiname.getText());
				final DataInputStream in = new DataInputStream(fis);
				read(in);
				fis.close();
			}
			catch (final IOException ex) {
				ex.printStackTrace();
				ok = false;
			}
			return ok;
		}

		public boolean speichern() {
			boolean ok = true;
			CsvHandler.this.datensatzTeiler = this.trennzeichen.getText().toCharArray()[0];
			try {
				final FileOutputStream fos = new FileOutputStream(this.dateiname.getText());
				final DataOutputStream out = new DataOutputStream(fos);
				write(out);
				out.flush();
				fos.close();
			}
			catch (final IOException ex) {
				ex.printStackTrace();
				ok = false;
			}
			return ok;
		}

	}

	private class CsvDateiDialog extends JDialog {

		private static final long serialVersionUID = 1L;
		private final CsvPane hauptPane;
		private final JPanel buttonPane = new JPanel();
		private final JButton buttonOK;
		private final JButton buttonAbbruch = new JButton(res.getString("button_cancel"));
		protected boolean ok = true;

		private CsvDateiDialog(final JFrame frame, final String path, final boolean laden) {
			super(frame, true);
			if (laden) {
				setTitle(res.getString("csv_file_load"));
				this.buttonOK = new JButton(res.getString("button_load"));
			}
			else {
				setTitle(res.getString("csv_file_save"));
				this.buttonOK = new JButton(res.getString("button_save"));
			}
			this.hauptPane = new CsvPane(frame, path, laden);
			final Container contentPane = getContentPane();
			contentPane.add(this.hauptPane, BorderLayout.CENTER);
			contentPane.add(this.buttonPane, BorderLayout.SOUTH);
			this.buttonPane.add(this.buttonOK);
			this.buttonPane.add(this.buttonAbbruch);

			this.buttonOK.addActionListener(new ActionListener() {

				public void actionPerformed(final ActionEvent e) {
					if (laden) {
						CsvDateiDialog.this.ok = CsvDateiDialog.this.hauptPane.laden();
					}
					else {
						CsvDateiDialog.this.ok = CsvDateiDialog.this.hauptPane.speichern();
					}
					setVisible(false);
				}
			});
			this.buttonAbbruch.addActionListener(new ActionListener() {

				public void actionPerformed(final ActionEvent e) {
					CsvDateiDialog.this.ok = false;
					setVisible(false);
				}
			});
			getRootPane().setDefaultButton(this.buttonAbbruch);
		}

	}

	public static void main(final String[] args)
			throws Exception {
		final FileInputStream fis = new FileInputStream("test-out.csv");
		final DataInputStream in = new DataInputStream(fis);
		final CsvHandler handler = new CsvHandler(in);
		fis.close();

		final FileOutputStream fos = new FileOutputStream("test-out2.csv");
		final DataOutputStream out = new DataOutputStream(fos);
		handler.write(out);
		out.flush();
		fos.close();
	}
}
