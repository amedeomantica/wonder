// Generated by the WOLips Templateengine Plug-in at 09.03.2006 16:24:23

import org.apache.log4j.Logger;

import er.ajax.json.JSONRequestHandler;
import er.extensions.appserver.ERXApplication;

public class Application extends ERXApplication {
	static Logger log = Logger.getLogger(Application.class);

	public static void main(String argv[]) {
		ERXApplication.main(argv, Application.class);
	}

	public Application() {
		super();
		log.info("Welcome to " + this.name() + " !");
		/* ** put your initialization code in here ** */
		setAllowsConcurrentRequestHandling(true);
		setDefaultRequestHandler(requestHandlerForKey(directActionRequestHandlerKey()));
		JSONRequestHandler requestHandler = JSONRequestHandler.register();
		requestHandler.registerService("exampleService", new ExampleService());
	}
}