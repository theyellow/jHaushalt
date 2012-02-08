/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

jHaushalt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.


(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

package haushalt.auswertung.bloecke;

import java.awt.Graphics2D;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.6/2010.11.23
 */

/*
 * 2010.11.23 Graphics2D in der Methode 'print' verwendet
 * 2004.08.22 Erste Version (2.0)
 */
abstract public class AbstractGraphikBlock 
  extends AbstractBlock {

  abstract protected int getHoehe(int breite);
    
  public int print(Graphics2D g2d, int zeile, int xStart, int yStart, int hoehe, int breite) {
    if(zeile > 0)
      return -1;
    if(hoehe < getHoehe(breite))
      return 0;
    return paint(g2d, xStart, yStart, breite);
  }
  

}
