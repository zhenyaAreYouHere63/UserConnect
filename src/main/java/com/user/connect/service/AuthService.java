package com.user.connect.service;

import com.user.connect.dto.auth.CredentialsDto;
import com.user.connect.entity.token.TokenPair;

public interface AuthService {

    TokenPair login(CredentialsDto credentialsDto);

    void logout(String email);
}
