package com.coderman.repository;

import com.coderman.modal.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author zhangyukang
 * @Date 2020/3/1 10:26
 * @Version 1.0
 **/
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    /**
     * 根据用户名获取权限
     * @param name
     * @return
     */
    Permission findByName(String name);

    /**
     * 获取所有的菜单（超级管理员使用）
     * @return
     */
    List<Permission> findByTypeIs(int type);
}
