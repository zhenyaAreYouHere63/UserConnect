package org.task.webapplication.auth;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.task.webapplication.dto.UserDto;
import org.task.webapplication.entity.User;
import org.task.webapplication.jwt.JwtUtil;
import org.task.webapplication.mapper.UserMapper;
import org.task.webapplication.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final JwtUtil jwtUtil;

    public void login(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(),
                        request.password()
                )
        );

        User principal = (User) authentication.getPrincipal();

        userRepository.save(principal);

        UserDto userDto = userMapper.mapUserToUserDto(principal);
        String token = jwtUtil.generateToken(userDto.email());
    }
}
