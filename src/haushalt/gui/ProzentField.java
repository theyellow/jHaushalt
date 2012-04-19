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
 * along with jHaushalt; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */

package haushalt.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Textfeld um Prozent-Werte zu editieren.
 * Bei der Eingabe werden keine Buchstaben akzeptiert.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5.3/2008.03.19
 * @since 2.5
 */

/*
 * 2008.03.19 BugFix: getValue liefert jetzt Integer-Werte
 * 2007.02.20 Erste Version (2.5)
 */

public class ProzentField extends DeleteableTextField {

	private static final long serialVersionUID = 1L;

	public ProzentField(final String prozent) {
		super(prozent, 4);
	}

	public Integer getValue() {
		if (getText().equals("")) {
			return 0;
		}
		final int wert = Integer.parseInt(getText());
		if (wert > 100) {
			return 100;
		}
		return wert;
	}

	@Override
	protected Document createDefaultModel() {
		return new ProzentDocument();
	}

	private static class ProzentDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;

		@Override
		public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
			final char[] source = str.toCharArray();
			final int anzChar = source.length;
			final char[] result = new char[anzChar];
			int j = 0;

			for (int i = 0; i < anzChar; i++) {
				if ((offs + i < 3) && Character.isDigit(source[i])) {
					result[j++] = source[i];
				}
			}
			super.insertString(offs, new String(result, 0, j), a);
		}
	}

}
