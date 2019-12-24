/*******************************************************************************
 * Copyright (c) 2018 Softeam
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.authservice.jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.scava.authservice.configuration.JwtAuthenticationConfig;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private final JwtAuthenticationConfig config;
	private final ObjectMapper mapper;

	public JwtUsernamePasswordAuthenticationFilter(JwtAuthenticationConfig config, AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(config.getUrl(), "POST"));
		setAuthenticationManager(authManager);
		this.config = config;
		this.mapper = new ObjectMapper();
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse rsp)
			throws AuthenticationException, IOException, ServletException {
		User u = mapper.readValue(req.getInputStream(), User.class);
		return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword(), Collections.emptyList()));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse rsp, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		Instant now = Instant.now();
		String token = Jwts.builder().setSubject(auth.getName())
				.claim("authorities",
						auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plusSeconds(config.getExpiration())))
				.signWith(SignatureAlgorithm.HS256, config.getSecret().getBytes()).compact();
		rsp.addHeader(config.getHeader(), config.getPrefix() + " " + token);
	}

	private static class User {
		private String username, password;

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}
	}
}
