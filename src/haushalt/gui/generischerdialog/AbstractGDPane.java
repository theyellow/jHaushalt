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

import java.awt.FlowLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */

public abstract class AbstractGDPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private Object wert;

	protected AbstractGDPane(final String text) {
		setBorder(BorderFactory.createTitledBorder(text));
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	public Object getRefreshedWert() {
		refreshWert();
		return wert;
	}

	protected Object getWert() {
		return wert;
	}

	protected void setWert(final Object wert) {
		this.wert = wert;
	}

	protected abstract void refreshWert();

	public void refreshRegisterUndKategorien() {
		// Methode ersetzt die obenstehende; der GDP wird die
		// Datenbasis übergeben
	}

	protected abstract JComponent getZentraleKomponente();

	public abstract void laden(DataInputStream in) throws IOException;

	public abstract void speichern(DataOutputStream out) throws IOException;

}
