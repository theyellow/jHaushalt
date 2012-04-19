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

import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.AktuellerMonat;
import haushalt.daten.zeitraum.AktuellesJahr;
import haushalt.daten.zeitraum.AktuellesQuartal;
import haushalt.daten.zeitraum.FreierZeitraum;
import haushalt.daten.zeitraum.Halbjahr;
import haushalt.daten.zeitraum.Jahr;
import haushalt.daten.zeitraum.JahresanfangBisDatum;
import haushalt.daten.zeitraum.JahresanfangBisHeute;
import haushalt.daten.zeitraum.MehrereJahre;
import haushalt.daten.zeitraum.Monat;
import haushalt.daten.zeitraum.Quartal;
import haushalt.daten.zeitraum.VollesJahrBisDatum;
import haushalt.daten.zeitraum.VollesJahrBisHeute;

import javax.swing.JComboBox;

/**
 * Stellt die Auswahlbox für die verschiedenen Zeiträume bereit.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.05.31
 */

/*
 * 2007.05.31 Internationalisierung
 * 2007.02.14 Erweiterung: Aktueller Monat und aktuelles Jahr
 * 2005.02.19 Erweiterung: Zeitraum für mehrere Jahre
 */

public class ZeitraumComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;
	private static final TextResource res = TextResource.get();

	private static final String[] items = {
			res.getString("month"),
			res.getString("quarter"),
			res.getString("halfyear"),
			res.getString("year"),
			res.getString("full_year_to_date"),
			res.getString("full_year_since_today"),
			res.getString("beginning_of_the_year_to_date"),
			res.getString("beginning_of_the_year_since_today"),
			res.getString("free_period"),
			res.getString("several_years"),
			res.getString("current_month"),
			res.getString("current_quarter"),
			res.getString("current_year")
	};

	/**
	 * Es wird der Zeitraum passend zur Klasse des Parameters vorausgewählt.
	 * Wird <b>null<b> übergeben, wird der Zeitraum 'Monat' gewählt.
	 * 
	 * @param zeitraum
	 *            Objekt vom Typ 'ZeitIntervall' zur Auswahl des passenden
	 *            Eintrags in der ComboBox
	 */
	public ZeitraumComboBox(final AbstractZeitraum zeitraum) {
		super(items);
		waehleZeitraum(zeitraum);
	}

	public void waehleZeitraum(final AbstractZeitraum zeitraum) {
		if (zeitraum == null) {
			setSelectedIndex(0);
		}
		else if (zeitraum.getClass() == Quartal.class) {
			setSelectedIndex(1);
		}
		else if (zeitraum.getClass() == Halbjahr.class) {
			setSelectedIndex(2);
		}
		else if (zeitraum.getClass() == Jahr.class) {
			setSelectedIndex(3);
		}
		else if (zeitraum.getClass() == VollesJahrBisDatum.class) {
			setSelectedIndex(4);
		}
		else if (zeitraum.getClass() == VollesJahrBisHeute.class) {
			setSelectedIndex(5);
		}
		else if (zeitraum.getClass() == JahresanfangBisDatum.class) {
			setSelectedIndex(6);
		}
		else if (zeitraum.getClass() == JahresanfangBisHeute.class) {
			setSelectedIndex(7);
		}
		else if (zeitraum.getClass() == FreierZeitraum.class) {
			setSelectedIndex(8);
		}
		else if (zeitraum.getClass() == MehrereJahre.class) {
			setSelectedIndex(9);
		}
		else if (zeitraum.getClass() == AktuellerMonat.class) {
			setSelectedIndex(10);
		}
		else if (zeitraum.getClass() == AktuellesQuartal.class) {
			setSelectedIndex(11);
		}
		else if (zeitraum.getClass() == AktuellesJahr.class) {
			setSelectedIndex(12);
		}
		else {
			setSelectedIndex(0);
		}
	}

	/**
	 * Erzeugt aus dem ausgewählten Eintrag der ComboBox und dem übergebenen
	 * Text einen neuen Zeitraum.
	 * 
	 * @param text
	 *            Wert des Zeitraums
	 * @return erzeugter Zeitraum
	 */
	public AbstractZeitraum getZeitraum(final String text) {
		switch (getSelectedIndex()) {
		case 0:
			return new Monat(text);
		case 1:
			return new Quartal(text);
		case 2:
			return new Halbjahr(text);
		case 3:
			return new Jahr(text);
		case 4:
			return new VollesJahrBisDatum(text);
		case 5:
			return new VollesJahrBisHeute(text);
		case 6:
			return new JahresanfangBisDatum(text);
		case 7:
			return new JahresanfangBisHeute(text);
		case 8:
			return new FreierZeitraum(text);
		case 9:
			return new MehrereJahre(text);
		case 10:
			return new AktuellerMonat(text);
		case 11:
			return new AktuellesQuartal(text);
		case 12:
			return new AktuellesJahr(text);
		}
		return null;
	}
}