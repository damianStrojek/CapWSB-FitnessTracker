package pl.wsb.fitnesstracker.training.internal;

import lombok.Getter;

@Getter
class TrainingUserDto {
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getLastName() {
        return lastName;
    }

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;

    TrainingUserDto(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}