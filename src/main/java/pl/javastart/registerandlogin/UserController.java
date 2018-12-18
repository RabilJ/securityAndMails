package pl.javastart.registerandlogin;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.UUID;

@Controller
public class UserController {

    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private AsyncMailSender asyncMailSender;

    public UserController(UserRepository userRepository, UserRoleRepository userRoleRepository, AsyncMailSender asyncMailSender) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.asyncMailSender = asyncMailSender;
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
        String code = UUID.randomUUID().toString();
        user.setActivationKey(code);
        userRepository.save(user);

        UserRole userRole = new UserRole();
        userRole.setUsername(username);
        userRole.setRole("ROLE_USER");

        userRoleRepository.save(userRole);


        // TODO wysyłanie maila
        asyncMailSender.sendEmailWithAttachment(user.getUsername(), "Rejestracja", "<a href=" + '"' + "http://localhost:8080/aktywuj-konto?key=" + user.getActivationKey() + '"' + ">Link</a>");
        return "registerSuccess";
    }

    @GetMapping("/konto")
    public String account(Principal principal) {
        return "account";
    }

    @GetMapping("/aktywuj-konto")
    public String activation(@RequestParam String key) {
        User user = userRepository.findByActivationKey(key);
        if (user != null) {
            user.setEnabled(true);
            userRepository.save(user);
            return "activationSuccess";
        } else {
            return "/";
        }
    }
}