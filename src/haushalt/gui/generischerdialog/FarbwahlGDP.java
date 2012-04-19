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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2008.01.17
 */

/*
 * 2008.01.17 BugFix: Übergabe der Überschrift an den ColorChooser
 * 2006.02.02 Erste Version
 */

public class FarbwahlGDP extends AbstractGDPane {

	private static final long serialVersionUID = 1L;

	protected final JButton farbeSelektion = new JButton();

	public FarbwahlGDP(final String textAufforderung, final JFrame frame, final Color color) {
		super(textAufforderung);
		this.farbeSelektion.setText(Integer.toHexString(color.getRGB()).toUpperCase());
		this.farbeSelektion.setBackground(color);
		this.farbeSelektion.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				final Color farbe = JColorChooser.showDialog(frame, textAufforderung,
						FarbwahlGDP.this.farbeSelektion.getBackground());
				if (farbe != null) {
					FarbwahlGDP.this.farbeSelektion.setText(Integer.toHexString(farbe.getRGB()).toUpperCase());
					FarbwahlGDP.this.farbeSelektion.setBackground(farbe);
				}
			}
		});
		add(this.farbeSelektion);
		refreshWert();
	}

	@Override
	protected void refreshWert() {
		this.wert = this.farbeSelektion.getBackground();
	}

	@Override
	protected JComponent getZentraleKomponente() {
		return this.farbeSelektion;
	}

	@Override
	public void laden(final DataInputStream in) throws IOException {
		this.farbeSelektion.setBackground(new Color(in.readInt()));
		refreshWert();
	}

	@Override
	public void speichern(final DataOutputStream out) throws IOException {
		refreshWert();
		out.writeInt(this.farbeSelektion.getBackground().getRGB());
	}

}
