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

package haushalt.gui;

import haushalt.gui.action.StandardAction;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2006.07.04
 */

/*
 * 2006.07.04 Internationalisierung
 * 2006.01.31 Erweiterung: Kontextmenü hinzugefügt
 */

public class ActionHandler {

	private static final TextResource res = TextResource.get();

	private final Haushalt haushalt;
	private final JPopupMenu popupMenu = new JPopupMenu();
	private final List<StandardAction> menuDatei;
	private final List<StandardAction> menuBearbeiten;
	private final List<StandardAction> menuAusgabe;
	private final List<StandardAction> menuExtras;
	private final List<StandardAction> menuHilfe;

	// Sonderfall: Für MacOS X muss diese Action von Hand zur Toolbar hinzugefuegt werden
	private StandardAction preferences;

	public ActionHandler(final Haushalt haushalt) {
		super();
		this.haushalt = haushalt;

		menuDatei = erzeugeMenuDatei();
		menuBearbeiten = erzeugeMenuBearbeiten();
		menuAusgabe = erzeugeMenuAusgabe();
		menuExtras = erzeugeMenuExtras();
		menuHilfe = erzeugeMenuHilfe();

		// Das PopupMenü wird mit Bearbeiten-Menü belegt.
		for (StandardAction action : menuBearbeiten) {
			this.popupMenu.add(new JMenuItem(action));
		}
	}

	// 0:
	private List<StandardAction> erzeugeMenuDatei() {
		List<StandardAction> standardActions = new LinkedList<StandardAction>();

		standardActions.add(new StandardAction(
			haushalt,
			"neu",
			res.getString("new"),
			"New",
			res.getString("new_legend"),
			new Integer(KeyEvent.VK_N)));
		standardActions.add(new StandardAction(
			haushalt,
			"laden",
			res.getString("open") + "...",
			"Open",
			res.getString("open_legend"),
			new Integer(KeyEvent.VK_L)));
		standardActions.add(new StandardAction(
			haushalt,
			"speichern",
			res.getString("save"),
			"Save",
			res.getString("save_legend"),
			new Integer(KeyEvent.VK_S)));
		standardActions.add(new StandardAction(
			haushalt,
			"speichernUnter",
			res.getString("save_as") + "...",
			"SaveAs",
			res.getString("save_as_legend"),
			null));
		standardActions.add(new StandardAction(
			haushalt,
			"beenden",
			res.getString("exit"),
			null,
			res.getString("exit_legend"),
			new Integer(KeyEvent.VK_X)));

		return standardActions;
	}

	// 1:
	private List<StandardAction> erzeugeMenuBearbeiten() {
		List<StandardAction> standardActions = new LinkedList<StandardAction>();

		standardActions.add(new StandardAction(
			haushalt,
			"neueBuchungErstellen",
			res.getString("new_booking") + "...",
			"AddBuchung",
			res.getString("new_booking_legend"),
			new Integer(KeyEvent.VK_C)));
		standardActions.add(new StandardAction(
			haushalt,
			"loeschen",
			res.getString("delete"),
			"Delete",
			res.getString("delete_legend"),
			new Integer(KeyEvent.VK_D)));
		standardActions.add(new StandardAction(
			haushalt,
			"umbuchen",
			res.getString("rebook") + "...",
			"Umbuchung",
			res.getString("rebook_legend"),
			new Integer(KeyEvent.VK_U)));
		standardActions.add(new StandardAction(
			haushalt,
			"splitten",
			res.getString("split") + "...",
			"Splitten",
			res.getString("split_legend"),
			new Integer(KeyEvent.VK_P)));
		standardActions.add(new StandardAction(
			haushalt,
			"umwandeln",
			res.getString("convert") + "...",
			"Umwandeln",
			res.getString("convert_legend"),
			new Integer(KeyEvent.VK_W)));
		standardActions.add(new StandardAction(
			haushalt,
			"registerBearbeiten",
			res.getString("edit_registers") + "...",
			"Register",
			res.getString("edit_registers_legend"),
			new Integer(KeyEvent.VK_R)));
		standardActions.add(new StandardAction(
			haushalt,
			"kategorienBearbeiten",
			res.getString("edit_category") + "...",
			"Auto",
			res.getString("edit_category_legend"),
			new Integer(KeyEvent.VK_K)));
		standardActions.add(new StandardAction(
			haushalt,
			"suchen",
			res.getString("find") + "...",
			"Find",
			res.getString("find_legend"),
			null));
		standardActions.add(new StandardAction(
			haushalt,
			"alteBuchungenLoeschen",
			res.getString("delete_old_bookings") + "...",
			null,
			res.getString("delete_old_bookings_legend"),
			new Integer(KeyEvent.VK_E)));
		standardActions.add(new StandardAction(
			haushalt,
			"kategorieErsetzen",
			res.getString("replace_category") + "...",
			null,
			res.getString("replace_category_legend"),
			null));
		standardActions.add(new StandardAction(
			haushalt,
			"kategorienBereinigen",
			res.getString("clean_categories") + "...",
			null,
			res.getString("clean_categories_legend"),
			new Integer(KeyEvent.VK_B)));
		standardActions.add(new StandardAction(
			haushalt,
			"registerVereinigen",
			res.getString("join_register") + "...",
			null,
			res.getString("join_register_legend"),
			new Integer(KeyEvent.VK_V)));

		return standardActions;
	}

	// 2:
	private List<StandardAction> erzeugeMenuAusgabe() {
		List<StandardAction> standardActions = new LinkedList<StandardAction>();

		standardActions.add(new StandardAction(
			haushalt,
			"zeigeAuswertung",
			res.getString("show_report") + "...",
			"Auswertung",
			res.getString("show_report_legend"),
			new Integer(KeyEvent.VK_A)));
		standardActions.add(new StandardAction(
			haushalt,
			"exportCSV",
			res.getString("export_csv") + "...",
			"Export",
			res.getString("export_csv_legend"),
			null));
		standardActions.add(new StandardAction(
			haushalt,
			"drucken",
			res.getString("print") + "...",
			"Print",
			res.getString("print_legend"),
			new Integer(KeyEvent.VK_P)));

		return standardActions;
	}

	// 3: Extras
	private List<StandardAction> erzeugeMenuExtras() {
		List<StandardAction> standardActions = new LinkedList<StandardAction>();

		// Sonderfall: Für MacOS X muss diese Action von Hand zur Toolbar hinzugefuegt werden
		preferences = new StandardAction(
			haushalt,
			"optionen",
			res.getString("preferences") + "...",
			"Preferences",
			res.getString("preferences_legend"),
			new Integer(KeyEvent.VK_O));
		final StandardAction autoBuchungen = new StandardAction(haushalt, "autoBuchung", res.getString("automatic_booking")
			+ "...", "Robot", res.getString("automatic_booking_legend"), null);
		final StandardAction importCSV = new StandardAction(
			haushalt,
			"importCSV",
			res.getString("import_csv") + "...",
			"Import",
			res.getString("import_csv_legend"),
			new Integer(KeyEvent.VK_I));
		final StandardAction importQuicken = new StandardAction(haushalt, "importQuicken", res.getString("import_quicken")
			+ "...", null, res.getString("import_quicken_legend"), new Integer(KeyEvent.VK_Q));

		if (!Haushalt.isMacOSX()) {
			standardActions.add(preferences);
		}

		standardActions.add(autoBuchungen);
		standardActions.add(importCSV);
		standardActions.add(importQuicken);

		return standardActions;
	}

	// 4: Hilfe
	private List<StandardAction> erzeugeMenuHilfe() {
		List<StandardAction> standardActions = new LinkedList<StandardAction>();
		final StandardAction hilfe = new StandardAction(
			haushalt,
			"hilfeInhalt",
			res.getString("help_content") + "...",
			"Help",
			res.getString("help_content_legend"),
			new Integer(KeyEvent.VK_F1));
		final StandardAction programmInfo = new StandardAction(
			haushalt,
			"programmInfo",
			res.getString("program_info") + "...",
			"Information",
			res.getString("program_info_legend"),
			null);

		standardActions.add(hilfe);

		if (!Haushalt.isMacOSX()) {
			standardActions.add(programmInfo);
		}

		return standardActions;
	}

	public JMenuBar erzeugeMenuBar() {
		final JMenuBar menuBar = new JMenuBar();

		String title = "";
		JMenu menu = null;

		// Datei:
		title = res.getString("file");
		menu = createMenu(title, menuDatei);
		menuBar.add(menu);

		// Bearbeiten:
		title = res.getString("edit");
		menu = createMenu(title, menuBearbeiten);
		menuBar.add(menu);

		// Ausgabe:
		title = res.getString("output");
		menu = createMenu(title, menuAusgabe);
		menuBar.add(menu);

		// Extras:
		title = res.getString("extras");
		menu = createMenu(title, menuExtras);
		menuBar.add(menu);

		// Hilfe:
		title = res.getString("help");
		menu = createMenu(title, menuHilfe);
		menuBar.add(menu);

		return menuBar;
	}

	private JMenu createMenu(String title, List<StandardAction> actions) {
		JMenu menu = new JMenu(title);
		for (StandardAction action : actions) {
			final JMenuItem menuItem = new JMenuItem(action);
			menu.add(menuItem);
		}
		return menu;
	}

	public JToolBar erzeugeToolBar() {
		final JToolBar toolBar = new JToolBar();

		addActions(toolBar, menuDatei);
		toolBar.addSeparator();
		addActions(toolBar, menuBearbeiten);
		toolBar.addSeparator();
		addActions(toolBar, menuAusgabe);
		toolBar.addSeparator();
		addActions(toolBar, menuExtras);

		if (Haushalt.isMacOSX()) {
			// Sonderfall: Für MacOS X muss die Preferences-Action von Hand zur Toolbar hinzugefuegt werden
			List<StandardAction> preferenceList = new LinkedList<StandardAction>();
			preferenceList.add(preferences);
			addActions(toolBar, preferenceList);
		}

		toolBar.addSeparator();
		addActions(toolBar, menuHilfe);

		return toolBar;
	}

	private void addActions(final JToolBar toolBar, final List<StandardAction> actions) {
		JButton button;
		for (StandardAction action : actions) {
			if (action.getBigIcon() != null) {
				button = createButton(action);
				toolBar.add(button);
			}
		}
	}

	private JButton createButton(StandardAction action) {
		final JButton button = new JButton(action);
		button.setText("");
		button.setIcon(action.getBigIcon());
		return button;
	}

	public JPopupMenu getPopupMenu() {
		return this.popupMenu;
	}

}
