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
import haushalt.auswertung.DlgContainerAuswertung;
import haushalt.gui.Haushalt;
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
  private static final TextResource res = TextResource.get();

  private final JPanel southPane = new JPanel();
  protected final JTextArea textArea = new JTextArea(10, 30);
  private final JButton buttonGC = new JButton(res.getString("button_start_gc"));
  private final JButton buttonAbbruch = new JButton(res.getString("button_close"));

  public DlgInfo(JFrame frame) {
    super(frame, res.getString("program_info"), true);
    textArea.setEditable(true);
    textArea.selectAll();
    textArea.cut();
    textArea.setEditable(false);
    textArea.append(Haushalt.COPYRIGHT+"\n\n");
    textArea.append(res.getString("info_gpl_text1")+"\n");
    textArea.append(res.getString("info_gpl_text2")+"\n");
    textArea.append(res.getString("info_gpl_text3")+"\n\n");

    textArea.append(res.getString("info_version_text1")+" "+Haushalt.VERSION+"\n");
    textArea.append(res.getString("info_version_text2")+" "+Datenbasis.VERSION_DATENBASIS+"\n");
    textArea.append(res.getString("info_version_text3")+" "+DlgContainerAuswertung.VERSION_AUSWERTUNG+"\n");
    
    textArea.append(res.getString("info_internals1")+" "+
        Datenbasis.cacheHit+"/"+Datenbasis.cacheMiss+" = "+
        String.format("%1$.1f%%", 100.0D*Datenbasis.cacheHit/(Datenbasis.cacheHit+Datenbasis.cacheMiss))+"\n");
    Properties prop = System.getProperties();
    textArea.append(res.getString("info_internals2")+" "+prop.getProperty("os.name")+"\n");
    textArea.append(res.getString("info_internals3")+" "+prop.getProperty("os.arch")+"\n");
    textArea.append(res.getString("info_internals4")+" "+prop.getProperty("java.vm.version")+"\n");
    textArea.append(res.getString("info_internals5")+" "+Runtime.getRuntime().freeMemory()+"\n");
    textArea.append(res.getString("info_internals6")+" "+Runtime.getRuntime().totalMemory());
    textArea.setAlignmentX(LEFT_ALIGNMENT);

    buttonGC.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        System.gc();
        textArea.append("\n"+res.getString("info_internals5")+"  "+Runtime.getRuntime().freeMemory());
      }
    });
    buttonAbbruch.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
    getRootPane().setDefaultButton(buttonAbbruch);

		URLClassLoader urlLoader = (URLClassLoader)getClass().getClassLoader();
		URL fileLoc = urlLoader.findResource("res/jhh-image.png");
		Image image = Toolkit.getDefaultToolkit().createImage(fileLoc);
    Container contentPane = getContentPane();
    contentPane.add(new JLabel(new ImageIcon(image)), BorderLayout.NORTH);
    contentPane.add(new JScrollPane(textArea), BorderLayout.CENTER);
    contentPane.add(southPane, BorderLayout.SOUTH);
    southPane.add(buttonGC);
    southPane.add(buttonAbbruch);
    contentPane.setPreferredSize(new Dimension(350, 450));
  }
}
