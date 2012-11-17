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

package haushalt.gui.generischerdialog;

import haushalt.gui.DeleteableTextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.1/2006.02.14
 */

/*
 * 2006.02.14 BugFix: Eingabe von "0" verhindern
 * 2004.08.22 Erste Version
 */

public class ZahlGDP extends AbstractGDPane {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ZahlGDP.class.getName());

	private final DeleteableTextField textField;

	public ZahlGDP(final String textAufforderung, final Integer wertEingabe) {
		super(textAufforderung);
		this.textField = new DeleteableTextField("" + wertEingabe, 4) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Document createDefaultModel() {
				return new ZahlDocument();
			}
		};
		add(this.textField);
		refreshWert();
	}

	@Override
	protected void refreshWert() {
		String text = this.textField.getText();
		if (text.equals("")) {
			text = "1";
		}
		setWert(Integer.valueOf(text));
		if (getWert().equals(0)) {
			setWert(1);
		}
		this.textField.setText("" + getWert());
	}

	@Override
	public JComponent getZentraleKomponente() {
		return this.textField;
	}

	@Override
	public void laden(final DataInputStream in) throws IOException {
		this.textField.setText(in.readUTF());
		refreshWert();
	}

	@Override
	public void speichern(final DataOutputStream out) throws IOException {
		refreshWert();
		out.writeUTF(this.textField.getText());
	}

	private static class ZahlDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;

		@Override
		public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
			final char[] source = str.toCharArray();
			final char[] result = new char[source.length];
			int j = 0;

			for (int i = 0; i < source.length; i++) {
				if (Character.isDigit(source[i])) {
					result[j++] = source[i];
				} else {
					LOGGER.warning("-I- Falsches Zeichen: " + source[i]);
				}
			}

			super.insertString(offs, new String(result, 0, j), a);
		}
	}

}
