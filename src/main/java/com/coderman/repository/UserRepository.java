package com.coderman.repository;

import com.coderman.modal.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author zhangyukang
 * @Date 2020/2/25 18:50
 * @Version 1.0
 **/
public interface  UserRepository extends JpaRepository<User, Long> {
    /**
     *  查询用户by用户名
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 用户名查询
     * @param pageable
     * @param username
     * @return
     */
    Page<User> findByUsernameLikeAndTypeIsNot(Pageable pageable,String  username,int type);
}
