package com.gar.system.service.impl;

import static com.gar.system.constants.GarConstants.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gar.system.dto.GarUserDTO;
import com.gar.system.model.GarAuthority;
import com.gar.system.model.GarUser;
import com.gar.system.repository.GarAuthorityRepository;
import com.gar.system.repository.GarUserRepository;
import com.gar.system.security.SecurityUtils;
import com.gar.system.service.GarUserService;
import com.gar.system.utils.RandomUtil;
import com.gar.system.web.vm.ManagedUserVM;

@Service
@Transactional
public class GarUserServiceImpl implements GarUserService {

	private final Logger log = LoggerFactory.getLogger(GarUserServiceImpl.class);

	@Autowired
	private GarUserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public JdbcTokenStore jdbcTokenStore;

	@Autowired
	private GarAuthorityRepository authorityRepository;

	public Optional<GarUser> activateRegistration(String key) {
		log.debug("Activating user for activation key {}", key);
		return userRepository.findOneByActivationKey(key).map(user -> {
			// activate given user for the registration key.
				user.setActivated(true);
				user.setActivationKey(null);
				log.debug("Activated user: {}", user);
				return user;
			});
	}

	public Optional<GarUser> completePasswordReset(String newPassword, String key) {
		log.debug("Reset user password for reset key {}", key);

		return userRepository.findOneByResetKey(key).filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400))).map(user -> {
			user.setPassword(passwordEncoder.encode(newPassword));
			user.setResetKey(null);
			user.setResetDate(null);
			return user;
		});
	}

	public Optional<GarUser> requestPasswordReset(String mail) {
		return userRepository.findOneByEmail(mail).filter(GarUser::isActivated).map(user -> {
			user.setResetKey(RandomUtil.generateResetKey());
			user.setResetDate(Instant.now());
			return user;
		});
	}

	public GarUser createUser(String login, String password, String firstName, String lastName, String email, String imageUrl, String langKey) {

		GarUser newUser = new GarUser();
		GarAuthority authority = authorityRepository.findOne(USER);
		Set<GarAuthority> authorities = new HashSet<>();
		String encryptedPassword = passwordEncoder.encode(password);
		newUser.setLogin(login);
		// new user gets initially a generated password
		newUser.setPassword(encryptedPassword);
		newUser.setFirstName(firstName);
		newUser.setLastName(lastName);
		newUser.setEmail(email);
		newUser.setImageUrl(imageUrl);
		newUser.setLangKey(langKey);
		// new user is not active
		newUser.setActivated(false);
		// new user gets registration key
		newUser.setActivationKey(RandomUtil.generateActivationKey());
		authorities.add(authority);
		newUser.setAuthorities(authorities);
		userRepository.save(newUser);
		log.debug("Created Information for User: {}", newUser);
		return newUser;
	}

	public GarUser createUser(GarUserDTO userDTO) {
		GarUser user = new GarUser();
		user.setLogin(userDTO.getLogin());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());
		user.setImageUrl(userDTO.getImageUrl());
		if (userDTO.getLangKey() == null) {
			user.setLangKey("en"); // default language
		} else {
			user.setLangKey(userDTO.getLangKey());
		}
		if (userDTO.getAuthorities() != null) {
			Set<GarAuthority> authorities = new HashSet<>();
			userDTO.getAuthorities().forEach(authority -> authorities.add(authorityRepository.findOne(authority)));
			user.setAuthorities(authorities);
		}
		String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
		user.setPassword(encryptedPassword);
		user.setResetKey(RandomUtil.generateResetKey());
		user.setResetDate(Instant.now());
		user.setActivated(true);
		userRepository.save(user);
		log.debug("Created Information for User: {}", user);
		return user;
	}

	/**
	 * Update basic information (first name, last name, email, language) for the
	 * current user.
	 *
	 * @param firstName
	 *            first name of user
	 * @param lastName
	 *            last name of user
	 * @param email
	 *            email id of user
	 * @param langKey
	 *            language key
	 * @param imageUrl
	 *            image URL of user
	 */
	public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
		userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);
			user.setLangKey(langKey);
			user.setImageUrl(imageUrl);
			log.debug("Changed Information for User: {}", user);
		});
	}

	/**
	 * Update all information for a specific user, and return the modified user.
	 *
	 * @param userDTO
	 *            user to update
	 * @return updated user
	 */
	public Optional<GarUserDTO> updateUser(GarUserDTO userDTO) {
		return Optional.of(userRepository.findOne(userDTO.getId())).map(user -> {
			user.setLogin(userDTO.getLogin());
			user.setFirstName(userDTO.getFirstName());
			user.setLastName(userDTO.getLastName());
			user.setEmail(userDTO.getEmail());
			user.setImageUrl(userDTO.getImageUrl());
			user.setActivated(userDTO.isActivated());
			user.setLangKey(userDTO.getLangKey());
			Set<GarAuthority> managedAuthorities = user.getAuthorities();
			managedAuthorities.clear();
			userDTO.getAuthorities().stream().map(authorityRepository::findOne).forEach(managedAuthorities::add);
			log.debug("Changed Information for User: {}", user);
			return user;
		}).map(GarUserDTO::new);
	}

	public void deleteUser(String login) {
		jdbcTokenStore.findTokensByUserName(login).forEach(token -> jdbcTokenStore.removeAccessToken(token));
		userRepository.findOneByLogin(login).ifPresent(user -> {
			userRepository.delete(user);
			log.debug("Deleted User: {}", user);
		});
	}

	public void changePassword(String password) {
		userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
			String encryptedPassword = passwordEncoder.encode(password);
			user.setPassword(encryptedPassword);
			log.debug("Changed password for User: {}", user);
		});
	}

	@Transactional(readOnly = true)
	public Page<GarUserDTO> getAllManagedUsers(Pageable pageable) {
		return userRepository.findAllByLoginNot(pageable, ANONYMOUS_USER).map(GarUserDTO::new);
	}

	@Transactional(readOnly = true)
	public Optional<GarUser> getUserWithAuthoritiesByLogin(String login) {
		return userRepository.findOneWithAuthoritiesByLogin(login);
	}

	@Transactional(readOnly = true)
	public GarUser getUserWithAuthorities(Long id) {
		return userRepository.findOneWithAuthoritiesById(id);
	}

	@Transactional(readOnly = true)
	public GarUser getUserWithAuthorities() {
		return userRepository.findOneWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).orElse(null);
	}

	/**
	 * Not activated users should be automatically deleted after 3 days.
	 * <p>
	 * This is scheduled to get fired everyday, at 01:00 (am).
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void removeNotActivatedUsers() {
		List<GarUser> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS));
		for (GarUser user : users) {
			log.debug("Deleting not activated user {}", user.getLogin());
			userRepository.delete(user);
		}
	}

	/**
	 * @return a list of all the authorities
	 */
	public List<String> getAuthorities() {
		return authorityRepository.findAll().stream().map(GarAuthority::getName).collect(Collectors.toList());
	}

	@Override
	public GarUser createUser(ManagedUserVM managedUserVM) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<GarUserDTO> updateUser(ManagedUserVM managedUserVM) {
		// TODO Auto-generated method stub
		return null;
	}

}
