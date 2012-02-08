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

import haushalt.daten.Euro;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.1/2006.02.03
 */

 /*
  * 2006.02.03 BugFix: Abfangen von value==null
  */
public class EuroRenderer extends DefaultTableCellRenderer {
  private static final long serialVersionUID = 1L;
  public Component getTableCellRendererComponent(
      JTable table,
      Object value,
      boolean isSelected,
      boolean hasFocus,
      int row,
      int col)
    {
      JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
      comp.setHorizontalAlignment(SwingConstants.RIGHT);
      if((value == null) || (Euro.NULL_EURO.compareTo((Euro) value) == 0))
        comp.setText("");
      else if(Euro.NULL_EURO.compareTo((Euro) value) < 0)
        comp.setForeground(Color.black);
      else
        comp.setForeground(Color.red);
      return comp;
    }

}
