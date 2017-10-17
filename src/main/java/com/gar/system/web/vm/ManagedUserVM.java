package com.gar.system.web.vm;

import static com.gar.system.constants.GarConstants.PASSWORD_MAX_LENGTH;
import static com.gar.system.constants.GarConstants.PASSWORD_MIN_LENGTH;

import java.time.Instant;
import java.util.Set;

import javax.validation.constraints.Size;

import lombok.Getter;

import com.gar.system.dto.GarUserDTO;

/**
 * View Model extending the UserDTO, which is meant to be used in the user management UI.
 */
@Getter
public class ManagedUserVM extends GarUserDTO {

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    public ManagedUserVM(Long id, String login, String password, String firstName, String lastName,
                         String email, boolean activated, String imageUrl, String langKey,
                         String createdBy, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate,
                        Set<String> authorities) {

        super(id, login, firstName, lastName, email, activated, imageUrl, langKey,
            createdBy, createdDate, lastModifiedBy, lastModifiedDate,  authorities);

        this.password = password;
    }

    @Override
    public String toString() {
        return "ManagedUserVM{" +
            "} " + super.toString();
    }
}
