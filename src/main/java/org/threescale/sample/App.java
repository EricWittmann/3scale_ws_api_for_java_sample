package org.threescale.sample;

import threescale.v3.api.ParameterMap;
import threescale.v3.api.ServerError;

/**
 * Example application using the 3scale API client.
 */
public class App {
	
	public static void main(String[] args) {
		ThreescaleClient client = new ThreescaleClient("localhost", 7171, false);
		client.whitelistAppId("app-1");

		String serviceToken = "12345";
		String serviceId = "foo";

		ParameterMap params = new ParameterMap();
		params.add("app_id", "app-1");
		params.add("app_key", "app_key");
		ParameterMap usage = new ParameterMap();
		usage.add("hits", "1");
		params.add("usage", usage);
		
		// This one should work OK because the app_id has been whitelisted.
		try {
			System.out.println("Calling 3scale server (authrep) for app_id=app-1 (whitelisted)");
			client.authrep(serviceToken, serviceId, params);
			System.out.println("Authrep succeeded!");
		} catch (ServerError e) {
			throw new Error(e);
		}

		// -------------

		// Now try again with an app_id that isn't whitelisted
		params = new ParameterMap();
		params.add("app_id", "app-2");
		params.add("app_key", "app_key");
		usage = new ParameterMap();
		usage.add("hits", "1");
		params.add("usage", usage);
		
		try {
			System.out.println("Calling 3scale server (authrep) for app_id=app-2 (NOT whitelisted)");
			client.authrep(serviceToken, serviceId, params);
			throw new Error("Authrep succeeded unexpectedly!");
		} catch (ServerError e) {
			// This is OK
			System.out.println("Authrep failed as expected with: [" + e.getMessage() + "]");
		}
	}
	
}
