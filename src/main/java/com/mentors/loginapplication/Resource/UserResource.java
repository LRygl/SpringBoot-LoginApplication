package com.mentors.loginapplication.Resource;

import com.mentors.loginapplication.Exception.EmailExistsException;
import com.mentors.loginapplication.Model.User;
import com.mentors.loginapplication.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static com.mentors.loginapplication.Constant.FileConstant.FORWARD_SLASH;
import static com.mentors.loginapplication.Constant.FileConstant.USER_FOLDER;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }


    /* GET METHOD */
    @GetMapping("/listAll")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> userList = userService.getAllUnpagedUsers();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping(path = "/image/{userUUID}", produces = IMAGE_PNG_VALUE)
    public byte[] getProfileImage(@PathVariable("userUUID") UUID userId) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + userId  + FORWARD_SLASH + "avatar.png"));
    }

    /* POST METHOD */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws  EmailExistsException, IOException {
        User newUser = userService.register(user.getUserFirstName(), user.getUserLastName(), user.getUserEmail());
        LOGGER.debug("Registered a new user " + newUser);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked") String isNonLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws EmailExistsException, IOException {
        User newUser = userService.addNewUser(firstName, lastName, email, role, Boolean.parseBoolean(isActive) ,Boolean.parseBoolean(isNonLocked), profileImage);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }
}
