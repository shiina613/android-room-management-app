package com.kma.lamphoun.room_management.repository;

import com.kma.lamphoun.room_management.common.enums.Role;
import com.kma.lamphoun.room_management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndIdNot(String phone, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

    Page<User> findByRole(Role role, Pageable pageable);
    Optional<User> findByIdAndRole(Long id, Role role);
}
