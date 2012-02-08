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

package haushalt.daten;

import java.util.regex.*;
import java.text.NumberFormat;

/**
 * Parst einen String mit mehreren Zahlen und addiert diese.
 * @author Kay Ruhland
 * @version 2.6/2009.05.17
 * @since 2.6
 */

public class MathParser {
    private NumberFormat nf;

    MathParser( ) {
        this.nf = NumberFormat.getInstance();
    }
    
    MathParser( NumberFormat nf ) {
        this.nf = nf;
    }

    /**
     * Bildet die Summe aus mehreren Zahlen. Bei einem '-' wird die Zahl
     * als negative Zahl interpretiert und somit vom Ergebnis abgezogen.
     * @param expr (" 5� - 3,0")
     * @return (2.0)
     */
    public double parseExpr(String expr) {
        String operatoren = "+-";
        double res = 0.0;

        Pattern p = Pattern.compile( "["+operatoren+"]{0,1}\\s*\\d+[,\\.]{0,1}\\d*" );
        Matcher m = p.matcher(expr);
        
        while ( m.find() ) {
            String sVal = m.group().replace(" ", "");
            if (sVal.startsWith("+")) {
               sVal = sVal.substring(1);
            }
            try {
                res += nf.parse( sVal ).doubleValue();
            }
            catch(java.text.ParseException e) {
                return 0.0;
            }
        }
        
        return res;
    }
    
    public static void main ( String [] args ) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        MathParser mp = new MathParser(nf);
        System.out.println( mp.parseExpr("=  - 123,2 -4,1 CHF + *0,3� 0,5$") );
    }
}
