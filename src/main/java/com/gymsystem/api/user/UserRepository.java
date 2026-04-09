package com.gymsystem.api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Al nombrar el método usando esta convención (findBy + Campo),
    // Spring boot construye la sentencia SQL automáticamente por debajo
    Optional<User> findByEmail(String email);
    
}
