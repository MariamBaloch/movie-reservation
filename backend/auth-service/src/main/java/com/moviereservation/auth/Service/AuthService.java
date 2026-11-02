package com.moviereservation.auth.Service;

import com.moviereservation.auth.DTO.LoginRequest;
import com.moviereservation.auth.DTO.LoginResponse;
import com.moviereservation.auth.DTO.RegisterRequest;
import com.moviereservation.auth.DTO.RegisterResponse;
import com.moviereservation.auth.Enum.Role;
import com.moviereservation.auth.Exception.InformationExistException;
import com.moviereservation.auth.Exception.InformationNotFoundException;
import com.moviereservation.auth.Exception.InvalidCredentialsException;
import com.moviereservation.auth.Model.User;
import com.moviereservation.auth.Repository.UserRepository;
import com.moviereservation.auth.Security.JWTUtils;
import com.moviereservation.auth.Security.MyUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;


    public RegisterResponse register(RegisterRequest request){
        //if email already exists throw an exception
        if(userRepository.existsByEmail(request.getEmail())){
            throw new InformationExistException("user with email address " + request.getEmail() + " already exists");
        }

        //registration
        //create user
        User userObject = new User();
        userObject.setName(request.getName());
        userObject.setEmail(request.getEmail());
        userObject.setRole(Role.USER);

        //encoded password
        userObject.setPassword(passwordEncoder.encode(request.getPassword()));

        //save User
        userRepository.save(userObject);

        //create RegisterResponse
        RegisterResponse registerResponse = new RegisterResponse("Registration successful.");

        //return RegisterResponse
        return registerResponse;
    }

    public LoginResponse login(LoginRequest request){
        //get user email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));


        //compare hashed password and user password input
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            //add warn log then throw an exception
            throw new InvalidCredentialsException("Invalid email or password");
        }

        //convert the user to MyUserDetails for JWT generation
        MyUserDetails myUserDetails = new MyUserDetails(user);

        //generate jwt token
        String jwtToken = jwtUtils.generateJwtToken(myUserDetails);

        return new LoginResponse("Login successful", jwtToken);
    }


}
