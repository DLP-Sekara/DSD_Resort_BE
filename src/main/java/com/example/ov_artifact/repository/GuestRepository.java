package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.Guest;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface GuestRepository extends JpaRepository<Guest, String> {

    boolean existsByNic(String nic);

    Optional<Guest> findByNic(String nic);
}
