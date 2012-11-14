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

package haushalt.gui.generischerdialog;

import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.gui.DeleteableTextField;
import haushalt.gui.ZeitraumComboBox;

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
 * @version 2.0/2004.08.22
 */

public class ZeitraumGDP extends AbstractGDPane {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ZeitraumGDP.class.getName());

	private final ZeitraumComboBox comboBox;
	private final DeleteableTextField textField;

	public ZeitraumGDP(final String textAufforderung, final AbstractZeitraum zeitraum) {
		super(textAufforderung);
		this.comboBox = new ZeitraumComboBox(zeitraum);
		this.textField = new DeleteableTextField(zeitraum.getDatenString(), 10) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Document createDefaultModel() {
				return new ZeitraumDocument();
			}
		};
		add(this.comboBox);
		add(this.textField);
		refreshWert();
	}

	@Override
	protected void refreshWert() {
		setWert(this.comboBox.getZeitraum(this.textField.getText()));
	}

	@Override
	public JComponent getZentraleKomponente() {
		return this.textField;
	}

	@Override
	public void laden(final DataInputStream in) throws IOException {
		final AbstractZeitraum zeitraum = AbstractZeitraum.laden(in);
		this.comboBox.waehleZeitraum(zeitraum);
		this.textField.setText(zeitraum.getDatenString());
	}

	@Override
	public void speichern(final DataOutputStream out) throws IOException {
		final AbstractZeitraum zeitraum = this.comboBox.getZeitraum(this.textField.getText());
		zeitraum.speichern(out);
	}

	private static class ZeitraumDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;

		@Override
		public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
			final char[] source = str.toCharArray();
			final char[] result = new char[source.length];
			int j = 0;

			for (int i = 0; i < source.length; i++) {
				if ((Character.isDigit(source[i])) || (source[i] == '/') || (source[i] == '-') || (source[i] == '.')) {
					result[j++] = source[i];
				} else {
					LOGGER.warning("-I- Falsches Zeichen: " + source[i]);
				}
			}

			super.insertString(offs, new String(result, 0, j), a);
		}
	}
}
