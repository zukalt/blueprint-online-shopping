package com.deepthought.services.dto;



import com.deepthought.services.model.User;
import com.deepthought.services.model.UserSession;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DtoMapper {

    public abstract User toUser(UserRegistrationRequest request);
    @Named("toUserDto")
    public abstract UserDto toUserDto(User user);

    @IterableMapping(qualifiedByName = "toUserDto")
    public abstract List<UserDto> toUserDto(List<User> users);

    public abstract UserSessionDto toSessionDto(UserSession session);
}
