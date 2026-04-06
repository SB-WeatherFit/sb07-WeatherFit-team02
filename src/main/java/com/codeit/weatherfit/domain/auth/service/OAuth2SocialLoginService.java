package com.codeit.weatherfit.domain.auth.service;

import com.codeit.weatherfit.domain.auth.entity.SocialProvider;
import com.codeit.weatherfit.domain.user.entity.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2SocialLoginService {

    User loadOrCreateUser(SocialProvider provider, OAuth2User oAuth2User);
}