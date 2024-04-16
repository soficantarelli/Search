package ar.edu.ubp.das.security;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import ar.edu.ubp.das.beans.UserBean;

public class TokenBasedSecurityContext implements SecurityContext {
	private final boolean secure;
	private UserBean user;

    public TokenBasedSecurityContext(UserBean user, boolean secure) {
        this.user = user;
        this.secure = secure;
    }
    
    public boolean isUserInRole(String role) {
        return this.user.getRole().equals(role);
    }

    public boolean isSecure() {
        return secure;
    }

	@Override
	public Principal getUserPrincipal() {
		return user;
	}

	@Override
	public String getAuthenticationScheme() {
		return "Bearer";
	}

}