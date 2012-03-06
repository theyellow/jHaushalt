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

package haushalt.gui;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2006.07.04
 */

/*
 * 2006.07.04 Internationalisierung
 * 2006.01.31 Erweiterung: Kontextmenü hinzugefügt
 */

public class ActionHandler {
	private static final boolean DEBUG = false;
  private static final TextResource res = TextResource.get();

  final private String[] hauptmenu = {
      res.getString("file"),
      res.getString("edit"),
      res.getString("output"),
      res.getString("extras"),
      res.getString("help")
  };
  final private Object[] alleActions;
  // 0:
  final private Object[][] menuDateiText = {
		{"neu", res.getString("new"), "New", res.getString("new_legend"), new Integer(KeyEvent.VK_N)},
		{"laden", res.getString("open")+"...", "Open", res.getString("open_legend"), new Integer(KeyEvent.VK_L)}, 
		{"speichern", res.getString("save"), "Save", res.getString("save_legend"), new Integer(KeyEvent.VK_S)},
		{"speichernUnter", res.getString("save_as")+"...", "SaveAs", res.getString("save_as_legend"), null},
		{"beenden", res.getString("exit"), null, res.getString("exit_legend"), new Integer(KeyEvent.VK_X)}
	};
  // 1:
	final private Object[][] menuBearbeitenText = {
			{"neueBuchungErstellen", res.getString("new_booking")+"...", "AddBuchung", res.getString("new_booking_legend"), new Integer(KeyEvent.VK_C)},
			{"loeschen", res.getString("delete"), "Delete", res.getString("delete_legend"), new Integer(KeyEvent.VK_D)},
		{"umbuchen", res.getString("rebook")+"...", "Umbuchung", res.getString("rebook_legend"), new Integer(KeyEvent.VK_U)},
    {"splitten", res.getString("split")+"...", "Splitten", res.getString("split_legend"), new Integer(KeyEvent.VK_P)},
    {"umwandeln", res.getString("convert")+"...", "Umwandeln", res.getString("convert_legend"), new Integer(KeyEvent.VK_W)},
		{"registerBearbeiten", res.getString("edit_registers")+"...", "Register", res.getString("edit_registers_legend"), new Integer(KeyEvent.VK_R)},
		{"kategorienBearbeiten", res.getString("edit_category")+"...", "Auto", res.getString("edit_category_legend"), new Integer(KeyEvent.VK_K)},
		{"suchen", res.getString("find")+"...", "Find", res.getString("find_legend"), null},
		{"alteBuchungenLoeschen", res.getString("delete_old_bookings")+"...", null, res.getString("delete_old_bookings_legend"), new Integer(KeyEvent.VK_E)},
		{"kategorieErsetzen", res.getString("replace_category")+"...", null, res.getString("replace_category_legend"), null},
    {"kategorienBereinigen", res.getString("clean_categories")+"...", null, res.getString("clean_categories_legend"), new Integer(KeyEvent.VK_B)},
    {"registerVereinigen", res.getString("join_register")+"...", null, res.getString("join_register_legend"), new Integer(KeyEvent.VK_V)}
	};
  // 2:
	final private Object[][] menuAusgabeText = {
    {"zeigeAuswertung", res.getString("show_report")+"...", "Auswertung", res.getString("show_report_legend"), new Integer(KeyEvent.VK_A)},
    {"exportCSV", res.getString("export_csv")+"...", "Export", res.getString("export_csv_legend"), null},
    {"drucken", res.getString("print")+"...", "Print", res.getString("print_legend"), new Integer(KeyEvent.VK_P)}
	};

	// 3: Extras
	final private Object[] preferences = { "optionen",
			res.getString("preferences") + "...", "Preferences",
			res.getString("preferences_legend"), new Integer(KeyEvent.VK_O) };
	final private Object[] autoBuchungen = { "autoBuchung",
			res.getString("automatic_booking") + "...", "Robot",
			res.getString("automatic_booking_legend"), null };
	final private Object[] importCSV = { "importCSV",
			res.getString("import_csv") + "...", "Import",
			res.getString("import_csv_legend"), new Integer(KeyEvent.VK_I) };
	final private Object[] importQuicken = { "importQuicken",
			res.getString("import_quicken") + "...", null,
			res.getString("import_quicken_legend"), new Integer(KeyEvent.VK_Q) };
	final private Object[][] menuExtrasText;

	// 4: Hilfe
	final private Object[] hilfe = { "hilfeInhalt",
			res.getString("help_content") + "...", "Help",
			res.getString("help_content_legend"), new Integer(KeyEvent.VK_F1) };
	final private Object[] programmInfo = { "programmInfo",
			res.getString("program_info") + "...", "Information",
			res.getString("program_info_legend"), null };
	final private Object[][] menuHilfeText;

  protected final Haushalt haushalt;
  private final JPopupMenu popupMenu = new JPopupMenu();
  
  public ActionHandler(Haushalt haushalt) {
    super();
    this.haushalt = haushalt;
		if (!Haushalt.isMacOSX()) {
			menuHilfeText = new Object[][] { hilfe, programmInfo };
			menuExtrasText = new Object[][] { preferences, autoBuchungen,
					importCSV, importQuicken };
		} else {
			menuHilfeText = new Object[][] { hilfe };
			menuExtrasText = new Object[][] { autoBuchungen, importCSV,
					importQuicken };
		}
    alleActions = new Object[hauptmenu.length];
		alleActions[0] = erzeugeAction(menuDateiText);
		alleActions[1] = erzeugeAction(menuBearbeitenText);
		alleActions[2] = erzeugeAction(menuAusgabeText);
		alleActions[3] = erzeugeAction(menuExtrasText);
		alleActions[4] = erzeugeAction(menuHilfeText);
    
    // Das PopupMenü wird mit Bearbeiten-Menü belegt.
    HaushaltAction[] action = (HaushaltAction[]) alleActions[1];
    for (int i = 0; i < action.length; i++)
      popupMenu.add(new JMenuItem(action[i]));
  }
  
	private HaushaltAction[] erzeugeAction(Object[][] text) {
		int anzahl = text.length;
		HaushaltAction[] action = new HaushaltAction[anzahl];
		for(int i=0; i<anzahl; i++) {
			action[i] = new HaushaltAction(text[i]);
		}
		if(DEBUG)
		  System.out.println("ActionHandler: Action-Liste erzeugt.");
		return action;
	}
	
	public JMenuBar erzeugeMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		for(int i=0; i<hauptmenu.length; i++) {
			JMenu menu = new JMenu(hauptmenu[i]);
			Action[] action = (Action[]) alleActions[i];
			for(int j=0; j<action.length; j++) {
        JMenuItem menuItem = new JMenuItem(action[j]);
        menu.add(menuItem);
      }
			menuBar.add(menu);      
    }
	  return menuBar;
	}

	public JToolBar erzeugeToolBar() {
		JToolBar toolBar = new JToolBar();
		for(int i=0; i<hauptmenu.length; i++) {
			HaushaltAction[] action = (HaushaltAction[]) alleActions[i];
			for(int j=0; j<action.length; j++) {
				ImageIcon bigIcon = action[j].getBigIcon();
				if(bigIcon != null) {
					JButton button = new JButton(action[j]);
					button.setText("");
					button.setIcon(bigIcon);
					toolBar.add(button);
				}
			}
		}
		 return toolBar;
	}

  public JPopupMenu getPopupMenu() {
    return popupMenu;
  }
  
	protected ImageIcon createBigIcon(Object iconname) {
		if(iconname == null)
			return null;
		URLClassLoader urlLoader = (URLClassLoader)getClass().getClassLoader();
		URL imageURL = urlLoader.findResource("res/" + iconname + "24.png");
		if(DEBUG)
			System.out.println("ActionHandler: Erzeuge Image "+iconname+"@"+imageURL);
		return new ImageIcon(imageURL);
	}
	
	protected ImageIcon createSmallIcon(Object iconname) {
		if(iconname == null)
			iconname = "Leer";
		URLClassLoader urlLoader = (URLClassLoader)getClass().getClassLoader();
		URL imageURL = urlLoader.findResource("res/" + iconname + "16.png");
		if(DEBUG)
			System.out.println("ActionHandler: Erzeuge Image "+iconname+"@"+imageURL);
		return new ImageIcon(imageURL);
	}
	
	private class HaushaltAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
		private final String name;
		private final ImageIcon bigIcon;
		HaushaltAction(Object[] text) {
			super((String)text[1], createSmallIcon(text[2]));
			name = (String)text[0];
			bigIcon = createBigIcon(text[2]);
			putValue(SHORT_DESCRIPTION, text[3]);
			if(text[4] != null) {
	  		putValue(MNEMONIC_KEY, text[4]);
  			int code = ((Integer)text[4]).intValue();
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(code, Toolkit
						.getDefaultToolkit().getMenuShortcutKeyMask()));
			}
		}
		public ImageIcon getBigIcon() {
			return bigIcon;
		}
		public void actionPerformed(ActionEvent e) {
			Method call;
      try {
        call = Haushalt.class.getMethod(name, (Class[])null);
				call.invoke(haushalt, (Object[])null);
      } catch (SecurityException e1) {
        e1.printStackTrace();
      } catch (NoSuchMethodException e1) {
        e1.printStackTrace();
      } catch (IllegalArgumentException e1) {
        e1.printStackTrace();
      } catch (IllegalAccessException e1) {
        e1.printStackTrace();
      } catch (InvocationTargetException e1) {
        e1.printStackTrace();
      }
		}
	}

}
