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

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Klasse liefert die lokalisierten Menüs, Texte etc.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5/2006.07.04
 * @since 2.5
 */

/*
 * 2006.07.04 Erste Version
 */

public final class TextResource {

	private static final boolean DEBUG = false;
	private static final TextResource RESOURCE = new TextResource();
	private static final Logger LOGGER = Logger.getLogger(TextResource.class.getName());

	private Locale locale = Locale.getDefault();
	private final String bundle = "res/jhh-resources";
	private ResourceBundle resourceBundle = ResourceBundle.getBundle(this.bundle, this.locale);

	private TextResource() {}

	public static TextResource get() {
		return RESOURCE;
	}

	public void setLocale(final String localeText) {
		final StringTokenizer st = new StringTokenizer(localeText, "_");
		final String sprache = st.nextToken();
		final String land = st.hasMoreTokens() ? st.nextToken() : "";
		setLocale(new Locale(sprache, land));
	}

	public void setLocale(final Locale locale) {
		this.locale = locale;
		try {
			this.resourceBundle = ResourceBundle.getBundle(this.bundle, locale);
		} catch (final MissingResourceException e) {
			this.locale = new Locale("de", "DE");
			this.resourceBundle = ResourceBundle.getBundle(this.bundle, locale);
		}
		if (DEBUG) {
			LOGGER.info("New locale = " + locale.getDisplayName());
			Enumeration<String> liste;
			liste = this.resourceBundle.getKeys();
			while (liste.hasMoreElements()) {
				LOGGER.info(liste.nextElement());
			}
		}
	}

	public Locale getLocale() {
		return this.locale;
	}

	public String getString(final String key) {
		return this.resourceBundle.getString(key);
	}

	public String getAutoBuchungIntervallName(final Integer idx) {
		switch (idx) {
			case 1:
				return RESOURCE.getString("automatic_posting_month");
			case 2:
				return RESOURCE.getString("automatic_posting_quarter");
			case 3:
				return RESOURCE.getString("automatic_posting_halfyear");
			case 4:
				return RESOURCE.getString("automatic_posting_year");
			default:
				return RESOURCE.getString("automatic_posting_week");
		}
	}

	public String[] getAutoBuchungIntervallNamen() {
		final String[] namen = new String[5];
		namen[0] = RESOURCE.getString("automatic_posting_week");
		namen[1] = RESOURCE.getString("automatic_posting_month");
		namen[2] = RESOURCE.getString("automatic_posting_quarter");
		namen[3] = RESOURCE.getString("automatic_posting_halfyear");
		namen[4] = RESOURCE.getString("automatic_posting_year");
		return namen;
	}

	public Integer getAutoBuchungIntervallIndex(final String name) {
		if (name.equals(RESOURCE.getString("automatic_posting_week"))) {
			return new Integer(0);
		}
		if (name.equals(RESOURCE.getString("automatic_posting_month"))) {
			return new Integer(1);
		}
		if (name.equals(RESOURCE.getString("automatic_posting_quarter"))) {
			return new Integer(2);
		}
		if (name.equals(RESOURCE.getString("automatic_posting_halfyear"))) {
			return new Integer(3);
		}
		if (name.equals(RESOURCE.getString("automatic_posting_year"))) {
			return new Integer(4);
		}
		return new Integer(0);
	}

}
