package Reservista.example.Backend.DAOs;


import Reservista.example.Backend.Models.EntityClasses.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    @Query("SELECT u.isActivated FROM User u WHERE u.email = :email")
    boolean findIsActivatedByEmail(@Param("email") String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);

    @Query("SELECT u.isBlocked FROM User u WHERE u.email = :email")
    boolean findIsBlockedByEmail(@Param("email") String email);
    @Query("SELECT u.email FROM User u")
    List<String> findAllUsersEmails();
}

