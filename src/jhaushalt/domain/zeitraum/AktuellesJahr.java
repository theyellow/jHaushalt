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
package jhaushalt.domain.zeitraum;

import haushalt.daten.Datum;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.02.14
 */

/*
 * 2007.02.14 Erste Version
 */

public class AktuellesJahr extends Jahr {

	public AktuellesJahr(final String dummy) {
		super(new Datum().getJahr());
	}

}
