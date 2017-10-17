package com.gar.system.service;

import java.util.List;
import java.util.Set;

import com.gar.system.dto.GarUserDTO;
import com.gar.system.model.GarAuthority;
import com.gar.system.model.GarUser;

public interface GarUserMapperService {

	GarUserDTO userToUserDTO(GarUser user);

	List<GarUserDTO> usersToUserDTOs(List<GarUser> users);

	GarUser userDTOToUser(GarUserDTO userDTO);

	List<GarUser> userDTOsToUsers(List<GarUserDTO> userDTOs);

	GarUser userFromId(Long id);

	Set<GarAuthority> authoritiesFromStrings(Set<String> strings);

}
