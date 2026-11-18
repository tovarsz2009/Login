package com.thebestadso.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thebestadso.login.entity.Roles;

@Repository
public interface RolRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(String name);
}
