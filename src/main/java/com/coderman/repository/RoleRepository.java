package com.coderman.repository;

import com.coderman.modal.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author zhangyukang
 * @Date 2020/3/1 10:26
 * @Version 1.0
 **/
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}