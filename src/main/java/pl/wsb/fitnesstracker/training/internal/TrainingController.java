/*
 ____            _            _____ _             _     _      ___ ___ ___ ___ ___
|    \ ___ _____|_|___ ___   |   __| |_ ___ ___  |_|___| |_   | . | . | . |_  |  _|
|  |  | .'|     | | .'|   |  |__   |  _|  _| . | | | -_| '_|  |_  |_  | . |_  | . |
|____/|__,|_|_|_|_|__,|_|_|  |_____|_| |_| |___|_| |___|_,_|  |___|___|___|___|___|
                                               |___|
 */
package pl.wsb.fitnesstracker.training.internal;

import pl.wsb.fitnesstracker.training.api.*;
import pl.wsb.fitnesstracker.user.api.UserNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/v1/trainings")
class TrainingController {

    private final TrainingProvider trainingProvider;
    private final TrainingService trainingService;
    private final TrainingMapper trainingMapper;

    TrainingController(TrainingProvider trainingProvider, TrainingService trainingService, TrainingMapper trainingMapper) {
        this.trainingProvider = trainingProvider;
        this.trainingService = trainingService;
        this.trainingMapper = trainingMapper;
    }

    @GetMapping
    List<TrainingDto> getAllTrainings() {
        return trainingProvider.getAllTrainings().stream()
                .map(trainingMapper::toTrainingDto)
                .toList();
    }

    @GetMapping("/{userId}")
    List<TrainingDto> getTrainingsByUser(@PathVariable Long userId) {
        return trainingProvider.getTrainingsByUserId(userId).stream()
                .map(trainingMapper::toTrainingDto)
                .toList();
    }

    @GetMapping("/completed/{date}")
    List<TrainingDto> getCompletedTrainingsAfter(@PathVariable LocalDate date) {
        return trainingProvider.getCompletedTrainingsAfter(date).stream()
                .map(trainingMapper::toTrainingDto)
                .toList();
    }

    @GetMapping("/activityType")
    List<TrainingDto> getTrainingsByActivityType(@RequestParam ActivityType activityType) {
        return trainingProvider.getTrainingsByActivityType(activityType).stream()
                .map(trainingMapper::toTrainingDto)
                .toList();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    TrainingDto createTraining(@RequestBody TrainingUpdateDto training) {
        return trainingMapper.toTrainingDto(trainingService.createTraining(training));
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    TrainingDto updateTraining(@PathVariable Long id, @RequestBody TrainingUpdateDto training) {
        return trainingMapper.toTrainingDto(trainingService.updateTraining(id, training));
    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(TrainingNotFoundException.class)
    ResponseEntity<String> handleTrainingNotFoundException(TrainingNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
    }
}
