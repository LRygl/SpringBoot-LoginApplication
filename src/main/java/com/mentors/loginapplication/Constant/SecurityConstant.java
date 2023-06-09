package com.mentors.loginapplication.Constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 86_400_000; //24Hours Token Expiration time in miliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String TOKEN_ISSUER_NAME = "Get Arrays, LLC";
    public static final String TOKEN_ADMINISTRATION = "User Management Portal";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page/resource";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this resource";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {"/status/**","/user/login","/user/list", "/user/register", "/user/resetpassword/**", "/user/image/**"}; //allowed url to be accessed without permissions
    //public static final String[] PUBLIC_URLS = {"*"}; // All Public
}
