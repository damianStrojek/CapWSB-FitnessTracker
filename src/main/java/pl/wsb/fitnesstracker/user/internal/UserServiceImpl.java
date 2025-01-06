/*
 ____            _            _____ _             _     _      ___ ___ ___ ___ ___
|    \ ___ _____|_|___ ___   |   __| |_ ___ ___  |_|___| |_   | . | . | . |_  |  _|
|  |  | .'|     | | .'|   |  |__   |  _|  _| . | | | -_| '_|  |_  |_  | . |_  | . |
|____/|__,|_|_|_|_|__,|_|_|  |_____|_| |_| |___|_| |___|_,_|  |___|___|___|___|___|
                                               |___|
 */
package pl.wsb.fitnesstracker.user.internal;

import org.springframework.stereotype.Service;

import pl.wsb.fitnesstracker.user.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
class UserServiceImpl implements UserService, UserProvider {

    private final UserRepository userRepository;
    private final Logger log = Logger.getLogger(UserServiceImpl.class.getName());

    UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create a new User
     *
     * @param user User
     */
    @Override
    public User createUser(final User user) {

        log.info("Creating following user:" + user);

        if (user.getId() != null)
            throw new IllegalArgumentException("User has already an ID, update is not permitted!");

        return userRepository.save(user);
    }

    /**
     * Get all Users
     * @return List of Users
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Update an existing User overwrite
     * @param user User
     * @return User
     */
    @Override
    public User updateUser(final User user) {
        if (user.getId() == null)
            throw new IllegalArgumentException("User has NULL id.");
        return userRepository.save(user);
    }

    /**
     * Delete an existing User
     * @param userId Long
     */
    @Override
    public void deleteUserById(final Long userId) {
        log.info("Deleting User with ID " + userId);

        if (UserServiceImpl.this.getUser(userId).isEmpty())
            throw new IllegalArgumentException("There is no user with given ID.");

        userRepository.deleteById(userId);
    }

    /**
     * Get a User by ID
     * @param userId id of the user to be searched
     * @return An {@link Optional} containing the located User, or {@link Optional#empty()} if not found
     */
    @Override
    public Optional<User> getUser(final Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Get a User by part of an email
     * @param emailPart String
     * @return List of Users
     */
    @Override
    public List<User> getUsersByEmailPart(String emailPart) {
        return userRepository.findByEmailPartIgnoreCase(emailPart);
    }

    /**
     * Get all Users born before a given date
     * @param date LocalDate
     * @return List of Users
     */
    @Override
    public List<User> getUsersBornAfter(LocalDate date) {
        return userRepository.findByBirthdateAfter(date);
    }

}