package com.gar.system.service;

import com.gar.system.model.GarUser;

public interface GarMailService {

	void sendActivationEmail(GarUser user);

	void sendPasswordResetMail(GarUser user);

	void sendCreationEmail(GarUser newUser);

}
