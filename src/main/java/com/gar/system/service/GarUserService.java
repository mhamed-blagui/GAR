package com.gar.system.service;

import java.util.Optional;

import com.gar.system.model.GarUser;

public interface GarUserService {

	GarUser createUser(String login, String password, String firstName, String lastName, String lowerCase, String imageUrl, String langKey);

	GarUser getUserWithAuthorities();

	void changePassword(String password);

	Optional<GarUser> activateRegistration(String key);

	void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl);

	Optional<GarUser> completePasswordReset(String newPassword, String key);

	Optional<GarUser> requestPasswordReset(String mail);

}
