package com.gar.system.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gar.system.dto.GarUserDTO;
import com.gar.system.model.GarAuthority;
import com.gar.system.model.GarUser;
import com.gar.system.service.GarUserMapperService;

@Service
public class GarUserMapperServiceImpl implements GarUserMapperService {

	@Override
	public GarUserDTO userToUserDTO(GarUser user) {
        return new GarUserDTO(user);
    }

	@Override
    public List<GarUserDTO> usersToUserDTOs(List<GarUser> users) {
        return users.stream()
            .filter(Objects::nonNull)
            .map(this::userToUserDTO)
            .collect(Collectors.toList());
    }

	@Override
    public GarUser userDTOToUser(GarUserDTO userDTO) {
        if (userDTO == null) {
            return null;
        } else {
        	GarUser user = new GarUser();
            user.setId(userDTO.getId());
            user.setLogin(userDTO.getLogin());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setImageUrl(userDTO.getImageUrl());
            user.setActivated(userDTO.isActivated());
            user.setLangKey(userDTO.getLangKey());
            Set<GarAuthority> authorities = this.authoritiesFromStrings(userDTO.getAuthorities());
            if(authorities != null) {
                user.setAuthorities(authorities);
            }
            return user;
        }
    }

	@Override
    public List<GarUser> userDTOsToUsers(List<GarUserDTO> userDTOs) {
        return userDTOs.stream()
            .filter(Objects::nonNull)
            .map(this::userDTOToUser)
            .collect(Collectors.toList());
    }

	@Override
    public GarUser userFromId(Long id) {
        if (id == null) {
            return null;
        }
        GarUser user = new GarUser();
        user.setId(id);
        return user;
    }

	@Override
    public Set<GarAuthority> authoritiesFromStrings(Set<String> strings) {
        return strings.stream().map(string -> {
        	GarAuthority auth = new GarAuthority();
            auth.setName(string);
            return auth;
        }).collect(Collectors.toSet());
    }
}
