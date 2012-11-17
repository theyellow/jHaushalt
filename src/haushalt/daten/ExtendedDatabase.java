package haushalt.daten;

public class ExtendedDatabase {
	private Datenbasis dataBase;
	private String versionId;
	
	public ExtendedDatabase(final Datenbasis database, final String versionId) {
		this.dataBase = database;
		this.versionId = versionId;
	}
	
	public Datenbasis getDataBase() {
		return dataBase;
	}
	
	public String getVersionId() {
		return versionId;
	}
}
