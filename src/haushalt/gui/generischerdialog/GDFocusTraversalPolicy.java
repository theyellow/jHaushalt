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

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.LinkedList;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */
public class GDFocusTraversalPolicy extends FocusTraversalPolicy {

	private final LinkedList<Component> list = new LinkedList<Component>();

	public void addComponent(final Component comp) {
		this.list.addLast(comp);
	}

	@Override
	public Component getComponentAfter(final Container arg0, final Component comp) {
		int index = this.list.indexOf(comp) + 1;
		if (index == this.list.size()) {
			index = 0;
		}
		return this.list.get(index);
	}

	@Override
	public Component getComponentBefore(final Container arg0, final Component comp) {
		int index = this.list.indexOf(comp) - 1;
		if (index == -1) {
			index = this.list.size() - 1;
		}
		return this.list.get(index);
	}

	@Override
	public Component getFirstComponent(final Container arg0) {
		return this.list.getFirst();
	}

	@Override
	public Component getLastComponent(final Container arg0) {
		return this.list.getLast();
	}

	@Override
	public Component getDefaultComponent(final Container arg0) {
		return this.list.getFirst();
	}

}
