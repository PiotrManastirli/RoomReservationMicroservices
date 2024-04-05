package md.manastirli.controller;

import md.manastirli.dto.AuthRequest;
import md.manastirli.entity.UserCredentials;
import md.manastirli.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String addNewUser(@RequestBody UserCredentials user){
        return authenticationService.saveUser(user);
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest user){
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        if(authenticate.isAuthenticated()){
            return authenticationService.generateToken(user.getUsername());
        } else {
           throw new RuntimeException("Invalid Access");
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token){
         authenticationService.validateToken(token);
         return "Token is valid!";
    }


}
