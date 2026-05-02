package com.mp.capstone.project.dto.response;

import com.mp.capstone.project.enums.Auth0Role;

public class LoginResponseDTO {

    private String    accessToken;
    private String    tokenType = "Bearer";
    private int       expiresIn;
    private String    employeeId;
    private Auth0Role role;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String accessToken, int expiresIn,
                            String employeeId, Auth0Role role) {
        this.accessToken = accessToken;
        this.expiresIn   = expiresIn;
        this.employeeId  = employeeId;
        this.role        = role;
    }

    public String    getAccessToken()  { return accessToken; }
    public void      setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String    getTokenType()    { return tokenType; }
    public void      setTokenType(String tokenType) { this.tokenType = tokenType; }

    public int       getExpiresIn()    { return expiresIn; }
    public void      setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }

    public String    getEmployeeId()   { return employeeId; }
    public void      setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public Auth0Role getRole()         { return role; }
    public void      setRole(Auth0Role role) { this.role = role; }
}