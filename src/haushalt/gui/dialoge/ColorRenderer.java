package haushalt.gui.dialoge;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Component;

public class ColorRenderer extends JLabel implements ListCellRenderer {
  private static final long serialVersionUID = 1L;
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    public ColorRenderer(boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque(true); //MUST do this for background to show up.
    }

    public Component getListCellRendererComponent(JList list,
        Object color,
        int index,
        boolean isSelected,
        boolean hasFocus) {
        Color newColor = (Color)color;
        setBackground(newColor);
        if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                              list.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                              list.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }
        
        setText("#"+Integer.toHexString(newColor.getRGB()).toUpperCase());
        return this;
    }
}