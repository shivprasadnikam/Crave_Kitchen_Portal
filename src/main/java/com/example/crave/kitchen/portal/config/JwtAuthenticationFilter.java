package com.example.crave.kitchen.portal.config;

import com.example.crave.kitchen.portal.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenService jwtTokenService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Check if Authorization header exists
        if (authHeader == null) {
            logger.debug("No Authorization header found");
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token from Authorization header
        if (authHeader.startsWith("Bearer ")) {
            // Standard format: "Bearer <token>"
            jwt = authHeader.substring(7);
            logger.debug("Extracted token from Bearer Authorization header");
        } else {
            // Direct token format: just the token
            jwt = authHeader;
            logger.debug("Using direct token from Authorization header (no Bearer prefix)");
        }

        try {
            // Extract email from JWT token
            userEmail = jwtTokenService.extractEmail(jwt);
            logger.debug("Extracted email from JWT: {}", userEmail);

            // If email is extracted and no authentication exists in SecurityContext
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Validate JWT token
                if (jwtTokenService.validateToken(jwt, userEmail)) {
                    logger.debug("JWT token validated successfully for user: {}", userEmail);

                    // Create authentication token using user details authorities
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    // Set authentication details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentication set in SecurityContext for user: {}", userEmail);
                } else {
                    logger.warn("JWT token validation failed for user: {}", userEmail);
                }
            }
        } catch (Exception e) {
            // Log the exception but don't throw it to avoid breaking the filter chain
            logger.error("Error processing JWT token", e);
        }

        filterChain.doFilter(request, response);
    }
}