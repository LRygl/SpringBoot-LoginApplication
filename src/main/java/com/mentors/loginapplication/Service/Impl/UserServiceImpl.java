package com.mentors.loginapplication.Service.Impl;

import com.mentors.loginapplication.Enum.Role;
import com.mentors.loginapplication.Exception.EmailExistsException;
import com.mentors.loginapplication.Model.User;
import com.mentors.loginapplication.Repository.UserRepository;
import com.mentors.loginapplication.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.mentors.loginapplication.Constant.FileConstant.*;
import static com.mentors.loginapplication.Constant.UserImplementationConstant.NEW_USER_WAS_SUCCESSFULY_CREATED;
import static com.mentors.loginapplication.Enum.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.*;

@Service
public class UserServiceImpl implements UserService {

    /** CONFIGURATION PROPERTIES **/
    @Value("${configuration.adminUser.company}")
    private String AdminCompany;


    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUnpagedUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId){
        return userRepository.findUserById(userId);
    }

    @Override
    public User addNewUser(String firstName, String lastName, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws  EmailExistsException, IOException {
        User user = new User();
        UUID userUUID = UUID.randomUUID();

        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setUserUUID(userUUID);
        user.setUserFirstName(firstName);
        user.setUserLastName(lastName);
        user.setUserJoinDate(new Date());
        user.setUserLastUpdatedDate(new Date());
        user.setUserEmail(email);
        user.setUserPassword(encodedPassword);
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setUserRole(getRoleEnumName(role).name());
        user.setUserAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(user);

        Path userFolder = Paths.get(USER_FOLDER + userUUID).toAbsolutePath().normalize();
        Files.createDirectories(userFolder);
        LOGGER.info("created user directory" + userFolder);
        saveProfileImage(user, profileImage);
        user.setUserProfileImageUrl(getTemporaryProfileImageUrl(firstName + "+" + lastName, user.getId()));
        userRepository.save(user);

        return user;
    }

    public void addAdminUser(String firstName, String lastName, String email) throws IOException {
        User user = new User();
        String encodedPassword = encodePassword("admin");
        UUID userUUID = UUID.randomUUID();
        user.setId(1L);
        user.setUserUUID(userUUID);
        user.setUserFirstName(firstName);
        user.setUserLastName(lastName);
        user.setUserJoinDate(new Date());
        user.setUserLastUpdatedDate(new Date());
        user.setUserCompany(AdminCompany);
        user.setUserEmail(email);
        user.setUserPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setUserRole(getRoleEnumName("ROLE_SUPER_ADMIN").name());
        user.setUserAuthorities(getRoleEnumName("ROLE_SUPER_ADMIN").getAuthorities());
        userRepository.save(user);

        Path userFolder = Paths.get(USER_FOLDER + userUUID).toAbsolutePath().normalize();
        Files.createDirectories(userFolder);
        userRepository.save(user);
    }

    @Override
    public User register(String firstName, String lastName, String userEmail) throws EmailExistsException, IOException {
        userExistsByEmail(userEmail);
        User user = new User();

        String password = generatePassword();
        user.setUserFirstName(firstName);
        user.setUserLastName(lastName);
        user.setUserEmail(userEmail);
        user.setUserJoinDate(new Date());
        user.setUserLastUpdatedDate(new Date());
        user.setUserPassword(encodePassword(password));
        user.setActive(true);
        user.setNotLocked(true);
        user.setUserRole(ROLE_USER.name());
        user.setUserAuthorities(ROLE_USER.getAuthorities());
        userRepository.save(user);
        user.setUserProfileImageUrl(getTemporaryProfileImageUrl("test",user.getId()));

        LOGGER.info("New user created " + userEmail + " " + user.getId());
        LOGGER.info("User Password is " + password);
        Path userFolder = Paths.get(USER_FOLDER + user.getId()).toAbsolutePath().normalize();
        LOGGER.info(String.valueOf(userFolder));
        Files.createDirectories(userFolder);
        /** emailService.sendNewPasswordEmail(firstName,password,email); **/
        LOGGER.info(NEW_USER_WAS_SUCCESSFULY_CREATED + userEmail + (user.getId()) + ") with email " + userEmail);
        userRepository.save(user);
        return user;
    }




    public Boolean userExistsByEmail(String userEmail) throws EmailExistsException {
        User result = userRepository.findUserByUserEmail((userEmail));
        if(userRepository.findUserByUserEmail(userEmail)==null){
            return false;
        } else
            throw new EmailExistsException("EMAIL EXISTS");
    }







    /** PRIVATE METHODS **/
    private String generatePassword(){
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String setProfileImageUrl(String fileName, UUID userId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + FORWARD_SLASH + userId + FORWARD_SLASH
                + fileName + DOT + PNG_EXTENSION).toUriString();
    }
    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
                throw new IOException();
            }
            Path userFolder = Paths.get(USER_FOLDER + user.getUserUUID()).toAbsolutePath().normalize();
            LOGGER.info(String.valueOf(userFolder));
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUserEmail() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(profileImage.getOriginalFilename() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setUserProfileImageUrl(setProfileImageUrl(profileImage.getOriginalFilename(), user.getUserUUID()));
            user.setUserLastUpdatedDate(new Date());
            userRepository.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }
    private String getTemporaryProfileImageUrl(String username, Long userId) throws IOException {
        LOGGER.info("Generating user image");
        User user = getUserById(userId);
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + "name=" + username + "&" + TEMP_IMAGE_ROUNDED + "&" + TEMP_IMAGE_BACKGROUND_COLOR + "&" + TEMP_IMAGE_TEXT_COLOR);
        Path userFolder = Paths.get(USER_FOLDER + user.getUserUUID()).toAbsolutePath().normalize();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        LOGGER.info(userFolder + FORWARD_SLASH + "avatar.png");
        OutputStream os = new FileOutputStream(userFolder + FORWARD_SLASH + "avatar.png");
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
                os.write(chunk,0,bytesRead);
            }
        }
        os.close();

        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + user.getUserUUID()  ).toUriString();
    }


}
