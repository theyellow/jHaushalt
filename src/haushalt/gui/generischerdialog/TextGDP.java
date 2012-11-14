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

import haushalt.gui.DeleteableTextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JComponent;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */

public class TextGDP extends AbstractGDPane {

	private static final long serialVersionUID = 1L;
	private final DeleteableTextField textField;

	public TextGDP(final String textAufforderung, final String textEingabe) {
		super(textAufforderung);
		this.textField = new DeleteableTextField(textEingabe, 20);
		add(this.textField);
		refreshWert();
	}

	@Override
	protected void refreshWert() {
		setWert(this.textField.getText());
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
		out.writeUTF(this.textField.getText());
	}

}