package com.moviereservation.auth.Service;

import com.moviereservation.auth.DTO.RegisterRequest;
import com.moviereservation.auth.DTO.RegisterResponse;
import com.moviereservation.auth.Enum.Role;
import com.moviereservation.auth.Exception.InformationExistException;
import com.moviereservation.auth.Model.User;
import com.moviereservation.auth.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


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

}
