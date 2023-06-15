package com.spring.oauth2.springsecurity.oauth2;

import java.util.Map;

public class OAuth2GitHubUser extends OAuth2UserDetails {

    public OAuth2GitHubUser(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("login");
    }
}
