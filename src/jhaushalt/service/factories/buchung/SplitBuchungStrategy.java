package jhaushalt.service.factories.buchung;

import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.Geldbetrag;
import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.SplitBuchung;
import jhaushalt.domain.kategorie.EinzelKategorie;
import jhaushalt.domain.kategorie.MehrfachKategorie;
import jhaushalt.service.factories.CategoryFactory;
import jhaushalt.service.factories.DatumFactory;
import jhaushalt.service.factories.GeldbetragFactory;
import jhaushalt.service.factories.io.DataInputFacade;

public class SplitBuchungStrategy implements BuchungStrategy {

	public Buchung loadData(DataInputFacade in) throws IOException, ParseException {
		return laden(in);
	}
	
	private SplitBuchung laden(final DataInputFacade in) throws IOException, ParseException {
		final SplitBuchung splitBuchung = new SplitBuchung();
		splitBuchung.setDatum(DatumFactory.getInstance(in));
		splitBuchung.setText(in.getDataString());
		
		final int size = in.getInt();
		MehrfachKategorie kategorien = new MehrfachKategorie(size); // this.splitKategorie = new MehrfachKategorie(size);
		for (int i = 0; i < size; i++) {
			final EinzelKategorie category = CategoryFactory.getInstance(in);
			final Geldbetrag betrag = GeldbetragFactory.getInstance(in);
			category.setWert(betrag);
			kategorien.add(category);
		}
		splitBuchung.setSplitCategories(kategorien);
		return splitBuchung;
	}

}
