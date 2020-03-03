package com.coderman.service;

import com.coderman.modal.User;
import com.coderman.vo.MenuVO;
import com.coderman.vo.PageVO;
import com.coderman.vo.UserPageQueryVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Author zhangyukang
 * @Date 2020/3/1 12:26
 * @Version 1.0
 **/
public interface UserService {
    /**
     * 查询用户by用户名
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 获取用户菜单信息
     * @return
     */
    List<MenuVO> findUserMenus();

    /**
     * 模糊查询用户列表
     * @param pageQueryVO
     * @return
     */
    PageVO<User> findUserByQueryVO(UserPageQueryVO pageQueryVO);

    /**
     * 删除用户
     * @param id
     */
    void deleteById(Long id);

    /**
     * 保存用户
     * @param user
     */
    void add(User user);
}
