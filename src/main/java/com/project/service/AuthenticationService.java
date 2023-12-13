package com.project.service;

import com.project.contactMessage.messages.Messages;
import com.project.entity.concretes.user.User;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.LoginRequest;
import com.project.payload.request.business.UpdatePasswordRequest;
import com.project.payload.response.AuthResponse;
import com.project.payload.response.UserResponse;
import com.project.repository.UserRepository;
import com.project.security.jwt.JwtUtils;
import com.project.security.service.UserDetailsImpl;
import com.project.security.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public ResponseEntity<AuthResponse> authenticateUser(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        Optional<String> role = roles.stream().findFirst();
        AuthResponse.AuthResponseBuilder authResponseBuilder = AuthResponse.builder();
        authResponseBuilder.username(userDetails.getUsername());
        authResponseBuilder.token(token.substring(7));
        authResponseBuilder.name(userDetails.getName());
        authResponseBuilder.ssn(userDetails.getSsn());
        authResponseBuilder.ssn(userDetails.getSsn());
        role.ifPresent(authResponseBuilder::role);
        return ResponseEntity.ok(authResponseBuilder.build());


    }


    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return userMapper.mapUserToResponse(user);
    }

    public void updatePassword(UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {
        String username= (String) request.getAttribute("username");
        User user = userRepository.findByUsername(username);
        // Built_in kontrolü yapılır
        if(Boolean.TRUE.equals(user.getBuilt_in())){ // null gelme durumunda exception almamak için bu şekilde yazdık
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
        // eski şifre bilgisi doğru mu
        if(!passwordEncoder.matches(updatePasswordRequest.getOldPassword(),user.getPassword())){
            throw new BadRequestException(ErrorMessages.PASSWORD_NOT_MATCHED);
        }
        // Yeni şifreyi encode edilecek
        String hashedPassword= passwordEncoder.encode(updatePasswordRequest.getNewPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }
}
