package md.manastirli.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import md.manastirli.dto.JwtAuthenticationResponse;
import md.manastirli.dto.SignInRequest;
import md.manastirli.dto.SignUpRequest;
import md.manastirli.entity.Role;
import md.manastirli.entity.User;
import md.manastirli.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;


    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        userService.create(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));
        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    public Optional<User> getUserById(Long userId) {
        return repository.findById(userId);
    }

    public Optional<User> getUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    public String deleteUser(String username) {
        Optional<User> userOptional = repository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            repository.delete(user);
            return "User was successfully deleted";
        } else {
            return "User with username " + username + " not found";
        }
    }

    @PostConstruct
    public void createAdminAccountIfNotExists() {
        Optional<User> admin = repository.findUserByRole(Role.ROLE_ADMIN);
        if (admin.isEmpty()) {
            User newAdmin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("password"))
                    .email("fdfdfdfd@mail.ru")
                    .role(Role.ROLE_ADMIN)
                    .build();
            repository.save(newAdmin);
        }
    }
}
