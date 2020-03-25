package com.javainuse.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.security.otp.Totp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

@Component
@WebFilter(urlPatterns = { "/validate" })
public class AuthenticationFilter extends GenericFilterBean {

	private final static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("coontroller enter in Authentication filter");
		HttpServletRequest httpRequest = asHttp(request);
		HttpServletResponse httpResponse = asHttp(response);

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			System.out.println("authentication:"+authentication);
			if (authentication != null) {
				boolean authenticated = SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
				if (authenticated) {
					String parameter = httpRequest.getParameter("otp");
					if (parameter != null) {
						boolean verify;
						try {
							Totp t = new Totp("JAHER7E4UR3YHB57W7BVJMNXRR3CFJDO");
							verify = t.verify(parameter);
							if (verify) {
								System.out.println("OTP Verified in filter class");
							} else {
								
								
								  throw new InternalAuthenticationServiceException(
								  "Unable to authenticate Domain User for provided credentials");
								 
							}
						} catch (Exception e) {
							System.out.println(e);
							Authentication authentication21 = SecurityContextHolder.getContext().getAuthentication();
							System.out.println("authentication21:"+authentication21);
							if(authentication21!=null) {
								System.out.println("authentication21:"+authentication21.getCredentials());
							}
							httpResponse.sendRedirect("/welcome");

						}
					}

					chain.doFilter(request, response);
				} else {
					System.out.println("control comes to false ");
					throw new InternalAuthenticationServiceException(
							"Unable to authenticate Domain User for provided credentials");

				}
			} else {
				chain.doFilter(request, response);
			}
		} catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
			
			System.out.println("control comes to false catch block");
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			System.out.println(authentication);
			SecurityContextHolder.clearContext();
			Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
			System.out.println(authentication1);
			httpResponse.sendRedirect("/welcome");
		} catch (AuthenticationException authenticationException) {
			SecurityContextHolder.clearContext();
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
		} finally {

		}

	}

	private HttpServletRequest asHttp(ServletRequest request) {
		return (HttpServletRequest) request;
	}

	private HttpServletResponse asHttp(ServletResponse response) {
		return (HttpServletResponse) response;
	}
	/**
	 * private boolean postToAuthenticate(HttpServletRequest httpRequest, String
	 * resourcePath) { return
	 * ApiController.AUTHENTICATE_URL.equalsIgnoreCase(resourcePath) &&
	 * httpRequest.getMethod().equals("POST"); }
	 * 
	 * 
	 * private void processUsernamePasswordAuthentication(HttpServletResponse
	 * httpResponse, Optional<String> username, Optional<String> password) throws
	 * IOException { Authentication resultOfAuthentication =
	 * tryToAuthenticateWithUsernameAndPassword(username, password);
	 * SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
	 * httpResponse.setStatus(HttpServletResponse.SC_OK); TokenResponse
	 * tokenResponse = new
	 * TokenResponse(resultOfAuthentication.getDetails().toString()); String
	 * tokenJsonResponse = new ObjectMapper().writeValueAsString(tokenResponse);
	 * httpResponse.addHeader("Content-Type", "application/json");
	 * httpResponse.getWriter().print(tokenJsonResponse); }
	 * 
	 * private Authentication
	 * tryToAuthenticateWithUsernameAndPassword(Optional<String> username,
	 * Optional<String> password) { UsernamePasswordAuthenticationToken
	 * requestAuthentication = new UsernamePasswordAuthenticationToken(username,
	 * password); return tryToAuthenticate(requestAuthentication); }
	 * 
	 * private void processTokenAuthentication(Optional<String> token) {
	 * Authentication resultOfAuthentication = tryToAuthenticateWithToken(token);
	 * SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
	 * }
	 * 
	 * private Authentication tryToAuthenticateWithToken(Optional<String> token) {
	 * PreAuthenticatedAuthenticationToken requestAuthentication = new
	 * PreAuthenticatedAuthenticationToken(token, null); return
	 * tryToAuthenticate(requestAuthentication); }
	 * 
	 * private Authentication tryToAuthenticate(Authentication
	 * requestAuthentication) { Authentication responseAuthentication =
	 * authenticationManager.authenticate(requestAuthentication); if
	 * (responseAuthentication == null || !responseAuthentication.isAuthenticated())
	 * { throw new InternalAuthenticationServiceException( "Unable to authenticate
	 * Domain User for provided credentials"); } logger.debug("User successfully
	 * authenticated"); return responseAuthentication; }
	 */
}