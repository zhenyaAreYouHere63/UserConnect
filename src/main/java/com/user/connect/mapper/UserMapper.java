package com.user.connect.mapper;

import com.user.connect.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import com.user.connect.entity.user.User;


@Component
@Mapper
public interface UserMapper {

    UserDto mapUserToUserDto(User user);

    @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "role", constant = "USER")
    User mapUserDtoToUser(UserDto userDto) ;
}
