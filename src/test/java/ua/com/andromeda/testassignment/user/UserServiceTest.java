package ua.com.andromeda.testassignment.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.com.andromeda.testassignment.exception.InvalidRangeException;
import ua.com.andromeda.testassignment.exception.InvalidUUIDException;
import ua.com.andromeda.testassignment.exception.UserNotFoundException;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UserServiceTest {
    @MockBean
    UserRepository userRepository;

    @Autowired
    UserService target;

    @Value("${user.min.age}")
    private String minAge;

    User getDefaultUser() {
        User user = new User();
        user.setFirstName("Andrii");
        user.setLastName("Heraskin");
        user.setEmail("andromeda@gmail.com");
        user.setAddress("Peremohy Street 20");
        user.setPhoneNumber("+380678955568");
        return user;
    }

    @Test
    void findById_shouldReturnExistingUser() {
        // config
        UUID id = UUID.randomUUID();
        User expected = getDefaultUser();
        when(userRepository.findById(id)).thenReturn(Optional.of(expected));

        // method invocation
        User actual = target.findById(id.toString());

        // assertions
        assertEquals(expected, actual);
        verify(userRepository).findById(id);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void findById_shouldThrowResourceNotFoundException() {
        // config
        UUID randomId = UUID.randomUUID();
        when(userRepository.findById(randomId)).thenReturn(Optional.empty());

        // method invocation, assertions
        assertThrows(UserNotFoundException.class, () -> target.findById(randomId.toString()));
        verify(userRepository).findById(randomId);
        verify(userRepository, times(1)).findById(randomId);
    }

    @Test
    void findById_shouldThrowInvalidUUIDException() {
        String invalidUUID = "invalid uuid";
        assertThrows(InvalidUUIDException.class, () -> target.findById(invalidUUID));
        verify(userRepository, never()).findById(any());
    }


    @Test
    void save_success_exactAllowedAge() {
        // config
        User userToSave = getDefaultUser();
        LocalDate exactAllowedBirthDate = LocalDate.now().minusYears(Integer.parseInt(minAge));
        userToSave.setBirthDate(exactAllowedBirthDate);

        // method invocation
        target.save(userToSave);

        // assertion
        verify(userRepository, times(1)).save(userToSave);
    }

    @Test
    void delete_shouldThrowInvalidUUIDException() {
        assertThrows(InvalidUUIDException.class, () -> target.delete("invalid uuid"));
    }

    @Test
    void delete_success() {
        UUID id = UUID.randomUUID();
        target.delete(id.toString());
        verify(userRepository, times(1)).deleteById(id);
    }


    @Test
    void findAllByBirthDateBetween_success() {
        LocalDate now = LocalDate.now();
        LocalDate from = now.minusYears(30);
        LocalDate to = now.minusYears(15);
        Pageable pageable = Pageable.unpaged();
        PageImpl<User> expected = new PageImpl<>(List.of(getDefaultUser(), getDefaultUser()));

        when(userRepository.findAllByBirthDateBetweenAnd(from, to, pageable)).thenReturn(expected);

        Page<User> actual = target.findAllByBirthDateBetween(from, to, pageable);

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findAllByBirthDateBetweenAnd(from, to, pageable);
    }

    @Test
    void findAllByBirthDateBetween_shouldThrowInvalidRangeException_fromGreaterThanTo() {
        LocalDate from = LocalDate.now();
        LocalDate to = from.minusYears(15);
        Pageable pageable = Pageable.unpaged();
        assertThrows(
                InvalidRangeException.class,
                () -> target.findAllByBirthDateBetween(from, to, pageable)
        );
    }

    @Test
    void findAllByBirthDateBetween_shouldThrowInvalidRangeException_fromIsNull() {
        LocalDate to = LocalDate.now();
        Pageable pageable = Pageable.unpaged();
        assertThrows(
                InvalidRangeException.class,
                () -> target.findAllByBirthDateBetween(null, to, pageable)
        );
    }

    @Test
    void partialUpdate_success_emptyFields() {
        // config
        User expected = getDefaultUser();
        UUID id = UUID.randomUUID();
        expected.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(expected));
        when(userRepository.save(any(User.class))).thenReturn(expected);

        // method invocation
        User actual = target.partialUpdate(id.toString(), Collections.emptyMap());

        // assertions
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(expected, actual);
    }

    @Test
    void partialUpdate_success_notEmptyFields() {
        // config
        User userToUpdate = getDefaultUser();
        UUID id = UUID.randomUUID();
        userToUpdate.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(userToUpdate));
        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        String newFirstName = "Taras";
        String newLastName = "Shevchenko";
        Map<String, Object> fields = Map.of(
                "firstName", newFirstName,
                "lastName", newLastName
        );

        // method invocation
        User actual = target.partialUpdate(id.toString(), fields);

        // assertions
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(newFirstName, actual.getFirstName());
        assertEquals(newLastName, actual.getLastName());
        assertEquals(userToUpdate.getEmail(), actual.getEmail());
        assertEquals(userToUpdate.getAddress(), actual.getAddress());
        assertEquals(userToUpdate.getBirthDate(), actual.getBirthDate());
        assertEquals(userToUpdate.getPhoneNumber(), actual.getPhoneNumber());
    }

    @Test
    void partialUpdate_success_nonExistingFields() {
        // config
        User userToUpdate = getDefaultUser();
        UUID id = UUID.randomUUID();
        userToUpdate.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(userToUpdate));
        when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);
        Map<String, Object> fields = Map.of("nonExisting", "nonExistingValue");

        // method invocation
        User actual = target.partialUpdate(id.toString(), fields);

        // assertions
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(userToUpdate);
        assertEquals(userToUpdate, actual);
    }
}