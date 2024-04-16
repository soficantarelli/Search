package ar.edu.ubp.das.security;

import java.io.IOException;
import java.security.Key;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;

import ar.edu.ubp.das.beans.UserBean;
import ar.edu.ubp.das.logger.MyLogger;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    
	public static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    private MyLogger logger;
	
	public AuthenticationFilter() {
		this.logger = new MyLogger(this.getClass().getSimpleName());
	}

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            
            UserBean user = authenticationToken(authorizationHeader.substring(7));

            boolean isSecure = requestContext.getSecurityContext().isSecure();
            SecurityContext securityContext = new TokenBasedSecurityContext(user, isSecure);
            requestContext.setSecurityContext(securityContext);
            requestContext.setProperty("id", user.getIdUser());
			requestContext.setProperty("rol", user.getRole());
			
			
			
			this.logger.log(MyLogger.INFO, "Auth");
            
            return;
        } else {
        	this.logger.log(MyLogger.ERROR, "Auth: Error");
        	requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
        }
    }
    
    private UserBean authenticationToken(String token) throws IOException {
    	Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
    	
    	UserBean user = new UserBean();
    	user.setIdUser((Integer) jws.getBody().get("id"));
    	user.setRole(jws.getBody().get("role").toString());

		return user; 	
    }
}
