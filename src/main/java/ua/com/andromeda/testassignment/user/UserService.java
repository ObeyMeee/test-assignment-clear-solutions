package ua.com.andromeda.testassignment.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ua.com.andromeda.testassignment.exception.InvalidRangeException;
import ua.com.andromeda.testassignment.exception.InvalidUUIDException;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;


    public User findById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return userRepository.findById(uuid)
                    .orElseThrow(() -> new ResourceNotFoundException("User with id='" + id + "' not found"));
        } catch (IllegalArgumentException ex) {
            throw new InvalidUUIDException();
        }
    }

    public User save(@Valid User userToSave) {
        return userRepository.save(userToSave);
    }

    public void delete(String id) {
        try {
            userRepository.deleteById(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new InvalidUUIDException();
        }
    }

    public Page<User> findAllByBirthDateBetween(LocalDate from, LocalDate to, Pageable pageable) {
        if (from == null || from.isAfter(to)) {
            throw new InvalidRangeException("'From' date must be less than 'to'");
        }
        return userRepository.findAllByBirthDateBetweenAnd(from, to, pageable);
    }

    @SneakyThrows
    public User partialUpdate(String id, Map<String, Object> fields) {
        User foundedUser = findById(id);
        return objectMapper.updateValue(foundedUser, fields);
    }
}
