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

import java.util.logging.Logger;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.1/2006.02.10
 * @since 2.1
 */

/*
 * 2006.02.10 Erste Version
 */

public class UmbuchungKategorie implements Kategorie, Cloneable {

	private static final Logger LOGGER = Logger.getLogger(UmbuchungKategorie.class.getName());
	private static final long serialVersionUID = 1L;

	private Register quelle;
	private Register ziel;

	public UmbuchungKategorie(final Register quelle, final Register ziel) {
		this.quelle = quelle;
		this.ziel = ziel;
	}

	public Register getPartnerRegister(final Register register) {
		return (register == this.quelle) ? this.ziel : this.quelle;
	}

	public Register getPartnerRegister(final String regname) {
		return regname.equals("" + this.quelle) ? this.ziel : this.quelle;
	}

	public boolean isSelbstbuchung() {
		return (this.quelle == this.ziel);
	}

	@Override
	public String toString() {
		return "[" + this.quelle + "->" + this.ziel + "]";
	}

	public int compareTo(final Kategorie kategorie) {
		return toString().compareTo(kategorie.toString());
	}

	public Register getQuelle() {
		return this.quelle;
	}

	public Register getZiel() {
		return this.ziel;
	}

	@Override
	public final Object clone() {
		UmbuchungKategorie umbuchungKategorie = new UmbuchungKategorie(this.quelle, this.ziel);
		try {
			umbuchungKategorie = (UmbuchungKategorie) super.clone();
		} catch (final CloneNotSupportedException e) {
			LOGGER.warning("Cloning error. This should never happen.");
		}
		umbuchungKategorie.setQuelle(quelle);
		umbuchungKategorie.setZiel(ziel);
		return umbuchungKategorie;
	}

	public void setQuelle(final Object neuesRegister) {
		this.quelle = (Register) neuesRegister;
	}

	public void setZiel(final Object neuesRegister) {
		this.ziel = (Register) neuesRegister;
	}
}
