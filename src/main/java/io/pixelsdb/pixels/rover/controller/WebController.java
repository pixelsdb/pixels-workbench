package io.pixelsdb.pixels.rover.controller;

import io.pixelsdb.pixels.rover.model.User;
import io.pixelsdb.pixels.rover.model.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Timestamp;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Created at: 4/1/23
 * Author: hank
 */
@Controller
public class WebController
{
    private final UserRepository userRepository;

    WebController(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @GetMapping("/home")
    public String homeGet(Authentication authentication, Model model)
    {
        requireNonNull(authentication, "authentication is null");
        checkArgument(authentication.isAuthenticated(), "user is not authenticated");
        String email = authentication.getName();
        User user = this.userRepository.findByEmail(email);
        model.addAttribute("user", user);
        return "home";
    }

    @PostMapping("/home")
    public String homePost(Authentication authentication, Model model)
    {
        requireNonNull(authentication, "authentication is null");
        checkArgument(authentication.isAuthenticated(), "user is not authenticated");
        String email = authentication.getName();
        User user = this.userRepository.findByEmail(email);
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/signin") // post to "/signin" is processed by the SecurityFilterChain
    public String signForm(Authentication authentication)
    {
        if (authentication != null && authentication.isAuthenticated())
        {
            return "home";
        }
        return "signin";
    }

    @GetMapping("/signup")
    public String signupForm(Model model)
    {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(User user, Model model)
    {
        System.out.println(user.toString());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
        System.out.println(encodedPassword);
        userRepository.save(user);
        model.addAttribute("msg", "signup");
        return "signin";
    }

    @GetMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response,
                             Authentication authentication, Model model)
    {
        if (authentication != null && authentication.isAuthenticated())
        {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            model.addAttribute("msg", "logout");
        }
        return "signin";
    }
}