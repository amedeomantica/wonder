// DO NOT EDIT.  Make changes to ItalianProvince.java instead.
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
public abstract class _ItalianProvince extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "ItalianProvince";

  // Attribute Keys
  public static final ERXKey<Integer> CODICE_REGIONE = new ERXKey<Integer>("codiceRegione");
  public static final ERXKey<Integer> CODICE_RIPARTIZIONE = new ERXKey<Integer>("codiceRipartizione");
  public static final ERXKey<String> DENOMINAZIONE = new ERXKey<String>("denominazione");
  public static final ERXKey<String> DENOMINAZIONE_REGIONE = new ERXKey<String>("denominazioneRegione");
  public static final ERXKey<String> RIPARTIZIONE_GEOGRAFICA = new ERXKey<String>("ripartizioneGeografica");
  public static final ERXKey<String> SIGLA = new ERXKey<String>("sigla");
  // Relationship Keys
  public static final ERXKey<er.geo.ItalianCity> CITIES = new ERXKey<er.geo.ItalianCity>("cities");

  // Attributes
  public static final String CODICE_REGIONE_KEY = CODICE_REGIONE.key();
  public static final String CODICE_RIPARTIZIONE_KEY = CODICE_RIPARTIZIONE.key();
  public static final String DENOMINAZIONE_KEY = DENOMINAZIONE.key();
  public static final String DENOMINAZIONE_REGIONE_KEY = DENOMINAZIONE_REGIONE.key();
  public static final String RIPARTIZIONE_GEOGRAFICA_KEY = RIPARTIZIONE_GEOGRAFICA.key();
  public static final String SIGLA_KEY = SIGLA.key();
  // Relationships
  public static final String CITIES_KEY = CITIES.key();

  private static Logger LOG = Logger.getLogger(_ItalianProvince.class);

  public ItalianProvince localInstanceIn(EOEditingContext editingContext) {
    ItalianProvince localInstance = (ItalianProvince)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public Integer codiceRegione() {
    return (Integer) storedValueForKey(_ItalianProvince.CODICE_REGIONE_KEY);
  }

  public void setCodiceRegione(Integer value) {
    if (_ItalianProvince.LOG.isDebugEnabled()) {
    	_ItalianProvince.LOG.debug( "updating codiceRegione from " + codiceRegione() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianProvince.CODICE_REGIONE_KEY);
  }

  public Integer codiceRipartizione() {
    return (Integer) storedValueForKey(_ItalianProvince.CODICE_RIPARTIZIONE_KEY);
  }

  public void setCodiceRipartizione(Integer value) {
    if (_ItalianProvince.LOG.isDebugEnabled()) {
    	_ItalianProvince.LOG.debug( "updating codiceRipartizione from " + codiceRipartizione() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianProvince.CODICE_RIPARTIZIONE_KEY);
  }

  public String denominazione() {
    return (String) storedValueForKey(_ItalianProvince.DENOMINAZIONE_KEY);
  }

  public void setDenominazione(String value) {
    if (_ItalianProvince.LOG.isDebugEnabled()) {
    	_ItalianProvince.LOG.debug( "updating denominazione from " + denominazione() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianProvince.DENOMINAZIONE_KEY);
  }

  public String denominazioneRegione() {
    return (String) storedValueForKey(_ItalianProvince.DENOMINAZIONE_REGIONE_KEY);
  }

  public void setDenominazioneRegione(String value) {
    if (_ItalianProvince.LOG.isDebugEnabled()) {
    	_ItalianProvince.LOG.debug( "updating denominazioneRegione from " + denominazioneRegione() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianProvince.DENOMINAZIONE_REGIONE_KEY);
  }

  public String ripartizioneGeografica() {
    return (String) storedValueForKey(_ItalianProvince.RIPARTIZIONE_GEOGRAFICA_KEY);
  }

  public void setRipartizioneGeografica(String value) {
    if (_ItalianProvince.LOG.isDebugEnabled()) {
    	_ItalianProvince.LOG.debug( "updating ripartizioneGeografica from " + ripartizioneGeografica() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianProvince.RIPARTIZIONE_GEOGRAFICA_KEY);
  }

  public String sigla() {
    return (String) storedValueForKey(_ItalianProvince.SIGLA_KEY);
  }

  public void setSigla(String value) {
    if (_ItalianProvince.LOG.isDebugEnabled()) {
    	_ItalianProvince.LOG.debug( "updating sigla from " + sigla() + " to " + value);
    }
    takeStoredValueForKey(value, _ItalianProvince.SIGLA_KEY);
  }

  public NSArray<er.geo.ItalianCity> cities() {
    return (NSArray<er.geo.ItalianCity>)storedValueForKey(_ItalianProvince.CITIES_KEY);
  }

  public NSArray<er.geo.ItalianCity> cities(EOQualifier qualifier) {
    return cities(qualifier, null, false);
  }

  public NSArray<er.geo.ItalianCity> cities(EOQualifier qualifier, boolean fetch) {
    return cities(qualifier, null, fetch);
  }

  public NSArray<er.geo.ItalianCity> cities(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.geo.ItalianCity> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = new EOKeyValueQualifier(er.geo.ItalianCity.PROVINCE_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
      }

      results = er.geo.ItalianCity.fetchItalianCities(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = cities();
      if (qualifier != null) {
        results = (NSArray<er.geo.ItalianCity>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.geo.ItalianCity>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }
  
  public void addToCities(er.geo.ItalianCity object) {
    includeObjectIntoPropertyWithKey(object, _ItalianProvince.CITIES_KEY);
  }

  public void removeFromCities(er.geo.ItalianCity object) {
    excludeObjectFromPropertyWithKey(object, _ItalianProvince.CITIES_KEY);
  }

  public void addToCitiesRelationship(er.geo.ItalianCity object) {
    if (_ItalianProvince.LOG.isDebugEnabled()) {
      _ItalianProvince.LOG.debug("adding " + object + " to cities relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	addToCities(object);
    }
    else {
    	addObjectToBothSidesOfRelationshipWithKey(object, _ItalianProvince.CITIES_KEY);
    }
  }

  public void removeFromCitiesRelationship(er.geo.ItalianCity object) {
    if (_ItalianProvince.LOG.isDebugEnabled()) {
      _ItalianProvince.LOG.debug("removing " + object + " from cities relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	removeFromCities(object);
    }
    else {
    	removeObjectFromBothSidesOfRelationshipWithKey(object, _ItalianProvince.CITIES_KEY);
    }
  }

  public er.geo.ItalianCity createCitiesRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName( er.geo.ItalianCity.ENTITY_NAME );
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, _ItalianProvince.CITIES_KEY);
    return (er.geo.ItalianCity) eo;
  }

  public void deleteCitiesRelationship(er.geo.ItalianCity object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _ItalianProvince.CITIES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllCitiesRelationships() {
    Enumeration<er.geo.ItalianCity> objects = cities().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteCitiesRelationship(objects.nextElement());
    }
  }


  public static ItalianProvince createItalianProvince(EOEditingContext editingContext, Integer codiceRegione
, Integer codiceRipartizione
, String denominazione
, String denominazioneRegione
, String ripartizioneGeografica
, String sigla
) {
    ItalianProvince eo = (ItalianProvince) EOUtilities.createAndInsertInstance(editingContext, _ItalianProvince.ENTITY_NAME);    
		eo.setCodiceRegione(codiceRegione);
		eo.setCodiceRipartizione(codiceRipartizione);
		eo.setDenominazione(denominazione);
		eo.setDenominazioneRegione(denominazioneRegione);
		eo.setRipartizioneGeografica(ripartizioneGeografica);
		eo.setSigla(sigla);
    return eo;
  }

  public static ERXFetchSpecification<ItalianProvince> fetchSpec() {
    return new ERXFetchSpecification<ItalianProvince>(_ItalianProvince.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ItalianProvince> fetchAllItalianProvinces(EOEditingContext editingContext) {
    return _ItalianProvince.fetchAllItalianProvinces(editingContext, null);
  }

  public static NSArray<ItalianProvince> fetchAllItalianProvinces(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ItalianProvince.fetchItalianProvinces(editingContext, null, sortOrderings);
  }

  public static NSArray<ItalianProvince> fetchItalianProvinces(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ItalianProvince> fetchSpec = new ERXFetchSpecification<ItalianProvince>(_ItalianProvince.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<ItalianProvince> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ItalianProvince fetchItalianProvince(EOEditingContext editingContext, String keyName, Object value) {
    return _ItalianProvince.fetchItalianProvince(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ItalianProvince fetchItalianProvince(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ItalianProvince> eoObjects = _ItalianProvince.fetchItalianProvinces(editingContext, qualifier, null);
    ItalianProvince eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ItalianProvince that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ItalianProvince fetchRequiredItalianProvince(EOEditingContext editingContext, String keyName, Object value) {
    return _ItalianProvince.fetchRequiredItalianProvince(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ItalianProvince fetchRequiredItalianProvince(EOEditingContext editingContext, EOQualifier qualifier) {
    ItalianProvince eoObject = _ItalianProvince.fetchItalianProvince(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ItalianProvince that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ItalianProvince localInstanceIn(EOEditingContext editingContext, ItalianProvince eo) {
    ItalianProvince localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
