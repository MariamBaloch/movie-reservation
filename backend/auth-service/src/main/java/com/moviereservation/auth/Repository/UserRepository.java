package com.moviereservation.auth.Repository;

import com.moviereservation.auth.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends MongoRepository<User, String> {

    //find use by email during login
    Optional<User> findByEmail(String email);

    //check if email already exists during registration
    boolean existsByEmail(String email);
}
