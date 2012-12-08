package jhaushalt.service.factories;

import java.io.IOException;

import jhaushalt.domain.Geldbetrag;
import jhaushalt.service.factories.io.DataInputFacade;

public class GeldbetragFactory {

	public static Geldbetrag getInstance(DataInputFacade in) throws IOException {
		return  new Geldbetrag(in.getLong());
	}
}
