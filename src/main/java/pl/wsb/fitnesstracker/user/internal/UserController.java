/*
 ____            _            _____ _             _     _      ___ ___ ___ ___ ___
|    \ ___ _____|_|___ ___   |   __| |_ ___ ___  |_|___| |_   | . | . | . |_  |  _|
|  |  | .'|     | | .'|   |  |__   |  _|  _| . | | | -_| '_|  |_  |_  | . |_  | . |
|____/|__,|_|_|_|_|__,|_|_|  |_____|_| |_| |___|_| |___|_,_|  |___|___|___|___|___|
                                               |___|
 */
package pl.wsb.fitnesstracker.user.internal;

import org.springframework.web.bind.annotation.*;

import pl.wsb.fitnesstracker.user.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

/**
 * Controller for users.
 */
@RestController
@RequestMapping("/v1/users")
class UserController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;
    private final UserEmailSimpleMapper userEmailSimpleMapper;

    UserController(UserServiceImpl userService, UserMapper userMapper, UserEmailSimpleMapper userEmailSimpleMapper) {
        this.userService = userService;
        this.userEmailSimpleMapper = userEmailSimpleMapper;
        this.userMapper = userMapper;
    }

    /**
     * Get all users
     *
     * @return List of UserDto
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers()
                          .stream()
                          .map(userMapper::toDto)
                          .toList();
    }

    /**
     * Get all users in simple format
     *
     * @return List of UserSimpleDto
     */
    @GetMapping("/simple")
    public List<UserSimpleDto> getAllSimpleUsers() {
        return userService.getAllUsers()
                .stream()
                .map(userMapper::toSimpleDto)
                .toList();
    }

    /**
     * Get all users in detailed format
     *
     * @return List of UserSimpleDto
     */
    @GetMapping("/details")
    public List<UserDetailsDto> getAllDetailedUsers() {
        return userService.getAllUsers()
                .stream()
                .map(userMapper::toDetailsDto)
                .toList();
    }

    /**
     * Get user by email
     *
     * @param email String
     * @return List of UserEmailSimpleDto
     */
    @GetMapping("/email")
    public List<UserEmailSimpleDto> getUserByEmail(@RequestParam String email) {
        return userService.getUsersByEmailPart(email)
                .stream()
                .map(userEmailSimpleMapper::toEmailSimpleDto)
                .toList();
    }

    /**
     * Get user by ID
     *
     * @param userId Long
     * @return UserDto
     */
    @GetMapping("/{userId}")
    public UserDto getUserByUserId(@PathVariable Long userId) {
        return userService.getUser(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User with ID: " + userId + " is not existing."));
    }

    /**
     * Get users born after a given date
     *
     * @param date LocalDate
     * @return List of UserDto
     */
    @GetMapping("/younger/{date}")
    public List<UserDto> findUsersBornAfter(@PathVariable LocalDate date) {
        return userService.getUsersBornAfter(date).stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Add a new user
     *
     * @param userDto UserDto
     * @return User
     */
    @PostMapping
    @ResponseStatus(CREATED)
    public User addUser(@RequestBody UserDto userDto) throws InterruptedException {
        User createdUser = null;
        try{
            createdUser = userService.createUser(userMapper.toEntity(userDto));
        } catch (Exception e) {
            throw new IllegalArgumentException("Not able to add user of ID: " + createdUser.getId() + ".\nError: " + e.getMessage());
        }

        return createdUser;
    }

    /**
     * Delete an existing user
     *
     * @param userId Long
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUserById(userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Not able to delete user of ID: " + userId + ".\nError: " + e.getMessage());
        }
    }

    /**
     * Update an existing user
     *
     * @param userId Long
     * @param userDto UserDto
     * @return User
     */
    @PutMapping("/{userId}")
    @ResponseStatus(OK)
    public User updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        try {
            User userDebug = userService.getUser(userId).orElseThrow(() -> new IllegalArgumentException("User with ID: " + userId + " was not found"));
            User userUpdate = userMapper.toUpdateEntity(userDto, userDebug);
            return userService.updateUser(userUpdate);
        } catch (Exception e) {
            throw new IllegalArgumentException("Not able to update user of ID: " + userId + ".\nError: " + e.getMessage());
        }
    }
}