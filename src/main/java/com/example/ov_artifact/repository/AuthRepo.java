package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.SystemUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepo extends JpaRepository<SystemUsers, String> {

    Optional<SystemUsers> findByName(String name);

    Optional<SystemUsers> findByEmail(String email);

    boolean existsByEmail(String email);
}
