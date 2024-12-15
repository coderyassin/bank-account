package org.yascode.bank_account.security;

public interface JwtHelper {
    boolean isTokenValid(String token);
}
