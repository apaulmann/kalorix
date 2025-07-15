package de.paulmannit.kalorix.service;

import de.paulmannit.kalorix.model.UserEty;
import de.paulmannit.kalorix.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public List<UserEty> getAllUsers() {
        return userRepository.listAll().stream().filter(userEty -> userEty.role.equals("user")).collect(Collectors.toList());
    }

    public UserEty getUserById(Long id) {
        return userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Person nicht gefunden"));
    }

    public UserEty getUserByName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Person nicht gefunden"));
    }

    public UserEty getUserByAccessToken(String accessToken) {
        return userRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new NotFoundException("Person nicht gefunden"));
    }

    public boolean isUserAvailable(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    @Transactional
    public UserEty createUser(UserEty userEty) {
        // Prüfe, ob E-Mail bereits existiert
        if (userEty.username != null && !isUserAvailable(userEty.username)) {
            throw new IllegalArgumentException("User wird bereits verwendet");
        }

        userEty.password = BcryptUtil.bcryptHash(userEty.password);
        // Stellt sicher, dass ein eindeutiger Access-Token generiert wird
        userRepository.persist(userEty);
        return userEty;
    }

    @Transactional
    public UserEty updateUser(Long id, UserEty userEty) {
        UserEty user = getUserById(id);

        user.username = userEty.username;
        user.email = userEty.email;
        user.password = BcryptUtil.bcryptHash(userEty.password);

        return user;
    }

    @Transactional
    public void deleteUser(Long id) {
        UserEty user = getUserById(id);
        userRepository.delete(user);
    }

    // Hilfsmethode zum Erstellen eines initialen Admin-Benutzers
    @Transactional
    public void createInitialAdminIfNeeded(UserEty userEty) {
        if (UserEty.count() == 0) {
            createUser(userEty);
        }
    }

    // Hilfsmethode zur Passwortvalidierung
    public boolean validatePassword(String username, String password) {
        try {
            UserEty admin = getUserByName(username);
            return BcryptUtil.matches(password, admin.password);
        } catch (NotFoundException e) {
            return false;
        }
    }

}