package com.mentors.loginapplication.Repository;


import com.mentors.loginapplication.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserById(Long id);
    User findUserByUserEmail(String userEmail);

}
