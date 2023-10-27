package com.rbp.bookmymovie.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rbp.bookmymovie.models.ERole;
import com.rbp.bookmymovie.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}