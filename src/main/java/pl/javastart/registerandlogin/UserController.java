package pl.javastart.registerandlogin;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class UserController {

    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;

    public UserController(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(String username, String password) {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(false);
        userRepository.save(user);

        UserRole userRole = new UserRole();
        userRole.setUsername(username);
        userRole.setRole("ROLE_USER");

        userRoleRepository.save(userRole);

        // TODO wysyłanie maila

        return "registerSuccess";
    }

    @GetMapping("/konto")
    public String account(Principal principal) {
        return "account";
    }
}
