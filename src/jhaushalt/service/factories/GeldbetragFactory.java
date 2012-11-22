package jhaushalt.service.factories;

import java.io.IOException;

import jhaushalt.domain.Geldbetrag;

public class GeldbetragFactory {

	public static Geldbetrag getInstance(DataSourceHolder in) throws IOException {
		return  new Geldbetrag(in.getLong());
	}
}
