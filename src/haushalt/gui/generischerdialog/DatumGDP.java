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

import haushalt.daten.Datum;
import haushalt.gui.DatumField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JComponent;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */

public class DatumGDP extends AbstractGDPane {

	private static final long serialVersionUID = 1L;
	private final DatumField datumField;

	public DatumGDP(final String text, final Datum datum) {
		super(text);
		this.datumField = new DatumField(datum);
		add(this.datumField);
		refreshWert();
	}

	@Override
	protected void refreshWert() {
		setWert(new Datum(this.datumField.getText()));
		this.datumField.setText("" + getWert()); // neusetzen, da ggf. Fehler
													// beim Parsen
	}

	@Override
	public JComponent getZentraleKomponente() {
		return this.datumField;
	}

	@Override
	public void laden(final DataInputStream in) throws IOException {
		final Datum datum = (Datum) getRefreshedWert();
		datum.laden(in);
		this.datumField.setText("" + datum);
	}

	@Override
	public void speichern(final DataOutputStream out) throws IOException {
		refreshWert();
		final Datum datum = (Datum) getRefreshedWert();
		datum.speichern(out);
	}

}