package pl.wsb.fitnesstracker.user.api;

import jakarta.annotation.Nullable;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

/**
 * UserDetailsDto is a representation of Detailed User entity.
 */
public record UserDetailsDto(String firstName, LocalDate birthDate, String email) { }
