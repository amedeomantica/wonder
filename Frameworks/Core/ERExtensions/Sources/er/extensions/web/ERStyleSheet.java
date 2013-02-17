package er.extensions.web;


/**
 * Simple class that wraps StyleSheet file information
 * @author amedeomantica
 *
 */

public class ERStyleSheet {

	private String _frameworkName;
	private String _fileName;
	private String _mediaType;
	
	public ERStyleSheet(String fileName, String frameworkName, String mediaType) throws Exception {
		
		if(fileName==null) {
			throw new Exception("Stylesheet filename cannot be null");
		}
		_fileName = fileName;
		
		_frameworkName = "app";
		if(frameworkName!=null) {
			_frameworkName = frameworkName;
		}
		
		_mediaType = "all";
		if(mediaType!=null) {
			_mediaType = mediaType;
		}
		
	}
	
	public String frameworkName() {
		return _frameworkName;
	}
	
	public String fileName() {
		return _fileName;
	}
	
	public String mediaType() {
		return _mediaType;
	}
	
}
