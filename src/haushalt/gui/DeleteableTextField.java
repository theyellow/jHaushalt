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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;

import javax.swing.JTextField;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */
public class DeleteableTextField extends JTextField implements KeyListener {

	private static final boolean DEBUG = false;
	private static final long serialVersionUID = 1L;
	private static int deltaste = 0;
	private static final Logger LOGGER = Logger.getLogger(DeleteableTextField.class.getName());

	public DeleteableTextField() {
		addKeyListener(this);
	}

	public DeleteableTextField(final int columns) {
		this("", columns);
	}

	public DeleteableTextField(final String text, final int columns) {
		super(text, columns);
		addKeyListener(this);
	}

	public static void setDeltaste(final int taste) {
		deltaste = taste;
	}

	// -- Methoden des Interface 'KeyListener'
	// -----------------------------------

	public void keyReleased(final KeyEvent e) {
		if ((e.getModifiers() == deltaste) && (e.getKeyCode() == 127)) {
			setText("");
		} else if (DEBUG) {
			LOGGER.info("Klasse Event-Source: " + e.getSource().getClass());
			LOGGER.info("Modifier=" + e.getModifiers());
			LOGGER.info("KeyCode=" + e.getKeyCode());
		}
	}

	public void keyPressed(final KeyEvent e) {
		// nichts zu tun !
	}

	public void keyTyped(final KeyEvent e) {
		// nichts zu tun !
	}

}
