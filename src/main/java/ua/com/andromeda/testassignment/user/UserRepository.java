package ua.com.andromeda.testassignment.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    @Query("FROM User u WHERE u.birthDate BETWEEN :from AND :to")
    List<User> findAllByBirthDateBetweenAnd(LocalDate from, LocalDate to);
}
