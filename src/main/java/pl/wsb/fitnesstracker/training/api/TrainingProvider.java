/*
 ____            _            _____ _             _     _      ___ ___ ___ ___ ___
|    \ ___ _____|_|___ ___   |   __| |_ ___ ___  |_|___| |_   | . | . | . |_  |  _|
|  |  | .'|     | | .'|   |  |__   |  _|  _| . | | | -_| '_|  |_  |_  | . |_  | . |
|____/|__,|_|_|_|_|__,|_|_|  |_____|_| |_| |___|_| |___|_,_|  |___|___|___|___|___|
                                               |___|
 */
package pl.wsb.fitnesstracker.training.api;

import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.training.internal.ActivityType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingProvider {

    /**
     * Retrieves all trainings
     *
     * @return All trainings in the database
     */
    List<Training> getAllTrainings();

    /**
     * Retrieves a training based on userIds
     *
     * @param userId Long
     * @return List of all trainings for given userId
     */
    List<Training> getTrainingsByUserId(Long userId);

    /**
     * Retrieves a trainings with the date after specified argument
     *
     * @param date LocalDate
     * @return List of all training with endDate after specified parameter
     */
    List<Training> getCompletedTrainingsAfter(LocalDate date);

    /**
     * Retrieves a training based on their activityType
     *
     * @param activityType ActivityType
     * @return List of all trainings that are specified with given activityType
     */
    List<Training> getTrainingsByActivityType(ActivityType activityType);
    
}
