package com.coffee.jedi.carsharingapp.repository;

import com.coffee.jedi.carsharingapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getRolesByName(Role.RoleName roleName);
}
