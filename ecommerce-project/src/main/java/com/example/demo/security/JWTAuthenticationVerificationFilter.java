package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.security.properties.SecurityConfigProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthenticationVerificationFilter extends BasicAuthenticationFilter {

    private final SecurityConfigProperties securityConfigProperties;

    public JWTAuthenticationVerificationFilter(AuthenticationManager authenticationManager,
                                               SecurityConfigProperties securityConfigProperties) {
        super(authenticationManager);
        this.securityConfigProperties = securityConfigProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(securityConfigProperties.getHeaderString());

        if (header == null || !header.startsWith(securityConfigProperties.getTokenPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(securityConfigProperties.getHeaderString());
        if (token != null) {
            String user = JWT.require(Algorithm.HMAC512(securityConfigProperties.getSecret().getBytes())).build()
                    .verify(token.replace(securityConfigProperties.getTokenPrefix(), ""))
                    .getSubject();
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }
}
