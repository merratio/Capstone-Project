package com.mp.capstone.project.exception;

/**
 * Thrown when any call to the Auth0 Management API fails —
 * including token fetch, user creation, role resolution, or role assignment.
 *
 * <p>Mirrors the pattern of the existing {@link BlockchainException}
 * in this project.
 */
public class Auth0Exception extends RuntimeException {

    public Auth0Exception(String message) {
        super(message);
    }

    public Auth0Exception(String message, Throwable cause) {
        super(message, cause);
    }
}