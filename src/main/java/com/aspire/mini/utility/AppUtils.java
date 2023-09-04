package com.aspire.mini.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class AppUtils {

	public static final String USER_DOES_NOT_EXIST = "User does not exist";
	public static final String MISSING_PARAMETERS = "Missing request parameters";
	public static final String INVALID_CREDENTIALS = "Invalid credentials";
	public static final String USER_ALREADY_EXISTS = "User already exists";

	private final int tokenExpiryHours;
	private final String signingKey;
	private final String tokenIssuer;

	public AppUtils(@Value("${jwt.token.expiry.hours}") int tokenExpiryHours,
			@Value("${jwt.token.signingkey}") String signingKey, @Value("${jwt.token.issuer}") String tokenIssuer) {
		this.tokenExpiryHours = tokenExpiryHours;
		this.signingKey = signingKey;
		this.tokenIssuer = tokenIssuer;
	}

	/**
	 * Validates a JSON Web Token (JWT) and returns its claims if valid.
	 *
	 * @param token The JWT token to be validated.
	 * @return A Jws<Claims> object containing JWT claims if the token is valid;
	 *         otherwise, returns null.
	 * @throws ExpiredJwtException If the token has expired.
	 */
	public Jws<Claims> validateJWTAndReturnClaims(String token) {
		try {
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(signingKey.getBytes())).build()
					.parseClaimsJws(token);
			String issuer = String.valueOf(claims.getBody().get("iss"));
			if (!StringUtils.pathEquals(issuer, tokenIssuer))
				return null;
			return claims;
		} catch (ExpiredJwtException e) {
			return null;
		}
	}

	public int getTokenExpiryHours() {
		return tokenExpiryHours;
	}

	public String getSigningKey() {
		return signingKey;
	}

	public String getTokenIssuer() {
		return tokenIssuer;
	}

}
