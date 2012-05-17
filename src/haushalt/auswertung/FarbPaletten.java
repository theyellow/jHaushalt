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
 * along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */

package haushalt.auswertung;

import java.awt.Color;
import java.util.StringTokenizer;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.4/2008.04.24
 * @since 2.0
 */

/*
 * 2008.04.24 Erweiterung um benutzerdfinierte Palette
 * 2008.04.24 BugFix: Korrektur der Palette 'Bunt'
 * 2004.08.22 Erste Version
 */
public final class FarbPaletten {

	private static final Color[] FS_STANDARD = {
			new Color(192, 192, 255), new Color(128, 128, 255), new Color(64, 64, 255), new Color(150, 64, 200),
			new Color(200, 0, 100), new Color(255, 0, 100), new Color(200, 100, 0), new Color(200, 150, 50),
			new Color(200, 200, 50), new Color(200, 255, 50), new Color(255, 255, 50)};

	private static final Color[] FS_SLOW = {
			new Color(192, 207, 127), new Color(240, 128, 96), new Color(111, 159, 160), new Color(207, 159, 15),
			new Color(159, 128, 176), new Color(80, 144, 111), new Color(128, 207, 223), new Color(224, 207, 144),
			new Color(191, 159, 143), new Color(175, 144, 96), new Color(144, 80, 127)};

	private static final Color[] FS_BUNT = {
			new Color(255, 128, 128), new Color(255, 255, 128), new Color(128, 255, 128), new Color(128, 255, 255),
			new Color(128, 128, 255), new Color(255, 128, 255), new Color(192, 0, 0), new Color(192, 192, 0),
			new Color(0, 192, 0), new Color(0, 192, 192), new Color(0, 0, 192), new Color(192, 0, 192)};

	private static final Color[] FS_GRAUSTUFEN = {
			new Color(120, 120, 120), new Color(140, 140, 140), new Color(160, 160, 160), new Color(180, 180, 180),
			new Color(200, 200, 200), new Color(220, 220, 220), new Color(200, 200, 200), new Color(180, 180, 180),
			new Color(160, 160, 160), new Color(140, 140, 140)};

	private static final Color[] FS_PASTELL = {
			new Color(140, 120, 140), new Color(140, 170, 210), new Color(120, 170, 150), new Color(210, 140, 120),
			new Color(150, 140, 120), new Color(170, 210, 170), new Color(150, 140, 120), new Color(210, 140, 120),
			new Color(120, 170, 150), new Color(140, 170, 210)};

	private static final Color[] FS_ZEBRA = {new Color(190, 190, 190), new Color(255, 255, 255)};

	private static Color[] fsCustom = {new Color(255, 0, 0), new Color(0, 255, 0), new Color(0, 0, 255)};

	private static final String[] PALETTEN_NAMEN = {"Standard", "Slow", "Bunt", "Graustufen", "Pastell", "Zebra", "Custom"};

	private static final Color[][] PALETTEN = {FS_STANDARD, FS_SLOW, FS_BUNT, FS_GRAUSTUFEN, FS_PASTELL, FS_ZEBRA, fsCustom};

	private static final String[] FARBEN_NAMEN = {"Weiß", "Schwarz", "Grau", "Rot", "Gelb", "Blau", "Grün"};

	private static final Color[] FARBEN = {
			Color.white, Color.black, Color.lightGray, Color.red, Color.yellow, Color.blue, Color.green};

	private FarbPaletten() {}

	public static Color getFarbe(final int nr, final String name) {
		for (int i = 0; i < PALETTEN.length; i++) {
			if (name.equalsIgnoreCase(PALETTEN_NAMEN[i])) {
				final Color[] palette = PALETTEN[i];
				return palette[nr % palette.length];
			}
		}
		for (int i = 0; i < FARBEN.length; i++) {
			if (name.equalsIgnoreCase(FARBEN_NAMEN[i])) {
				return FARBEN[i];
			}
		}
		return new Color(0, 0, 0);
	}

	public static int setCustomColor(final Color[] farben) {
		fsCustom = farben;
		for (int i = 0; i < PALETTEN.length; i++) {
			if (PALETTEN_NAMEN[i].equalsIgnoreCase("Custom")) {
				PALETTEN[i] = farben;
			}
		}
		return farben.length;
	}

	public static int setCustomColor(final String farben) {
		final StringTokenizer st = new StringTokenizer(farben, ",");
		final int anzahl = st.countTokens();
		fsCustom = new Color[anzahl];
		for (int i = 0; i < anzahl; i++) {
			fsCustom[i] = new Color(Integer.parseInt(st.nextToken()));
		}
		for (int i = 0; i < PALETTEN.length; i++) {
			if (PALETTEN_NAMEN[i].equalsIgnoreCase("Custom")) {
				PALETTEN[i] = fsCustom;
			}
		}
		return anzahl;
	}

	public static String getCustomColor() {
		String farben = "";
		for (int i = 0; i < fsCustom.length; i++) {
			if (i != 0) {
				farben += ",";
			}
			farben += "" + fsCustom[i].getRGB();
		}
		return farben;
	}

	public static String[] getPalettenNamen() {
		return PALETTEN_NAMEN;
	}
}
