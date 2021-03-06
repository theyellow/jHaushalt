package jhaushalt.service.factories;

import java.io.IOException;

import jhaushalt.domain.kategorie.EinzelKategorie;
import jhaushalt.service.factories.io.DataInputFacade;


public class CategoryFactory {

	public static EinzelKategorie getInstance(DataInputFacade in) throws IOException {
		return createCategoryTree(in.getDataString());
	}
	
	private static EinzelKategorie createCategoryTree(final String vollerName) {
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
