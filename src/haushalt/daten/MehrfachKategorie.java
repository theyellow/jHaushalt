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
package haushalt.daten;

import java.util.ArrayList;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.1/2006.02.10
 */

/*
 * 2006.02.10 Erste Version
 */
public class MehrfachKategorie extends ArrayList<EinzelKategorie> implements IKategorie {

	private static final long serialVersionUID = 1L;

	public MehrfachKategorie() {
		super();
	}

	public MehrfachKategorie(final int size) {
		super(size);
	}

	@Override
	public String toString() {
		String text = "{";
		for (int i = 0; i < size(); i++) {
			text += get(i);
			if (i < size() - 1) {
				text += ", ";
			}
			else {
				text += "}";
			}
		}
		return text;
	}

	public int compareTo(final IKategorie kategorie) {
		return toString().compareTo(kategorie.toString());
	}

}
