package org.example.userserviceandmanagement.dtos;

import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import org.example.userserviceandmanagement.models.Role;
import org.example.userserviceandmanagement.models.User;

import java.util.List;

@Getter
@Setter
public class UserDto {
    private String name;
    private String email;
    @ManyToMany
    private List<Role> roles;
    private boolean isEmailVerified;

    public static UserDto from(User user) {
        if (user == null) return null;

        UserDto userDto = new UserDto();
        userDto.email = user.getEmail();
        userDto.name = user.getUserName();
        userDto.roles = user.getRoles();
        userDto.isEmailVerified = user.isVerified();

        return userDto;
    }
}