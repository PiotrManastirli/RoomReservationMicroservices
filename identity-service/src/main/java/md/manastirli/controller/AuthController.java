package md.manastirli.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.manastirli.dto.JwtAuthenticationResponse;
import md.manastirli.dto.SignInRequest;
import md.manastirli.dto.SignUpRequest;
import md.manastirli.service.AuthenticationService;
import org.springframework.web.bind.annotation.*;
import md.manastirli.entity.User;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    @PermitAll
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/delete/{username}")
    @PermitAll
    public String deleteUser(@PathVariable String username) {
        return authenticationService.deleteUser(username);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    @PermitAll
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }

    @Operation(summary = "Найти пользователя")
    @GetMapping("/{username}")
    @PermitAll
    public Optional<User> getUser(@PathVariable String username) {
        return authenticationService.getUserByUsername(username);
    }




}
