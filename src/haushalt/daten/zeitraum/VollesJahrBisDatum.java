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

package haushalt.daten.zeitraum;

import haushalt.daten.Datum;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */

public class VollesJahrBisDatum
		extends AbstractZeitraum {

	private final int tag;
	private final int monat;
	private final int jahr;

	public VollesJahrBisDatum(final String datum) {
		final Datum d = new Datum(datum);
		this.tag = d.getTag();
		this.monat = d.getMonat();
		this.jahr = d.getJahr();
	}

	public VollesJahrBisDatum(final int tag, final int monat, final int jahr) {
		this.tag = tag;
		this.monat = monat;
		this.jahr = jahr;
	}

	public VollesJahrBisDatum(final Datum datum) {
		this.tag = datum.getTag();
		this.monat = datum.getMonat();
		this.jahr = datum.getJahr();
	}

	@Override
	public Datum getStartDatum() {
		return new Datum(this.tag, this.monat, this.jahr - 1);
	}

	@Override
	public Datum getEndDatum() {
		return new Datum(this.tag, this.monat, this.jahr);
	}

	@Override
	public AbstractZeitraum folgeZeitraum() {
		return new VollesJahrBisDatum(this.tag, this.monat, this.jahr + 1);
	}

	@Override
	public String toString() {
		return "" + getStartDatum() + "-" + getEndDatum();
	}

	@Override
	public String getDatenString() {
		return "" + getEndDatum();
	}
}