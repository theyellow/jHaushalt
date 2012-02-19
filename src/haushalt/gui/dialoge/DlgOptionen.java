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

import haushalt.auswertung.FarbPaletten;
import haushalt.gui.DatumField;
import haushalt.gui.DeleteableTextField;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Locale;
import java.util.Properties;

/**
 * Dialog zum Ändern der Optionen.
 * @author Dr. Lars H. Hahn
 * @version 2.5.4/2008.05.14
 */

 /*
  * 2008.05.14 Reiter für benutzer-definierte Farben
  * 2008.04.15 Farbe für zukünftige Buchungen
  * 2008.01.22 Umstellung der Auswahl des Delkeys auf Index
  * 2007.05.30 Internationalisierung
  * 2006.01.27 Entfernen der globalen Option 
  *            "Unterkategorien verwenden"
  * 2005.03.10 Erweiterung: Gemerkte Buchungen ab Datum
  */

public class DlgOptionen extends JDialog {
  private static final boolean DEBUG = false;
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();
  
  private final Locale liste_locales[] = Locale.getAvailableLocales();

  // GUI-Komponenten
  private final JTabbedPane tabbedPane = new JTabbedPane();
  private final JPanel allgemeinPane = new JPanel();
  private final JComboBox sprache;
  private final DeleteableTextField ordner = new DeleteableTextField();
  private final DeleteableTextField waehrung = new DeleteableTextField();
  private final JCheckBox euroImport = new JCheckBox();
  
  private final JPanel registerPane = new JPanel();
	private final String[] reiterAuswahl = {"BOTTOM", "TOP", "LEFT", "RIGHT"};
  private final JComboBox reiter = new JComboBox(reiterAuswahl);
  private final JCheckBox gemerkte = new JCheckBox();
  private final DatumField startDatum = new DatumField();
  private final String[] deltasteAuswahl = {
      res.getString("delkey_selection0"),
      res.getString("delkey_selection1"),
      res.getString("delkey_selection2"),
      res.getString("delkey_selection3")
  };
  private final JComboBox deltaste = new JComboBox(deltasteAuswahl);
  protected final JButton farbeSelektion = new JButton();
  protected final JButton farbeGitter = new JButton();
  protected final JButton farbeZukunft = new JButton();
  
  private final JPanel auswertungPane = new JPanel();
  private final String[] fontAuswahl = {"SansSerif", "Serif", "Monospaced"};
  private final JComboBox font = new JComboBox(fontAuswahl);
  private final DeleteableTextField punkt = new DeleteableTextField();
  
  private final JPanel customPane = new JPanel();
  private final JButton buttonAdd;
  private final JButton buttonDelete;
  private final JButton buttonEdit;
  private final DefaultListModel listModel = new DefaultListModel();
  private final JList customColor = new JList(listModel);
  
  private final JPanel buttonPane = new JPanel();
  private final JButton buttonOK = new JButton(res.getString("button_ok"));
  private final JButton buttonAbbruch = new JButton(res.getString("button_cancel"));

  // Daten
	private final Properties properties;

  public DlgOptionen(final Haushalt haushalt, Properties properties) {
    super(haushalt.getFrame(), res.getString("options"), true); // = modal
    String[] sprachen = new String[liste_locales.length];
    for (int i = 0; i < liste_locales.length; i++)
        sprachen[i] = liste_locales[i].getDisplayName();
    sprache = new JComboBox(sprachen);
  	this.properties = properties;
    allgemeinPane.setLayout(new GridLayout(0,2));
    allgemeinPane.add(new JLabel(res.getString("language_hint")+":"));
    allgemeinPane.add(sprache);
    allgemeinPane.add(new JLabel(res.getString("working_directory")+":"));
    allgemeinPane.add(ordner);
    allgemeinPane.add(new JLabel(res.getString("currency_symbol")+":"));
    allgemeinPane.add(waehrung);
    allgemeinPane.add(new JLabel(res.getString("import_currency")+":"));
    allgemeinPane.add(euroImport);
    allgemeinPane.add(new JLabel(res.getString("start_date_remembered_bookings")+":"));
    allgemeinPane.add(startDatum);
    allgemeinPane.add(new JLabel(""));
    tabbedPane.add(res.getString("general"), allgemeinPane);
    
    registerPane.setLayout(new GridLayout(0,2));
    registerPane.add(new JLabel(res.getString("tab_placement")+":"));
    registerPane.add(reiter);
    registerPane.add(new JLabel(res.getString("use_remembered_bookings")+":"));
    registerPane.add(gemerkte);
    registerPane.add(new JLabel(res.getString("key_clear_cell")+":"));
    registerPane.add(deltaste);
    registerPane.add(new JLabel(res.getString("background_color_selection")+":"));
    registerPane.add(farbeSelektion);
    registerPane.add(new JLabel(res.getString("grid_color")+":"));
    registerPane.add(farbeGitter);
    registerPane.add(new JLabel(res.getString("future_color")+":"));
    registerPane.add(farbeZukunft);
    tabbedPane.add(res.getString("register"), registerPane);

    auswertungPane.setLayout(new GridLayout(0,2));
    auswertungPane.add(new JLabel(res.getString("font")+":"));
    auswertungPane.add(font);
    auswertungPane.add(new JLabel(res.getString("font_size")+":"));
    auswertungPane.add(punkt);
    auswertungPane.add(new JLabel(""));
    auswertungPane.add(new JLabel(""));
    auswertungPane.add(new JLabel(""));
    auswertungPane.add(new JLabel(""));
    auswertungPane.add(new JLabel(""));
    tabbedPane.add(res.getString("report"), auswertungPane);
    
    customColor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    customColor.setCellRenderer(new ColorRenderer(true));
    customColor.setVisibleRowCount(6);
    MouseListener mouseListener = new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
          if((e.getClickCount() == 2) && !customColor.isSelectionEmpty()) {
              int idx = customColor.locationToIndex(e.getPoint());
              Color alteFarbe = (Color) customColor.getSelectedValue();
              Color neueFarbe = JColorChooser.showDialog(haushalt.getFrame(), res.getString("custom_color"), alteFarbe);
              if(neueFarbe != null) {
                listModel.removeElementAt(idx);
                listModel.insertElementAt(neueFarbe, idx);
           }
        }
      }
    };
    customColor.addMouseListener(mouseListener);
    JScrollPane listScrollPane = new JScrollPane(customColor);
    customPane.add(listScrollPane);
    buttonAdd = new JButton(res.getString("button_add"), haushalt.bildLaden("Add16.gif"));
    customPane.add(buttonAdd);
    buttonDelete = new JButton(res.getString("button_delete"), haushalt.bildLaden("Delete16.gif"));
    customPane.add(buttonDelete);
    buttonEdit = new JButton(res.getString("button_edit"), haushalt.bildLaden("Edit16.gif"));
    customPane.add(buttonEdit);
    tabbedPane.add(res.getString("custom_color"), customPane);


    farbeSelektion.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Color farbe = JColorChooser.showDialog(haushalt.getFrame(), res.getString("background_color_selection"), farbeSelektion.getBackground());
        if(farbe != null) {
      		farbeSelektion.setText("#"+Integer.toHexString(farbe.getRGB()).toUpperCase());
          farbeSelektion.setBackground(farbe);
        }
      }
    });
    farbeGitter.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Color farbe = JColorChooser.showDialog(haushalt.getFrame(), res.getString("grid_color"), farbeGitter.getBackground());
        if(farbe != null) {
          farbeGitter.setText("#"+Integer.toHexString(farbe.getRGB()).toUpperCase());
          farbeGitter.setBackground(farbe);
        }
      }
    });
    farbeZukunft.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Color farbe = JColorChooser.showDialog(haushalt.getFrame(), res.getString("future_color"), farbeZukunft.getBackground());
        if(farbe != null) {
          farbeZukunft.setText("#"+Integer.toHexString(farbe.getRGB()).toUpperCase());
          farbeZukunft.setBackground(farbe);
        }
      }
    });
    buttonAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Color neueFarbe = JColorChooser.showDialog(haushalt.getFrame(), res.getString("custom_color"), Color.WHITE);
        if(neueFarbe != null) {
          listModel.addElement(neueFarbe);
        }
      }
    });
    buttonDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(!customColor.isSelectionEmpty()) {
          int idx = customColor.getSelectedIndex();
          listModel.removeElementAt(idx);
        }
      }
    });
    buttonEdit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(!customColor.isSelectionEmpty()) {
          int idx = customColor.getSelectedIndex();
          Color alteFarbe = (Color) customColor.getSelectedValue();
          Color neueFarbe = JColorChooser.showDialog(haushalt.getFrame(), res.getString("custom_color"), alteFarbe);
          if(neueFarbe != null) {
            listModel.removeElementAt(idx);
            listModel.insertElementAt(neueFarbe, idx);
          }
        }
      }
    });
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exit();
        setVisible(false);
      }
    });
    buttonAbbruch.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
    buttonPane.add(buttonOK);
    buttonPane.add(buttonAbbruch);
    Container contentPane = getContentPane();
    contentPane.add(tabbedPane, BorderLayout.CENTER);
    contentPane.add(buttonPane, BorderLayout.SOUTH);
    getRootPane().setDefaultButton(buttonOK);
  }

  /**
   * Zeigt den Dialog auf dem Bildschirm an, wenn er noch nicht sichtbar ist.
   */
  public void showDialog() {
    init();
    setLocationRelativeTo(getOwner());
    pack();
    setVisible(true);
  }

  private void init() {
    String locale_name = res.getLocale().getDisplayName();
    for (int i = 0; i < sprache.getItemCount(); i++)
      if(locale_name.equals(sprache.getItemAt(i)))
        sprache.setSelectedIndex(i);
    ordner.setText(properties.getProperty("jhh.ordner"));
    font.setSelectedItem(properties.getProperty("jhh.opt.font", "SansSerif"));
    punkt.setText(properties.getProperty("jhh.opt.punkt", "12"));
		gemerkte.setSelected(Boolean.valueOf(properties.getProperty("jhh.opt.gemerkte", "true")).booleanValue());
    startDatum.setText(properties.getProperty("jhh.opt.startdatum", "01.01.00"));
		waehrung.setText(properties.getProperty("jhh.opt.waehrung", "€"));
    int idx = Integer.parseInt(properties.getProperty("jhh.opt.deltaste", "0"));
    deltaste.setSelectedIndex(idx);
		reiter.setSelectedItem(properties.getProperty("jhh.opt.reiter", "BOTTOM"));
		euroImport.setSelected(Boolean.valueOf(properties.getProperty("jhh.opt.euroimport", "true")).booleanValue());
		int farbe = new Integer(properties.getProperty("jhh.opt.selektion", "12632256")).intValue(); // #c0c0c0 
		farbeSelektion.setText(Integer.toHexString(farbe).toUpperCase());
		farbeSelektion.setBackground(new Color(farbe));
    farbe = new Integer(properties.getProperty("jhh.opt.gitter", "10066329")).intValue(); // #999999
    farbeGitter.setText(Integer.toHexString(farbe).toUpperCase());
    farbeGitter.setBackground(new Color(farbe));
    farbe = new Integer(properties.getProperty("jhh.opt.zukunft", "16777088")).intValue(); // #ffff80
    farbeZukunft.setText(Integer.toHexString(farbe).toUpperCase());
    farbeZukunft.setBackground(new Color(farbe));
    int anz = FarbPaletten.setCustomColor(properties.getProperty("jhh.opt.custom","16776960"));
    listModel.removeAllElements();
    for(int i=0;i<anz;i++) {
      listModel.addElement(FarbPaletten.getFarbe(i, "Custom"));
    }
    if(DEBUG) {
      System.out.println("Anzahl Custom Colors: "+anz);
    }
  }
  
  protected void exit() {
    properties.setProperty("jhh.opt.sprache", ""+liste_locales[sprache.getSelectedIndex()]);
		properties.setProperty("jhh.ordner", ordner.getText());
		properties.setProperty("jhh.opt.font", ""+font.getSelectedItem());
		properties.setProperty("jhh.opt.punkt", punkt.getText());
    properties.setProperty("jhh.opt.gemerkte", ""+gemerkte.isSelected());
    properties.setProperty("jhh.opt.startdatum", ""+startDatum.getText());
		properties.setProperty("jhh.opt.waehrung", waehrung.getText());
		properties.setProperty("jhh.opt.deltaste", ""+deltaste.getSelectedIndex());
		properties.setProperty("jhh.opt.reiter", ""+reiter.getSelectedItem());
		properties.setProperty("jhh.opt.euroimport", ""+euroImport.isSelected());
		properties.setProperty("jhh.opt.selektion", ""+farbeSelektion.getBackground().getRGB());
    properties.setProperty("jhh.opt.gitter", ""+farbeGitter.getBackground().getRGB());
    properties.setProperty("jhh.opt.zukunft", ""+farbeZukunft.getBackground().getRGB());
    Color[] farben = new Color[listModel.getSize()];
    for(int i=0; i<listModel.getSize(); i++) {
      farben[i] = (Color) listModel.getElementAt(i);
    }
    FarbPaletten.setCustomColor(farben);
    properties.setProperty("jhh.opt.custom", ""+FarbPaletten.getCustomColor());
		if(DEBUG) {
		  System.out.println(res.getString("option_set"));
		  properties.list(System.out);
		}
  }

}