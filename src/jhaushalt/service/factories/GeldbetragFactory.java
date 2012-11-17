package jhaushalt.service.factories;

import java.io.DataInputStream;
import java.io.IOException;

import jhaushalt.domain.Geldbetrag;

public class GeldbetragFactory {

	public static Geldbetrag getInstance(DataInputStream in) throws IOException {
		return  new Geldbetrag(in.readLong());
	}
}
