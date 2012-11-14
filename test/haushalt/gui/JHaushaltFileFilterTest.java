package haushalt.gui;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class JHaushaltFileFilterTest {

	private JHaushaltFileFilter filter;
	private File anyDirectory;
	private File testFile;

	@Before
	public void setUp() {
		filter = new JHaushaltFileFilter();
		
		anyDirectory = mock(File.class);
		when(anyDirectory.isDirectory()).thenReturn(true);
		
		testFile = mock(File.class);
		when(testFile.isDirectory()).thenReturn(false);				
	}
	
	@Test
	public void filterAcceptsDirectories() {
		assertThat(filter.accept(anyDirectory)).isTrue();
	}

	@Test
	public void filterFilesAcceptsSpecificFileEndingInLowerCase() {
		when(testFile.getName()).thenReturn("/ANY/PATH/to/haushaltsFile.jhh");
		assertThat(filter.accept(testFile)).isTrue();
	}

	@Test
	public void filterFilesAcceptsSpecificFilesEndingInUpperCase() {
		when(testFile.getName()).thenReturn("/ANY/PATH/to/haushaltsFile.JHH");
		assertThat(filter.accept(testFile)).isTrue();
	}

	@Test
	public void filterFilesDeniesFilesWithoutEnding() {
		when(testFile.getName()).thenReturn("/ANY/PATH/to/haushaltsFile");
		assertThat(filter.accept(testFile)).isFalse();
	}

	@Test
	public void filterFilesDeniesFilesWithWrongEnding() {
		when(testFile.getName()).thenReturn("/ANY/PATH/to/haushaltsFile.txt");
		assertThat(filter.accept(testFile)).isFalse();
	}
	
}
