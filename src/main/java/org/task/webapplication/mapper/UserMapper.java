package org.task.webapplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import org.task.webapplication.dto.UserDto;
import org.task.webapplication.entity.User;


@Component
@Mapper
public interface UserMapper {

    UserDto mapUserToUserDto(User user);

    @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID())")
    User mapUserDtoToUser(UserDto userDto) ;
}
