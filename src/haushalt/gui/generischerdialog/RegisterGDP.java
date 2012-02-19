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

package haushalt.gui.generischerdialog;

import haushalt.daten.Datenbasis;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.1/2006.01.27
 */

/*
 * 2006.01.27 Übergabe der Datenbasis, statt der Register-Namen
 */

public class RegisterGDP extends TextArrayGDP {

  private static final long serialVersionUID = 1L;
  private final Datenbasis db;

  public RegisterGDP(String text, Datenbasis datenbasis, String auswahl) {
    super(text, datenbasis.getRegisterNamen(), auswahl);
    db = datenbasis;
  }
  
  public void refreshRegisterUndKategorien() {
    String[] register = db.getRegisterNamen();
    String regname = (String) comboBox.getSelectedItem();
    comboBox.removeAllItems();
    for(int i=0; i<register.length; i++) {
      comboBox.addItem(register[i]);
    }
    if(regname != null)
      comboBox.setSelectedItem(regname);
  }

}
