package org.task.authenticify.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import org.task.authenticify.dto.user.UserDto;
import org.task.authenticify.entity.user.User;


@Component
@Mapper
public interface UserMapper {

    UserDto mapUserToUserDto(User user);

    @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "role", constant = "USER")
    User mapUserDtoToUser(UserDto userDto) ;
}
