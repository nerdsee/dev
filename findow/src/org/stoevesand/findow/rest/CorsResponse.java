package org.stoevesand.findow.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsResponse implements ContainerResponseFilter {
	public void filter(ContainerRequestContext req, ContainerResponseContext res) throws IOException {
		MultivaluedMap<String, Object> headers = res.getHeaders();
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
		headers.add("Access-Control-Allow-Headers", "Content-Type");
	}
}
