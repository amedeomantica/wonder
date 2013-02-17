package er.geo.migrations;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import er.extensions.jdbc.ERXSQLHelper.ColumnIndex;
import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationIndex;
import er.extensions.migration.ERXMigrationTable;
import er.extensions.migration.ERXModelVersion;

public class ItalianCitiesModel0 extends ERXMigrationDatabase.Migration {
	@Override
	public NSArray<ERXModelVersion> modelDependencies() {
		return null;
	}
  
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		// DO NOTHING
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable italianCityTable = database.newTableNamed("ITALIAN_CITY");
		italianCityTable.newIntegerColumn("codiceRegione", false);
		italianCityTable.newIntegerColumn("codiceProvincia", false);
		italianCityTable.newIntegerColumn("codiceComune", false);
		italianCityTable.newIntegerColumn("codiceIstatComune", false);
		italianCityTable.newIntegerColumn("codiceIstatComune103", false);
		italianCityTable.newIntegerColumn("codiceIstatComune107", false);
		italianCityTable.newStringColumn("denominazione", 255, false);
		italianCityTable.newStringColumn("denominazioneItaliano", 255, true);
		italianCityTable.newStringColumn("denominazioneTedesco", 255, true);
		italianCityTable.newIntBooleanColumn("isCapoluogo", false);
		italianCityTable.newIntegerColumn("zonaAltimetrica", false);
		italianCityTable.newIntegerColumn("altitudine", false);
		italianCityTable.newIntBooleanColumn("isLitoraneo", false);
		italianCityTable.newStringColumn("isMontano", 10, false);
		italianCityTable.newDoubleColumn("superficieTerritorialeTotale", 0, 0, false);
		italianCityTable.newIntegerColumn("popolazione", false);
		italianCityTable.create();
	 	italianCityTable.setPrimaryKey("codiceIstatComune");
	}
}