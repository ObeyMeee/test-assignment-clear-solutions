package ua.com.andromeda.testassignment.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ua.com.andromeda.testassignment.user.exception.IllegalAgeException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Value("${user.min.age}")
    private String minAge;

    public User findById(String id) {
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("User with id='" + id + "' not found"));
    }

    public User save(@Valid User userToSave) {
        validateAge(userToSave);
        return userRepository.save(userToSave);
    }

    public void delete(String id) {
        userRepository.deleteById(UUID.fromString(id));
    }

    private void validateAge(@Valid User userToSave) {
        LocalDate userBirthDate = userToSave.getBirthDate();
        LocalDate now = LocalDate.now();
        long userFullYears = ChronoUnit.YEARS.between(userBirthDate, now);
        if (userFullYears < Integer.parseInt(minAge)) {
            throw new IllegalAgeException("You must be at least " + minAge + " years old");
        }
    }

    public List<User> findAllByBirthDateBetween(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Invalid range. 'From' date must be less than 'to'");
        }
        return userRepository.findAllByBirthDateBetweenAnd(from, to);
    }

    @SneakyThrows
    public User partialUpdate(String id, Map<String, Object> fields) {
        User foundedUser = findById(id);
        return objectMapper.updateValue(foundedUser, fields);
    }
}
