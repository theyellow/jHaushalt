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

package haushalt.daten.zeitraum;

import haushalt.daten.Datum;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Basisklasse für alle Zeiträume
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.10.31
 */

/*
 * 2007.10.31 Verlagerung der Berechung der Differenz zwischen zwei Tagen in die
 * Klasse Datum
 * 2004.08.22 Version 2.0
 */

abstract public class AbstractZeitraum {

	private static final boolean DEBUG = false;

	/**
	 * Liefert den ersten Tag des Zeitraums.
	 * 
	 * @return erster Tag des Zeitraums
	 */
	abstract public Datum getStartDatum();

	/**
	 * Liefert den letzten Tag des Zeitraums.
	 * 
	 * @return letzter Tag des Zeitraums
	 */
	abstract public Datum getEndDatum();

	/**
	 * Liefert den auf diesen Zeitraum folgenden Zeitraum mit
	 * gleicher Länge.
	 * 
	 * @return nächster Zeitraum
	 */
	abstract public AbstractZeitraum folgeZeitraum();

	/**
	 * Liefert die Anzahl der Tage des Zeitraums.
	 * 
	 * @return Anzahl der Tage
	 */
	final public int getAnzahlTage() {
		return (int) getEndDatum().sub(getStartDatum());
	}

	/**
	 * Liefert eine textuelle Beschreibung des Zeitraums.
	 * 
	 * @return Textbeschreibung des Zeitraums
	 */
	@Override
	abstract public String toString();

	/**
	 * Liefert den String der zum Speichern des Zeitraums verwendet wird.
	 * In der Regel entspricht dies 'toString()'.
	 * 
	 * @return String zum Speichern
	 * @see AbstractZeitraum#toString()
	 */
	public String getDatenString() {
		return toString();
	}

	final public boolean equals(final AbstractZeitraum zeitraum) {
		if (zeitraum == null) {
			return false;
		}
		if (getStartDatum().compareTo(zeitraum.getStartDatum()) != 0) {
			return false;
		}
		if (getEndDatum().compareTo(zeitraum.getEndDatum()) != 0) {
			return false;
		}
		return true;
	}

	@Override
	final public boolean equals(final Object zeitraum) {
		return equals((AbstractZeitraum) zeitraum);
	}

	@Override
	public int hashCode() {
		assert false : "hashCode not designed";
		return 0;
	}

	public static AbstractZeitraum erzeugeZeitraum(final String name, final String parameter) {
		AbstractZeitraum zeitraum = null;
		final Object[] parameters = new String[1];
		parameters[0] = parameter;
		final Class<?>[] parameterTyp = { String.class };
		try {
			final Class<?> klasse = Class.forName(name);
			final Constructor<?> constructor = klasse.getConstructor(parameterTyp);
			zeitraum = (AbstractZeitraum) constructor.newInstance(parameters);
		}
		catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (final SecurityException e) {
			e.printStackTrace();
		}
		catch (final NoSuchMethodException e) {
			e.printStackTrace();
		}
		catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (final InstantiationException e) {
			e.printStackTrace();
		}
		catch (final IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (final InvocationTargetException e) {
			e.printStackTrace();
		}
		return zeitraum;
	}

	// -- E/A-Funktionen -------------------------------------------------------

	public static AbstractZeitraum laden(final DataInputStream in)
			throws IOException {
		final String name = in.readUTF();
		final String parameter = in.readUTF();
		return erzeugeZeitraum(name, parameter);
	}

	public void speichern(final DataOutputStream out)
			throws IOException {
		out.writeUTF(getClass().getName());
		out.writeUTF(getDatenString());
		if (DEBUG) {
			System.out.println(getClass().getName() + "(" + getDatenString() + ")");
		}
	}

}