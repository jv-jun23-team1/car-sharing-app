package com.team01.carsharingapp.repository;

import com.team01.carsharingapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getRolesByName(Role.RoleName roleName);
}
