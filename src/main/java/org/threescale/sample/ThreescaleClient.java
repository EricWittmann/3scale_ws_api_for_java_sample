package org.threescale.sample;

import java.util.HashSet;
import java.util.Set;

import threescale.v3.api.AuthorizeResponse;
import threescale.v3.api.ParameterMap;
import threescale.v3.api.ServerError;
import threescale.v3.api.ServiceApi;
import threescale.v3.api.impl.ServiceApiDriver;

/**
 * A sample three scale client (wrapper around a service driver in the 3scale
 * API java library).
 */
public class ThreescaleClient {

	private final ServiceApi api;
	private final Set<String> whitelist = new HashSet<String>();

	/**
	 * Constructor.
	 * @param host
	 * @param port
	 * @param useHttps
	 */
	public ThreescaleClient(String host, int port, boolean useHttps) {
		this.api = ServiceApiDriver.createApi(host, port, useHttps);
	}

	/**
	 * Adds an appId to the whitelist.
	 * @param appId
	 */
	public void whitelistAppId(String appId) {
		this.whitelist.add(appId);
	}


	public AuthorizeResponse authrep(String serviceToken, String serviceId, ParameterMap metrics) throws ServerError {
		AuthorizeResponse response;
		try {
			response = this.api.authrep(serviceToken, serviceId, metrics);
			return response;
		} catch (ServerError e) {
			// If the 3scale server couldn't be reached, AND the app_id is whitelisted, then return a "OK" response.  Note that
			// in this case, the metrics/reporting information will be lost.
			if (e.getMessage().contains("Connection refused") && this.whitelist.contains(metrics.getStringValue("app_id"))) {
				// Fake the response, since we didn't get one from the 3scale server.
				response = new AuthorizeResponse(200, "<status><authorized>true</authorized><plan>Basic</plan></status>");
				return response;
			} else {
				throw e;
			}
		}
	}

}
