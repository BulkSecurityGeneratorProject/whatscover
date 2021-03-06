package com.whatscover.service;

import com.whatscover.config.Constants;
import com.whatscover.domain.Authority;
import com.whatscover.domain.User;
import com.whatscover.repository.AuthorityRepository;
import com.whatscover.repository.UserRepository;
import com.whatscover.repository.search.UserSearchRepository;
import com.whatscover.security.AuthoritiesConstants;
import com.whatscover.security.SecurityUtils;
import com.whatscover.service.dto.UserDTO;
import com.whatscover.service.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SocialService socialService;

    private final UserSearchRepository userSearchRepository;

    private final AuthorityRepository authorityRepository;

    @Autowired
    private CustomerProfileService customerProfileService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SocialService socialService, UserSearchRepository userSearchRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.socialService = socialService;
        this.userSearchRepository = userSearchRepository;
        this.authorityRepository = authorityRepository;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userSearchRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
       log.debug("Reset user password for reset key {}", key);

       return userRepository.findOneByResetKey(key)
           .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
           .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                return user;
           });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmail(mail)
            .filter(User::getActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                return user;
            });
    }

    public User createUser(String login, String password, String firstName, String lastName, String email,
        String imageUrl, String langKey) {

        User newUser = new User();
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
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
        userSearchRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }


    /**
     * Create user only by knowing firstName, lastName and Email.
     * Normally this is done by administrator where given minimal information regarding the user.
     * User login is user email.
     *
     * @param firstName
     * @param lastName
     * @param email
     * @return
     */
    public User createUser(String firstName, String lastName, String email, String password, String role) {
        User user = new User();
        user.setLogin(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        user.setActivated(false);
        user.setActivationKey(RandomUtil.generateActivationKey());
        user.setLangKey("en");

        Set<Authority> authorities = new HashSet<>();
        Authority authority = authorityRepository.findOne(role);
        authorities.add(authority);
        user.setAuthorities(authorities);

        user = userRepository.save(user);
        userSearchRepository.save(user);
        log.debug("Created Information for User: {}", user);

        return user;
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
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
            Set<Authority> authorities = new HashSet<>();
            userDTO.getAuthorities().forEach(
                authority -> authorities.add(authorityRepository.findOne(authority))
            );
            user.setAuthorities(authorities);
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        userRepository.save(user);
        userSearchRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Create user only by knowing firstName, lastName and Email.
     * Normally this is done by administrator where given minimal information regarding the user.
     * User login is user email.
     *
     * @param firstName
     * @param lastName
     * @param email
     * @return
     */
    public User createUser(String firstName, String lastName, String email, String password) {
        User user = new User();
        user.setLogin(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        user.setActivated(false);
        user.setActivationKey(RandomUtil.generateActivationKey());
        user.setLangKey("en");

        Set<Authority> authorities = new HashSet<>();
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
        authorities.add(authority);
        user.setAuthorities(authorities);

        user = userRepository.save(user);
        userSearchRepository.save(user);
        log.debug("Created Information for User: {}", user);

        return user;
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user
     * @param lastName last name of user
     * @param email email id of user
     * @param langKey language key
     * @param imageUrl image URL of user
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setLangKey(langKey);
            user.setImageUrl(imageUrl);
            userSearchRepository.save(user);
            log.debug("Changed Information for User: {}", user);
        });
    }

    public void updateUser(String login, String firstName, String lastName, String email, String langKey, String imageUrl) {
        userRepository.findOneByLogin(login).ifPresent(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setLangKey(langKey);
            user.setImageUrl(imageUrl);
            userSearchRepository.save(user);
            log.debug("Changed Information for User: {}", user);
        });
    }

    public void updateUserByEmail(String firstName, String lastName, String email, String langKey, String imageUrl) {
        userRepository.findOneByEmail(email).ifPresent(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setLangKey(langKey);
            user.setImageUrl(imageUrl);
            userSearchRepository.save(user);
            log.debug("Changed Information for User: {}", user);
        });
    }

    public boolean checkUserExistByLogin(String login) {
    	return userRepository.findOneByLogin(login).isPresent();
    }
    /**
     * Find user by login
     * @param login
     * @return User object if exist, null otherwise
     */
    public User findUserByLogin(String login) {
    	return userRepository.findOneByLogin(login).get();
    }
    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update
     * @return updated user
     */
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository
            .findOne(userDTO.getId()))
            .map(user -> {
                user.setLogin(userDTO.getLogin());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setEmail(userDTO.getEmail());
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO.getAuthorities().stream()
                    .map(authorityRepository::findOne)
                    .forEach(managedAuthorities::add);
                userSearchRepository.save(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserDTO::new);
    }

    public void deleteUser(String login) {
        // delete Customer Profile linked to user Id
        userRepository.findOneByLogin(login).ifPresent(user -> {
            customerProfileService.deleteByUserId(user.getId());
        });

        userRepository.findOneByLogin(login).ifPresent(user -> {
            socialService.deleteUserSocialConnection(user.getLogin());
            userRepository.delete(user);
            userSearchRepository.delete(user);
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
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities(Long id) {
        return userRepository.findOneWithAuthoritiesById(id);
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        return userRepository.findOneWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).orElse(null);
    }


    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
            userSearchRepository.delete(user);
        }
    }

    /**
     * @return a list of all the authorities
     */
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    public boolean checkUserExistByEmail(String email) {
        return userRepository.findOneByEmail(email).isPresent();
    }

    /**
     * Find user by email
     * @param email
     * @return User object if exist, null otherwise
     */
    public User findUserByEmail(String email) {
        return userRepository.findOneByEmail(email).get();
    }
}
