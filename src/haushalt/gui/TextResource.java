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

package haushalt.gui;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * Klasse liefert die lokalisierten Menüs, Texte etc.
 * @author Dr. Lars H. Hahn
 * @version 2.5/2006.07.04
 * @since 2.5
 */

 /* 
  * 2006.07.04 Erste Version
  */

public class TextResource {
  private final static boolean DEBUG = false;
  private final static TextResource resource = new TextResource();
  
  private Locale locale = Locale.getDefault();
  private final String bundle = "res/jhh-resources";
  private ResourceBundle resourceBundle = ResourceBundle.getBundle(bundle, locale);
    
  private TextResource() {
  }
  
  public final static TextResource get() {
    return resource;
  }
  
  public void setLocale(String locale_text) {
    StringTokenizer st = new StringTokenizer(locale_text, "_");
    String sprache = st.nextToken();
    String land = st.hasMoreTokens()?st.nextToken():"";
    setLocale(new Locale(sprache, land));
  }
  

  public void setLocale(Locale locale) {
    this.locale = locale;
    try {
      resourceBundle = ResourceBundle.getBundle(bundle, locale);
    }
    catch(MissingResourceException e) {
      this.locale = new Locale("de", "DE");
      resourceBundle = ResourceBundle.getBundle(bundle, locale);
    }
    if(DEBUG) {
      System.out.println("New locale = "+locale.getDisplayName());
      Enumeration<String> liste;
      liste = resourceBundle.getKeys();
      while(liste.hasMoreElements()) {
        System.out.println(liste.nextElement());
      }       
    }
  }
  
  public Locale getLocale() {
    return locale;
  }
  
  public String getString(String key) {
    return resourceBundle.getString(key);
  }
 
  public String getAutoBuchungIntervallName(Integer idx) {
    switch(idx){
    case 1: return resource.getString("automatic_posting_month");
    case 2: return resource.getString("automatic_posting_quarter");
    case 3: return resource.getString("automatic_posting_halfyear");
    case 4: return resource.getString("automatic_posting_year");    
    default: return resource.getString("automatic_posting_week");
    }
  }
  
  public String[] getAutoBuchungIntervallNamen() {
    String[] namen = new String[5];
    namen[0] = resource.getString("automatic_posting_week");
    namen[1] = resource.getString("automatic_posting_month");
    namen[2] = resource.getString("automatic_posting_quarter");
    namen[3] = resource.getString("automatic_posting_halfyear");
    namen[4] = resource.getString("automatic_posting_year");
    return namen;
  }
  
  public Integer getAutoBuchungIntervallIndex(String name) {
    if(name.equals(resource.getString("automatic_posting_week")))
      return new Integer(0);
    if(name.equals(resource.getString("automatic_posting_month")))
      return new Integer(1);
    if(name.equals(resource.getString("automatic_posting_quarter")))
      return new Integer(2);
    if(name.equals(resource.getString("automatic_posting_halfyear")))
      return new Integer(3);
    if(name.equals(resource.getString("automatic_posting_year")))  
      return new Integer(4);
    return new Integer(0);
  }

}
