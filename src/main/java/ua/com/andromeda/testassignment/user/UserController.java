package ua.com.andromeda.testassignment.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.andromeda.testassignment.dto.Dto;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<Dto<User>> findById(@PathVariable String userId) {
        User foundedUser = userService.findById(userId);
        Dto<User> body = new Dto<>(foundedUser);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/search/birthDate/between")
    public ResponseEntity<Dto<Page<User>>> findAllByBirthDateBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> foundedUsers = userService.findAllByBirthDateBetween(from, to, pageable);
        Dto<Page<User>> body = new Dto<>(foundedUsers);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<Dto<User>> save(@RequestBody @Valid User user) {
        Dto<User> body = saveUser(user);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/users/" + body.data().getId());
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    private Dto<User> saveUser(User user) {
        User savedUser = userService.save(user);
        return new Dto<>(savedUser);
    }

    @PutMapping
    public ResponseEntity<Dto<User>> update(@RequestBody @Valid User user) {
        Dto<User> body = saveUser(user);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Dto<User>> partialUpdate(@PathVariable String userId,
                                                   @RequestBody Map<String, Object> fields) {
        User savedUser = userService.partialUpdate(userId, fields);
        Dto<User> body = new Dto<>(savedUser);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String userId) {
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
