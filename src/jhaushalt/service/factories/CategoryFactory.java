package jhaushalt.service.factories;


import java.io.IOException;

import jhaushalt.domain.kategorie.EinzelKategorie;
import jhaushalt.domain.kategorie.Kategorie;

public class CategoryFactory {

	public static Kategorie getInstance(DataSourceHolder in) throws IOException {
		return createCategoryTree(in.getDataString());
	}
	
	
	private static Kategorie createCategoryTree(final String vollerName) {
		final int n = vollerName.indexOf(":");
		if (n == -1) {
			return createOneTreeNodeOrLeave(vollerName, null);
		}
		final EinzelKategorie hauptkategorie = createOneTreeNodeOrLeave(vollerName.substring(0, n), null);
		return createOneTreeNodeOrLeave(vollerName.substring(n + 1), hauptkategorie);
	}
	
	/**
	 * Liefert die IKategorie mit dem angegebene Namen zurück.
	 * Wenn sie noch nicht existiert, wird sie erzeugt.
	 * 
	 * @param name
	 *            Name der IKategorie
	 * @param hauptkategorie
	 *            Hauptkategorie
	 * @return gesuchte IKategorie
	 */
	private static EinzelKategorie createOneTreeNodeOrLeave(final String name, final EinzelKategorie hauptkategorie) {
		return new EinzelKategorie(name, hauptkategorie);
	}

}
