package net.codejava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/mfa")
public class MfaController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MfaService mfaService;

    // Display MFA setup page with QR code
    @GetMapping("/setup")
    public String setupMfa(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }
        
        // Generate a new secret key if the user doesn't have one
        if (user.getMfaSecret() == null || user.getMfaSecret().isEmpty()) {
            String secretKey = mfaService.generateSecretKey();
            user.setMfaSecret(secretKey);
            userRepository.save(user);
        }
        
        // Generate QR code for the authenticator app
        String qrCodeImage = mfaService.generateQrCodeImageUri(user.getMfaSecret(), username);
        model.addAttribute("qrCodeImage", qrCodeImage);
        model.addAttribute("secret", user.getMfaSecret());
        
        return "mfa-setup";
    }
    
    // Enable MFA for the user after setup verification
    @PostMapping("/setup")
    public String verifyAndEnableMfa(@RequestParam("code") String code, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }
        
        // Verify the provided code
        if (mfaService.verifyCode(code, user.getMfaSecret())) {
            // Enable MFA for the user
            user.setMfaEnabled(true);
            userRepository.save(user);
            
            model.addAttribute("message", "Multi-factor authentication has been enabled successfully.");
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid verification code. Please try again.");
            model.addAttribute("qrCodeImage", mfaService.generateQrCodeImageUri(user.getMfaSecret(), username));
            model.addAttribute("secret", user.getMfaSecret());
            return "mfa-setup";
        }
    }
    
    // Display MFA verification page during login
    @GetMapping("/verify")
    public String verifyMfa() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // If not authenticated or not an MFA authentication, redirect to login
        if (auth == null || !(auth instanceof MfaAuthentication)) {
            return "redirect:/login";
        }
        
        return "mfa-verify";
    }
    
    // Verify MFA code during login
    @PostMapping("/verify")
    public String verifyMfaCode(@RequestParam("code") String code, HttpSession session, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // If not authenticated or not an MFA authentication, redirect to login
        if (auth == null || !(auth instanceof MfaAuthentication)) {
            return "redirect:/login";
        }
        
        MfaAuthentication mfaAuth = (MfaAuthentication) auth;
        User user = mfaAuth.getUser();
        
        // Verify the provided code
        if (mfaService.verifyCode(code, user.getMfaSecret())) {
            // Mark MFA as authenticated in the authentication object
            mfaAuth.setMfaAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(mfaAuth);
            
            // Redirect to the originally requested URL or home
            String requestedUrl = (String) session.getAttribute("REQUESTED_URL");
            if (requestedUrl != null) {
                session.removeAttribute("REQUESTED_URL");
                return "redirect:" + requestedUrl;
            }
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid verification code. Please try again.");
            return "mfa-verify";
        }
    }
    
    // Disable MFA for a user
    @PostMapping("/disable")
    public String disableMfa(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setMfaEnabled(false);
            user.setMfaSecret(null);
            userRepository.save(user);
            model.addAttribute("message", "Multi-factor authentication has been disabled successfully.");
        }
        
        return "redirect:/";
    }
}