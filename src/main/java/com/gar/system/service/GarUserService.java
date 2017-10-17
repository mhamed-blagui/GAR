package com.gar.system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gar.system.dto.GarUserDTO;
import com.gar.system.model.GarUser;
import com.gar.system.web.vm.ManagedUserVM;

public interface GarUserService {

	GarUser createUser(String login, String password, String firstName, String lastName, String lowerCase, String imageUrl, String langKey);

	GarUser getUserWithAuthorities();

	void changePassword(String password);

	Optional<GarUser> activateRegistration(String key);

	void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl);

	Optional<GarUser> completePasswordReset(String newPassword, String key);

	Optional<GarUser> requestPasswordReset(String mail);

	Page<GarUserDTO> getAllManagedUsers(Pageable pageable);

	List<String> getAuthorities();

	Optional<GarUser> getUserWithAuthoritiesByLogin(String login);

	void deleteUser(String login);

	GarUser createUser(ManagedUserVM managedUserVM);

	Optional<GarUserDTO> updateUser(ManagedUserVM managedUserVM);

}
