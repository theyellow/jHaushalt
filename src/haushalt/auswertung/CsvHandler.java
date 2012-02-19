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
  *            nutzbar
  * 2004.08.25 BugFix: OK / Abruch des Im-/Export-Dialogs 
  *            weitergegeben
  */

public class CsvHandler {
  private static final boolean DEBUG = false;
  private static final TextResource res = TextResource.get();

  private String[][] tabelle = {{"Leer"}};
  protected char datensatzTeiler = ';';

  public CsvHandler() {
    // OK
  }
  
  public CsvHandler(String[][] tabelle) {
    this.tabelle = tabelle;
    if(DEBUG)
      System.out.println("CsvHandler ["+tabelle.length+"]["+tabelle[0].length+"]");
  }
  
  public CsvHandler(ArrayList<String[]> tabelle) {
    this.tabelle = new String[tabelle.size()][tabelle.get(0).length];
    tabelle.toArray(this.tabelle);
  }

  public CsvHandler(DataInputStream in) {
    try {
      read(in);
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }

  private String trimZelle(String zelle) {
    if(zelle.equals("\"\""))
      zelle = "";
    else if(zelle.startsWith("\"") && zelle.endsWith("\""))
      zelle = zelle.substring(1, zelle.length()-1);
    if(DEBUG)
      System.out.println(zelle);
    return zelle;
  }

  public String[][] getTabelle() {
    int ya = tabelle.length;
    int xa = tabelle[0].length;
    String[][] t = new String[ya][xa];
    for(int y=0; y<ya; y++)
      for(int x=0; x<xa; x++)
        if(tabelle[y][x] == null)
          t[y][x] = "";
        else
          t[y][x] = tabelle[y][x];
    return t;
  }

  protected void read(DataInputStream in)
    throws IOException, FileNotFoundException {
    ArrayList<String> neueZeile = new ArrayList<String>();
    ArrayList<ArrayList<String>> zeilen = new ArrayList<ArrayList<String>>();
    String zelle = "";
    int z;
    int zellenProZeile = 0;
    while((z = in.read()) != -1) {
      char c = (char)z;
      if((z == 13) || (z == 10)) {
        if(neueZeile.size() > 0) {
          neueZeile.add(trimZelle(zelle));
          zelle = "";
          zeilen.add(neueZeile);
          if(neueZeile.size() > zellenProZeile)
            zellenProZeile = neueZeile.size();
          neueZeile = new ArrayList<String>();
        }
      }
      else if(c == datensatzTeiler) {
        if(DEBUG)
          System.out.print(zelle+", ");
        neueZeile.add(trimZelle(zelle));
        zelle = "";
      }
      else
        zelle += c;
    }
    tabelle = new String[zeilen.size()][zellenProZeile];
    for(int y=0; y<zeilen.size(); y++) {
      tabelle[y] = zeilen.get(y).toArray(tabelle[y]);
    }
  }

  protected void write(DataOutputStream out)
    throws IOException {
    for(int y=0; y<tabelle.length; y++) {
      for(int x=0; x<tabelle[y].length; x++) {
        if(x>0)
          out.writeByte(datensatzTeiler);
        out.writeBytes("\""+tabelle[y][x]+"\"");
        if(DEBUG)
          System.out.print(" "+tabelle[y][x]);
      }
      if(DEBUG)
        System.out.println("");
      out.writeByte(13);
      out.writeByte(10);
    }
  }

  public void setDatensatzTeiler(char datensatzTeiler) {
    this.datensatzTeiler = datensatzTeiler;
  }

  public boolean importDlg(JFrame frame, String path) {
    CsvDateiDialog dlg = new CsvDateiDialog(frame, path, true);
    dlg.pack();
    dlg.setVisible(true);
    return dlg.ok;
  }

  public boolean exportDlg(JFrame frame, String path) {
    CsvDateiDialog dlg = new CsvDateiDialog(frame, path, false);
    dlg.pack();
    dlg.setVisible(true);
    return dlg.ok;
  }
  
  public class CsvPane extends JPanel {
    private static final long serialVersionUID = 1L;
    
    protected final DeleteableTextField dateiname = new DeleteableTextField(20);
    protected final DeleteableTextField trennzeichen = new DeleteableTextField(""+datensatzTeiler, 2);
    private final JButton buttonAuswahl = new JButton(res.getString("button_selection"));
    private final FileFilter fileFilter = new FileFilter() {
      public boolean accept(File file) {
        if (file.isDirectory())
          return true;
        if(file.getName().toLowerCase().endsWith(".csv"))
          return true;
        return false;
      }
      public String getDescription() {
        return res.getString("csv_files")+" (*.csv)";
      }
    };
    

    public CsvPane(final JFrame frame, final String path, final boolean laden) {
      setLayout(new GridLayout(0,2));
      add(new JLabel(res.getString("filename")+":"));
      add(dateiname);
      add(Box.createGlue());
      add(buttonAuswahl);
      add(new JLabel(res.getString("separation_char")+":"));
      add(trennzeichen);
      
      buttonAuswahl.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          JFileChooser dateidialog  = new JFileChooser(path);
          dateidialog.setFileFilter(fileFilter);
          if(laden) {
            if(dateidialog.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
              dateiname.setText(dateidialog.getSelectedFile().getAbsolutePath());
            }
          }
          else {
            if(dateidialog.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
              String text = dateidialog.getSelectedFile().getAbsolutePath();
              if(!text.toLowerCase().endsWith(".csv"))
                text += ".csv";
              dateiname.setText(text);
            }
          }
        }
      });
    }
    
    public boolean laden() {
      boolean ok = true;
      datensatzTeiler = trennzeichen.getText().toCharArray()[0];
      try {
        FileInputStream fis = new FileInputStream(dateiname.getText());
        DataInputStream in = new DataInputStream(fis);
        read(in);
        fis.close();
      }
      catch(IOException ex) {
        ex.printStackTrace();
        ok = false;
      }
      return ok;
    }
    
    public boolean speichern() {
      boolean ok = true;
      datensatzTeiler = trennzeichen.getText().toCharArray()[0];
      try {
        FileOutputStream fos = new FileOutputStream(dateiname.getText());
        DataOutputStream out = new DataOutputStream(fos);
        write(out);
        out.flush();
        fos.close();
      }
      catch(IOException ex) {
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
      if(laden) {
        setTitle(res.getString("csv_file_load"));
        buttonOK = new JButton(res.getString("button_load"));
      }
      else {
        setTitle(res.getString("csv_file_save"));
        buttonOK = new JButton(res.getString("button_save"));
      }
      hauptPane = new CsvPane(frame, path, laden);
      Container contentPane = getContentPane();
      contentPane.add(hauptPane, BorderLayout.CENTER);
      contentPane.add(buttonPane, BorderLayout.SOUTH);
      buttonPane.add(buttonOK);
      buttonPane.add(buttonAbbruch);

      buttonOK.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if(laden)
            ok = hauptPane.laden();
          else
            ok = hauptPane.speichern();
          setVisible(false);
        }
      });
      buttonAbbruch.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ok = false;
          setVisible(false);
        }
      });
      getRootPane().setDefaultButton(buttonAbbruch);
    }

  }

  public static void main(String[] args)
   throws Exception {
    FileInputStream fis = new FileInputStream("test-out.csv");
    DataInputStream in = new DataInputStream(fis);
    CsvHandler handler = new CsvHandler(in);
    fis.close();

    FileOutputStream fos = new FileOutputStream("test-out2.csv");
    DataOutputStream out = new DataOutputStream(fos);
    handler.write(out);
    out.flush();
    fos.close();
  }
}
