package haushalt.gui.dialoge;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

public class ColorRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 1L;
	Border unselectedBorder = null;
	Border selectedBorder = null;
	boolean isBordered = true;

	public ColorRenderer(final boolean isBordered) {
		this.isBordered = isBordered;
		setOpaque(true); // MUST do this for background to show up.
	}

	public Component getListCellRendererComponent(final JList list,
			final Object color,
			final int index,
			final boolean isSelected,
			final boolean hasFocus) {
		final Color newColor = (Color) color;
		setBackground(newColor);
		if (this.isBordered) {
			if (isSelected) {
				if (this.selectedBorder == null) {
					this.selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
												list.getSelectionBackground());
				}
				setBorder(this.selectedBorder);
			}
			else {
				if (this.unselectedBorder == null) {
					this.unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
												list.getBackground());
				}
				setBorder(this.unselectedBorder);
			}
		}

		setText("#" + Integer.toHexString(newColor.getRGB()).toUpperCase());
		return this;
	}
}