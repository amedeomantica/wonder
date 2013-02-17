package er.geo.migrations;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import er.extensions.jdbc.ERXSQLHelper.ColumnIndex;
import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationIndex;
import er.extensions.migration.ERXMigrationTable;
import er.extensions.migration.ERXModelVersion;

public class ItalianCitiesModel1 extends ERXMigrationDatabase.Migration {
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
		ERXMigrationTable italianCityTable = database.existingTableNamed("ITALIAN_CITY");

		ERXMigrationTable italianProvinceTable = database.newTableNamed("ITALIAN_PROVINCE");
		italianProvinceTable.newIntegerColumn("codiceProvincia", false);
		italianProvinceTable.newIntegerColumn("codiceRegione", false);
		italianProvinceTable.newIntegerColumn("codiceRipartizione", false);
		italianProvinceTable.newStringColumn("denominazione", 100, false);
		italianProvinceTable.newStringColumn("denominazioneRegione", 100, false);
		italianProvinceTable.newStringColumn("ripartizioneGeografica", 100, false);
		italianProvinceTable.newStringColumn("sigla", 10, false);
		italianProvinceTable.create();
	 	italianProvinceTable.setPrimaryKey("codiceProvincia");

		//italianCityTable.addForeignKey("codiceProvincia", "ITALIAN_PROVINCE", "codiceProvincia");
	}
}