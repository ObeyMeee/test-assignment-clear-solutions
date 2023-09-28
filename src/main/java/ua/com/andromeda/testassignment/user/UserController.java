package ua.com.andromeda.testassignment.user;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.andromeda.testassignment.dto.Dto;

import java.util.Calendar;
import java.util.List;
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

    @GetMapping("birthDate/between")
    public ResponseEntity<Dto<List<User>>> findAllByBirthDateBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Calendar from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Calendar to) {
        List<User> foundedUsers = userService.findAllByBirthDateBetween(from, to);
        Dto<List<User>> body = new Dto<>(foundedUsers);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<Dto<User>> save(@RequestBody User user) {
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
    public ResponseEntity<Dto<User>> update(@RequestBody User user) {
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
    public ResponseEntity<Void> delete(@PathVariable String userId) {
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
