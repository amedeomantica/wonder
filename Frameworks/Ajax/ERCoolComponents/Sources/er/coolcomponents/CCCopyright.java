package er.coolcomponents;

import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOComponent;
import com.webobjects.foundation.NSTimestamp;


/**
 * Return the copyright date indicator (es. &copy; 2008-2009)
 * 
 * @author Amedeo Mantica
 * @binding startYear the copyright start year. If it is equal to actual year the results will not have the start-end interval. 
 */
public class CCCopyright extends WOComponent {
    private static Logger log = Logger.getLogger(CCCopyright.class);
	protected Integer startYear;
	protected Integer currentYear;
	
	public CCCopyright(WOContext context) {
        super(context);     
    }
	
	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}
	
	@Override
	public boolean isStateless() {
		return true;
	}
	
	@Override
	public void reset() {
		startYear=null;
		currentYear=null;
	}
	
	protected Integer currentYear() {
		if (currentYear==null) {
			NSTimestamp myNSTimestamp = new NSTimestamp();
			GregorianCalendar myCalendar = new GregorianCalendar();
			myCalendar.setTime(myNSTimestamp);
			currentYear = myCalendar.get(GregorianCalendar.YEAR);
		}
		return currentYear;
	}
    
	public String copy() throws Exception {
		
		if ( startYear() != null ) {
			if ( startYear().equals(currentYear()) ) {
				return Integer.toString(startYear());
			} else {
				return Integer.toString(startYear()) + " - " + Integer.toString(currentYear());
			}
		}
		
		return Integer.toString(currentYear());
		
		
	}
	
	protected Integer startYear() throws Exception {
		if (startYear == null) {
			if (canGetValueForBinding("startYear")) {
				Object sy = valueForBinding("startYear");
				if ( sy instanceof String) {
					this.startYear = new Integer((String)sy).intValue();
				} else if ( sy instanceof Integer) {
					this.startYear = (Integer) sy;
				} else {
					throw new Exception("Unable to set copyright startyear");
				}
			}
		}
		return startYear;
	}
    
}