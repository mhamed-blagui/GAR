package com.gar.system.web.rest;

import static com.gar.system.constants.GarConstants.CHECK_ERROR_MESSAGE;
import static com.gar.system.constants.GarConstants.PASSWORD_MAX_LENGTH;
import static com.gar.system.constants.GarConstants.PASSWORD_MIN_LENGTH;
import static com.gar.system.utils.HeaderUtil.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.gar.system.model.GarUser;
import com.gar.system.repository.GarUserRepository;
import com.gar.system.security.SecurityUtils;
import com.gar.system.service.GarMailService;
import com.gar.system.service.GarUserService;
import com.gar.system.dto.GarUserDTO;
import com.gar.system.web.vm.KeyAndPasswordVM;
import com.gar.system.web.vm.ManagedUserVM;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class GarAccountResource {
	
	private final Logger logger = Logger.getLogger(GarAccountResource.class);
	
	@Autowired
	private GarUserRepository userRepository;
	
	@Autowired
	private GarUserService userService;
	
	@Autowired
	private GarMailService mailService;
	
	/**
     * POST  /register : register the user.
     *
     * @param managedUserVM the managed user View Model
     * @return the ResponseEntity with status 201 (Created) if the user is registered or 400 (Bad Request) if the login or email is already in use
     */
    @PostMapping(path = "/register", produces={MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @Timed
    public ResponseEntity registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {

        HttpHeaders textPlainHeaders = new HttpHeaders();
        textPlainHeaders.setContentType(MediaType.TEXT_PLAIN);
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            return new ResponseEntity<>(CHECK_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }
        return userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase())
            .map(user -> new ResponseEntity<>("login already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
            .orElseGet(() -> userRepository.findOneByEmail(managedUserVM.getEmail())
                .map(user -> new ResponseEntity<>("email address already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> {
                    GarUser user = userService
                        .createUser(managedUserVM.getLogin(), managedUserVM.getPassword(),
                            managedUserVM.getFirstName(), managedUserVM.getLastName(),
                            managedUserVM.getEmail().toLowerCase(), managedUserVM.getImageUrl(),
                            managedUserVM.getLangKey());

                    mailService.sendActivationEmail(user);
                    return new ResponseEntity<>(HttpStatus.CREATED);
                })
        );
    }

    /**
     * GET  /activate : activate the registered user.
     *
     * @param key the activation key
     * @return the ResponseEntity with status 200 (OK) and the activated user in body, or status 500 (Internal Server Error) if the user couldn't be activated
     */
    @GetMapping("/activate")
    @Timed
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
        return userService.activateRegistration(key)
            .map(user -> new ResponseEntity<String>(HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    @GetMapping("/authenticate")
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        logger.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /account : get the current user.
     *
     * @return the ResponseEntity with status 200 (OK) and the current user in body, or status 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    @Timed
    public ResponseEntity<GarUserDTO> getAccount() {
        return Optional.ofNullable(userService.getUserWithAuthorities())
            .map(user -> new ResponseEntity<>(new GarUserDTO(user), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) or 500 (Internal Server Error) if the user couldn't be updated
     */
    @PostMapping("/account")
    @Timed
    public ResponseEntity saveAccount(@Valid @RequestBody GarUserDTO userDTO) {
        final String userLogin = SecurityUtils.getCurrentUserLogin();
        Optional<GarUser> existingUser = userRepository.findOneByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            return ResponseEntity.badRequest().headers(createFailureAlert("user-management", "emailexists", "Email already in use")).body(null);
        }
        return userRepository
            .findOneByLogin(userLogin)
            .map(u -> {
                userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                    userDTO.getLangKey(), userDTO.getImageUrl());
                return new ResponseEntity(HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * POST  /account/change_password : changes the current user's password
     *
     * @param password the new password
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) if the new password is not strong enough
     */
    @PostMapping(path = "/account/change_password",
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity changePassword(@RequestBody String password) {
        if (!checkPasswordLength(password)) {
            return new ResponseEntity<>(CHECK_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }
        userService.changePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * POST   /account/reset_password/init : Send an email to reset the password of the user
     *
     * @param mail the mail of the user
     * @return the ResponseEntity with status 200 (OK) if the email was sent, or status 400 (Bad Request) if the email address is not registered
     */
    @PostMapping(path = "/account/reset_password/init",
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity requestPasswordReset(@RequestBody String mail) {
        return userService.requestPasswordReset(mail)
            .map(user -> {
                mailService.sendPasswordResetMail(user);
                return new ResponseEntity<>("email was sent", HttpStatus.OK);
            }).orElse(new ResponseEntity<>("email address not registered", HttpStatus.BAD_REQUEST));
    }

    /**
     * POST   /account/reset_password/finish : Finish to reset the password of the user
     *
     * @param keyAndPassword the generated key and the new password
     * @return the ResponseEntity with status 200 (OK) if the password has been reset,
     * or status 400 (Bad Request) or 500 (Internal Server Error) if the password could not be reset
     */
    @PostMapping(path = "/account/reset_password/finish",
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity<String> finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            return new ResponseEntity<>(CHECK_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }
        return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
              .map(user -> new ResponseEntity<String>(HttpStatus.OK))
              .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
    
    private boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= PASSWORD_MIN_LENGTH &&
            password.length() <= PASSWORD_MAX_LENGTH;
    }
}
