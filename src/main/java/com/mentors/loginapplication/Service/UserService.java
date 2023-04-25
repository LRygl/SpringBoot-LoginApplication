package com.mentors.loginapplication.Service;

import com.mentors.loginapplication.Exception.EmailExistsException;
import com.mentors.loginapplication.Model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    List<User> getAllUnpagedUsers();
    User getUserById(Long userId);

    User register(String firstName, String lastName, String email) throws EmailExistsException, IOException;
    User addNewUser(String firstName, String lastName, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws EmailExistsException, IOException;
    void addAdminUser(String firstName, String lastName, String email) throws IOException;


}
