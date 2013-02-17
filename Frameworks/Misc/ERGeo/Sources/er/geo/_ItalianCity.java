// DO NOT EDIT.  Make changes to ItalianCity.java instead.
package er.geo;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

import er.extensions.eof.*;
import er.extensions.foundation.*;

@SuppressWarnings("all")
public abstract class _ItalianCity extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "ItalianCity";

  // Attribute Keys
  public static final ERXKey<Integer> ALTITUDINE = new ERXKey<Integer>("altitudine");
  public static final ERXKey<Integer> CODICE_COMUNE = new ERXKey<Integer>("codiceComune");
  public static final ERXKey<Integer> CODICE_ISTAT_COMUNE103 = new ERXKey<Integer>("codiceIstatComune103");
  public static final ERXKey<Integer> CODICE_ISTAT_COMUNE107 = new ERXKey<Integer>("codiceIstatComune107");
  public static final ERXKey<Integer> CODICE_PROVINCIA = new ERXKey<Integer>("codiceProvincia");
  public static final ERXKey<Integer> CODICE_REGIONE = new ERXKey<Integer>("codiceRegione");
  public static final ERXKey<String> DENOMINAZIONE = new ERXKey<String>("denominazione");
  public static final ERXKey<String> DENOMINAZIONE_ITALIANO = new ERXKey<String>("denominazioneItaliano");
  public static final ERXKey<String> DENOMINAZIONE_TEDESCO = new ERXKey<String>("denominazioneTedesco");
  public static final ERXKey<Boolean> IS_CAPOLUOGO = new ERXKey<Boolean>("isCapoluogo");
  public static final ERXKey<Boolean> IS_LITORANEO = new ERXKey<Boolean>("isLitoraneo");
  public static final ERXKey<String> IS_MONTANO = new ERXKey<String>("isMontano");
  public static final ERXKey<Integer> POPOLAZIONE = new ERXKey<Integer>("popolazione");
  public static final ERXKey<Double> SUPERFICIE_TERRITORIALE_TOTALE = new ERXKey<Double>("superficieTerritorialeTotale");
  public static final ERXKey<Integer> ZONA_ALTIMETRICA = new ERXKey<Integer>("zonaAltimetrica");
  // Relationship Keys
  public static final ERXKey<er.geo.ItalianProvince> PROVINCE = new ERXKey<er.geo.ItalianProvince>("province");

  // Attributes
  public static final String ALTITUDINE_KEY = ALTITUDINE.key();
  public static final String CODICE_COMUNE_KEY = CODICE_COMUNE.key();
  public static final String CODICE_ISTAT_COMUNE103_KEY = CODICE_ISTAT_COMUNE103.key();
  public static final String CODICE_ISTAT_COMUNE107_KEY = CODICE_ISTAT_COMUNE107.key();
  public static final String CODICE_PROVINCIA_KEY = CODICE_PROVINCIA.key();
  public static final String CODICE_REGIONE_KEY = CODICE_REGIONE.key();
  public static final String DENOMINAZIONE_KEY = DENOMINAZIONE.key();
  public static final String DENOMINAZIONE_ITALIANO_KEY = DENOMINAZIONE_ITALIANO.key();
  public static final String DENOMINAZIONE_TEDESCO_KEY = DENOMINAZIONE_TEDESCO.key();
  public static final String IS_CAPOLUOGO_KEY = IS_CAPOLUOGO.key();
  public static final String IS_LITORANEO_KEY = IS_LITORANEO.key();
  public static final String IS_MONTANO_KEY = IS_MONTANO.key();
  public static final String POPOLAZIONE_KEY = POPOLAZIONE.key();
  public static final String SUPERFICIE_TERRITORIALE_TOTALE_KEY = SUPERFICIE_TERRITORIALE_TOTALE.key();
  public static final String ZONA_ALTIMETRICA_KEY = ZONA_ALTIMETRICA.key();
  // Relationships
  public static final String PROVINCE_KEY = PROVINCE.key();

  private static Logger LOG = Logger.getLogger(_ItalianCity.class);

  public ItalianCity localInstanceIn(EOEditingContext editingContext) {
    ItalianCity localInstance = (ItalianCity)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public Integer altitudine() {
    return (Integer) storedValueForKey(_ItalianCity.ALTITUDINE_KEY);
  }

  public void setAltitudine(Integer value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating altitudine from " + altitudine() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.ALTITUDINE_KEY);
  }

  public Integer codiceComune() {
    return (Integer) storedValueForKey(_ItalianCity.CODICE_COMUNE_KEY);
  }

  public void setCodiceComune(Integer value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating codiceComune from " + codiceComune() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.CODICE_COMUNE_KEY);
  }

  public Integer codiceIstatComune103() {
    return (Integer) storedValueForKey(_ItalianCity.CODICE_ISTAT_COMUNE103_KEY);
  }

  public void setCodiceIstatComune103(Integer value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating codiceIstatComune103 from " + codiceIstatComune103() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.CODICE_ISTAT_COMUNE103_KEY);
  }

  public Integer codiceIstatComune107() {
    return (Integer) storedValueForKey(_ItalianCity.CODICE_ISTAT_COMUNE107_KEY);
  }

  public void setCodiceIstatComune107(Integer value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating codiceIstatComune107 from " + codiceIstatComune107() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.CODICE_ISTAT_COMUNE107_KEY);
  }

  public Integer codiceProvincia() {
    return (Integer) storedValueForKey(_ItalianCity.CODICE_PROVINCIA_KEY);
  }

  public void setCodiceProvincia(Integer value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating codiceProvincia from " + codiceProvincia() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.CODICE_PROVINCIA_KEY);
  }

  public Integer codiceRegione() {
    return (Integer) storedValueForKey(_ItalianCity.CODICE_REGIONE_KEY);
  }

  public void setCodiceRegione(Integer value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating codiceRegione from " + codiceRegione() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.CODICE_REGIONE_KEY);
  }

  public String denominazione() {
    return (String) storedValueForKey(_ItalianCity.DENOMINAZIONE_KEY);
  }

  public void setDenominazione(String value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating denominazione from " + denominazione() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.DENOMINAZIONE_KEY);
  }

  public String denominazioneItaliano() {
    return (String) storedValueForKey(_ItalianCity.DENOMINAZIONE_ITALIANO_KEY);
  }

  public void setDenominazioneItaliano(String value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating denominazioneItaliano from " + denominazioneItaliano() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.DENOMINAZIONE_ITALIANO_KEY);
  }

  public String denominazioneTedesco() {
    return (String) storedValueForKey(_ItalianCity.DENOMINAZIONE_TEDESCO_KEY);
  }

  public void setDenominazioneTedesco(String value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating denominazioneTedesco from " + denominazioneTedesco() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.DENOMINAZIONE_TEDESCO_KEY);
  }

  public Boolean isCapoluogo() {
    return (Boolean) storedValueForKey(_ItalianCity.IS_CAPOLUOGO_KEY);
  }

  public void setIsCapoluogo(Boolean value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating isCapoluogo from " + isCapoluogo() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.IS_CAPOLUOGO_KEY);
  }

  public Boolean isLitoraneo() {
    return (Boolean) storedValueForKey(_ItalianCity.IS_LITORANEO_KEY);
  }

  public void setIsLitoraneo(Boolean value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating isLitoraneo from " + isLitoraneo() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.IS_LITORANEO_KEY);
  }

  public String isMontano() {
    return (String) storedValueForKey(_ItalianCity.IS_MONTANO_KEY);
  }

  public void setIsMontano(String value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating isMontano from " + isMontano() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.IS_MONTANO_KEY);
  }

  public Integer popolazione() {
    return (Integer) storedValueForKey(_ItalianCity.POPOLAZIONE_KEY);
  }

  public void setPopolazione(Integer value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating popolazione from " + popolazione() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.POPOLAZIONE_KEY);
  }

  public Double superficieTerritorialeTotale() {
    return (Double) storedValueForKey(_ItalianCity.SUPERFICIE_TERRITORIALE_TOTALE_KEY);
  }

  public void setSuperficieTerritorialeTotale(Double value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating superficieTerritorialeTotale from " + superficieTerritorialeTotale() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.SUPERFICIE_TERRITORIALE_TOTALE_KEY);
  }

  public Integer zonaAltimetrica() {
    return (Integer) storedValueForKey(_ItalianCity.ZONA_ALTIMETRICA_KEY);
  }

  public void setZonaAltimetrica(Integer value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
    	_ItalianCity.LOG.debug( "updating zonaAltimetrica from " + zonaAltimetrica() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianCity.ZONA_ALTIMETRICA_KEY);
  }

  public er.geo.ItalianProvince province() {
    return (er.geo.ItalianProvince)storedValueForKey(_ItalianCity.PROVINCE_KEY);
  }
  
  public void setProvince(er.geo.ItalianProvince value) {
    takeStoredValueForKey(value, _ItalianCity.PROVINCE_KEY);
  }

  public void setProvinceRelationship(er.geo.ItalianProvince value) {
    if (_ItalianCity.LOG.isDebugEnabled()) {
      _ItalianCity.LOG.debug("updating province from " + province() + " to " + value);
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	setProvince(value);
    }
    else if (value == null) {
    	er.geo.ItalianProvince oldValue = province();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _ItalianCity.PROVINCE_KEY);
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, _ItalianCity.PROVINCE_KEY);
    }
  }
  

  public static ItalianCity createItalianCity(EOEditingContext editingContext, Integer altitudine
, Integer codiceComune
, Integer codiceIstatComune103
, Integer codiceIstatComune107
, Integer codiceProvincia
, Integer codiceRegione
, String denominazione
, Boolean isCapoluogo
, Boolean isLitoraneo
, String isMontano
, Integer popolazione
, Double superficieTerritorialeTotale
, Integer zonaAltimetrica
, er.geo.ItalianProvince province) {
    ItalianCity eo = (ItalianCity) EOUtilities.createAndInsertInstance(editingContext, _ItalianCity.ENTITY_NAME);    
		eo.setAltitudine(altitudine);
		eo.setCodiceComune(codiceComune);
		eo.setCodiceIstatComune103(codiceIstatComune103);
		eo.setCodiceIstatComune107(codiceIstatComune107);
		eo.setCodiceProvincia(codiceProvincia);
		eo.setCodiceRegione(codiceRegione);
		eo.setDenominazione(denominazione);
		eo.setIsCapoluogo(isCapoluogo);
		eo.setIsLitoraneo(isLitoraneo);
		eo.setIsMontano(isMontano);
		eo.setPopolazione(popolazione);
		eo.setSuperficieTerritorialeTotale(superficieTerritorialeTotale);
		eo.setZonaAltimetrica(zonaAltimetrica);
    eo.setProvinceRelationship(province);
    return eo;
  }

  public static ERXFetchSpecification<ItalianCity> fetchSpec() {
    return new ERXFetchSpecification<ItalianCity>(_ItalianCity.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ItalianCity> fetchAllItalianCities(EOEditingContext editingContext) {
    return _ItalianCity.fetchAllItalianCities(editingContext, null);
  }

  public static NSArray<ItalianCity> fetchAllItalianCities(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ItalianCity.fetchItalianCities(editingContext, null, sortOrderings);
  }

  public static NSArray<ItalianCity> fetchItalianCities(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ItalianCity> fetchSpec = new ERXFetchSpecification<ItalianCity>(_ItalianCity.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<ItalianCity> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ItalianCity fetchItalianCity(EOEditingContext editingContext, String keyName, Object value) {
    return _ItalianCity.fetchItalianCity(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ItalianCity fetchItalianCity(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ItalianCity> eoObjects = _ItalianCity.fetchItalianCities(editingContext, qualifier, null);
    ItalianCity eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ItalianCity that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ItalianCity fetchRequiredItalianCity(EOEditingContext editingContext, String keyName, Object value) {
    return _ItalianCity.fetchRequiredItalianCity(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ItalianCity fetchRequiredItalianCity(EOEditingContext editingContext, EOQualifier qualifier) {
    ItalianCity eoObject = _ItalianCity.fetchItalianCity(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ItalianCity that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ItalianCity localInstanceIn(EOEditingContext editingContext, ItalianCity eo) {
    ItalianCity localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
