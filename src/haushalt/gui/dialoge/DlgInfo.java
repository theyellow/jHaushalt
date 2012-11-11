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

import haushalt.auswertung.DlgContainerAuswertung;
import haushalt.auswertung.domain.HaushaltProperties;
import haushalt.daten.Datenbasis;
import haushalt.gui.Haushalt;
import haushalt.gui.MainWindow;
import haushalt.gui.TextResource;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Dialog mit dem die Programm-Information angezeigt werden.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5.4/2008.04.15
 */

/*
 * 2008.04.15 BugFix: Formatierung Hit/Miss-Rate
 * 2007.04.04 Internationalisierung
 * 2006.01.26 Ausgabe der verschienden Versionstände
 * 2006.01.27 Cache-Statistik hinzugefügt
 */

public class DlgInfo extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final TextResource RES = TextResource.get();

	private final JPanel southPane = new JPanel();
	private final JTextArea textArea = new JTextArea(10, 30);
	private final JButton buttonGC = new JButton(RES.getString("button_start_gc"));
	private final JButton buttonAbbruch = new JButton(RES.getString("button_close"));

	public DlgInfo(final JFrame frame) {
		super(frame, RES.getString("program_info"), true);
		this.textArea.setEditable(true);
		this.textArea.selectAll();
		this.textArea.cut();
		this.textArea.setEditable(false);
		this.textArea.append(HaushaltProperties.COPYRIGHT + "\n\n");
		this.textArea.append(RES.getString("info_gpl_text1") + "\n");
		this.textArea.append(RES.getString("info_gpl_text2") + "\n");
		this.textArea.append(RES.getString("info_gpl_text3") + "\n\n");
		this.textArea.append(RES.getString("info_icons") + "\n\n");

		this.textArea.append(RES.getString("info_version_text1") + " " + HaushaltProperties.VERSION + "\n");
		this.textArea.append(RES.getString("info_version_text2") + " " + Datenbasis.VERSION_DATENBASIS + "\n");
		this.textArea.append(RES.getString("info_version_text3") + " " + DlgContainerAuswertung.VERSION_AUSWERTUNG + "\n");

		this.textArea.append(RES.getString("info_internals1")
			+ " "
			+ Datenbasis.getCacheHit()
			+ "/"
			+ Datenbasis.getCacheMiss()
			+ " = "
			+ String.format("%1$.1f%%", 100.0D * Datenbasis.getCacheHit() / (Datenbasis.getCacheHit() + Datenbasis.getCacheMiss()))
			+ "\n");
		final Properties prop = System.getProperties();
		this.textArea.append(RES.getString("info_internals2") + " " + prop.getProperty("os.name") + "\n");
		this.textArea.append(RES.getString("info_internals3") + " " + prop.getProperty("os.arch") + "\n");
		this.textArea.append(RES.getString("info_internals4") + " " + prop.getProperty("java.vm.version") + "\n");
		this.textArea.append(RES.getString("info_internals5") + " " + Runtime.getRuntime().freeMemory() + "\n");
		this.textArea.append(RES.getString("info_internals6") + " " + Runtime.getRuntime().totalMemory());
		this.textArea.setAlignmentX(LEFT_ALIGNMENT);

		this.buttonGC.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				System.gc();
				DlgInfo.this.textArea.append("\n" + RES.getString("info_internals5") + "  " + Runtime.getRuntime().freeMemory());
			}
		});
		this.buttonAbbruch.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});
		getRootPane().setDefaultButton(this.buttonAbbruch);

		final URLClassLoader urlLoader = (URLClassLoader) getClass().getClassLoader();
		final URL fileLoc = urlLoader.findResource("RES/jhh-image.png");
		final Image image = Toolkit.getDefaultToolkit().createImage(fileLoc);
		final Container contentPane = getContentPane();
		contentPane.add(new JLabel(new ImageIcon(image)), BorderLayout.NORTH);
		contentPane.add(new JScrollPane(this.textArea), BorderLayout.CENTER);
		contentPane.add(this.southPane, BorderLayout.SOUTH);
		this.southPane.add(this.buttonGC);
		this.southPane.add(this.buttonAbbruch);
		contentPane.setPreferredSize(new Dimension(350, 450));
	}
}
