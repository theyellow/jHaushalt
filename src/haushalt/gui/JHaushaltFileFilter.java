package haushalt.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class JHaushaltFileFilter extends FileFilter {
	
	private static final TextResource RES = TextResource.get();
	
	@Override
	public boolean accept(final File file) {
		if (file.isDirectory()) {
			return true;
		}
		if (file.getName().toLowerCase().endsWith(".jhh")) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return RES.getString("jhaushalt_files");
	}
}
