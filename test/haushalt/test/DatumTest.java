/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

jHaushalt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.

(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

package haushalt.test;

import static org.junit.Assert.assertTrue;
import haushalt.daten.Datum;

import org.junit.Before;
import org.junit.Test;

public class DatumTest {

  private static Datum datum1;
  private static Datum datum2;
  
  @Before
  public void setUp() throws Exception {
    datum1 = new Datum(24,4,1971);
    datum2 = new Datum(28,3,1970);
  }

  @Test
  public void testSub() {
    assertTrue("Die Differenz zwischen den Daten sollte 392 sein.",
        datum1.sub(datum2) == 392);
    assertTrue("Die Differenz zwischen den Daten sollte -392 sein.",
        datum2.sub(datum1) == -392);
  }

  @Test
  public void testAddiereTage() {
	datum1.addiereTage(-392);
    assertTrue("Das Datum sollte der 28.03.70 sein.",
        datum1.compareTo(datum2) == 0);
    datum1.addiereTage(392);
    datum2.addiereTage(392);
    assertTrue("Das Datum sollte der 24.04.71 sein.",
        datum2.compareTo(datum1) == 0);
  }

  @Test
  public void testGetTag() {
    assertTrue("Der Tag sollte der 24. sein.",
        datum1.getTag() == 24);
  }

  @Test
  public void testGetMonat() {
    assertTrue("Der Monat sollte der 4. sein.",
        datum1.getMonat() == 4);
  }

  @Test
  public void testGetJahr() {
    assertTrue("Das Jahr sollte 1971 sein.",
        datum1.getJahr() == 1971);
  }

  @Test
  public void testCompareTo() {
    assertTrue("24.04.71 sollte größer als der 28.03.70 sein.",
        datum1.compareTo(datum2) > 0);
    assertTrue("24.04.71 sollte größer als der 28.03.70 sein.",
        datum2.compareTo(datum1) < 0);
  }

}
