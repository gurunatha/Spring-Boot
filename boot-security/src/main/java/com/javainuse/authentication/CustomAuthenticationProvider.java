package com.javainuse.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.javainuse.model.User;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static List<User> users = new ArrayList();

    public CustomAuthenticationProvider() {
        users.add(new User("erin", "123", "ROLE_ADMIN",false));
        users.add(new User("mike", "234", "ROLE_ADMIN",true));
    }

    public static List<User> getUsers() {
		return users;
	}

	public static void setUsers(List<User> users) {
		CustomAuthenticationProvider.users = users;
	}

	@Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String name = authentication.getName();
        System.out.println("CustomAuthenticationProvider :"+name);
        Object credentials = authentication.getCredentials();
    
        if (!(credentials instanceof String)) {
            return null;
        }
        String password = credentials.toString();
        System.out.println("CustomAuthenticationProvider:"+password);
        Optional<User> userOptional = users.stream()
                                           .filter(u -> u.match(name, password))
                                           .findFirst();
    
        if (!userOptional.isPresent()) {
        	  System.out.println("CustomAuthenticationProvider:"+userOptional.isPresent());
            throw new BadCredentialsException("Authentication failed for " + name);
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(userOptional.get().getRole()));
        Authentication auth = new
                UsernamePasswordAuthenticationToken("mike", password, grantedAuthorities);
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

   
}