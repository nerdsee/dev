package org.stoevesand.findow.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Date;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	private Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Get the HTTP Authorization header from the request
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Check if the HTTP Authorization header is present and formatted
		// correctly
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		// Extract the token from the HTTP Authorization header
		String token = authorizationHeader.substring("Bearer".length()).trim();

		try {

			// Validate the token
			String username = extractUserFromToken(token);
			replaceSecurityContext(username, requestContext);

		} catch (Exception e) {
			log.error("JWTExcpetion: " + e.getMessage());
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

	private String extractUserFromToken(String token) throws JWTVerificationException {
		// token =
		// "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Imhhbm5lcyIsImFkbWluIjp0cnVlLCJpc3MiOiJhdXRoMCJ9.E6Z9SYZdYKzfnLLu7IP2s6QRwYOO1XXxZWvpUpwela0";
		String name = "";
		try {
			// secret für auth0 von simprove
			Algorithm algorithm = Algorithm.HMAC256("ZQxW0vT_YNz5Xe3X9iCU63qycwtwQcElyZoR683goSvONaXdIyXfq9TCd75yZpJK");
			// Reusable verifier instance
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("https://simprove.eu.auth0.com/").build();
			DecodedJWT jwt = verifier.verify(token);

			String issuer = jwt.getIssuer();
			Date d = jwt.getExpiresAt();
			name = jwt.getSubject();
			issuer = jwt.getIssuer();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return name;
	}

	private void replaceSecurityContext(final String username, ContainerRequestContext requestContext) throws Exception {

		final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
		requestContext.setSecurityContext(new SecurityContext() {

			@Override
			public Principal getUserPrincipal() {

				return new Principal() {

					@Override
					public String getName() {
						return username;
					}
				};
			}

			@Override
			public boolean isUserInRole(String role) {
				return true;
			}

			@Override
			public boolean isSecure() {
				return currentSecurityContext.isSecure();
			}

			@Override
			public String getAuthenticationScheme() {
				return "Bearer";
			}
		});
	}
}