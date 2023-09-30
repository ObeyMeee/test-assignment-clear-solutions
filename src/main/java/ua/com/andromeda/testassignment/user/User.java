package ua.com.andromeda.testassignment.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ua.com.andromeda.testassignment.validation.annotation.BirthDate;
import ua.com.andromeda.testassignment.validation.annotation.PhoneNumber;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "Email cannot be empty")
    @Email(regexp = ".+@.+\\..+")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @Column(nullable = false)
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @Column(nullable = false)
    @PastOrPresent
    @BirthDate
    private LocalDate birthDate;

    private String address;

    @PhoneNumber
    private String phoneNumber;
}
