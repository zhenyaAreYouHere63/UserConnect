package org.task.authenticify.service;

import org.task.authenticify.dto.auth.CredentialsDto;
import org.task.authenticify.entity.token.TokenPair;

public interface AuthService {

    TokenPair login(CredentialsDto credentialsDto);

    void logout(String email);
}
