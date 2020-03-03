package com.coderman.service.impl;

import com.coderman.modal.Permission;
import com.coderman.modal.Role;
import com.coderman.modal.User;
import com.coderman.repository.PermissionRepository;
import com.coderman.repository.UserRepository;
import com.coderman.service.UserService;
import com.coderman.util.MenuTreeBuilder;
import com.coderman.vo.MenuVO;
import com.coderman.vo.PageVO;
import com.coderman.vo.UserPageQueryVO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author zhangyukang
 * @Date 2020/3/1 12:27
 * @Version 1.0
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 组装用户菜单信息
     * @return
     */
    @Override
    public List<MenuVO> findUserMenus() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Set<Permission> permissionSet=new HashSet<>();
        if(user.getType()==0){
            //超级管理员获取所有的菜单
            List<Permission> permissions = permissionRepository.findByTypeIs(0);
            permissionSet.addAll(permissions);
        }else {
            //普通用户根据角色获取
            List<Role> roles = user.getRoles();
            for (Role role : roles) {
                List<Permission> per = role.getPermissions();
                for (Permission permission : per) {
                    if(permission.getType()==0){
                        permissionSet.add(permission);
                    }
                }
            }
        }
        List<MenuVO> menuVOList=permissionSetToMenuList(permissionSet);
        return MenuTreeBuilder.build(menuVOList);
    }

    @Override
    public PageVO<User> findUserByQueryVO(UserPageQueryVO pageQueryVO) {
        @NotNull(message = "页码不能为空") int pageNo = pageQueryVO.getPageNo();
        @NotNull(message = "每页显示条数不能为为空") int pageSize = pageQueryVO.getPageSize();
        String username = pageQueryVO.getUsername();
        if(username!=null&&!"".equals(username)){
            username="%"+username+"%";
        }else {
            username="%%";
        }
        Pageable pageable =PageRequest.of(pageNo-1,pageSize);
        Page<User> page = userRepository.findByUsernameLikeAndTypeIsNot(pageable, username,0);
        PageVO<User> pageVO = new PageVO<>();
        pageVO.setRows(page.getContent());
        pageVO.setTotal(page.getTotalElements());
        return pageVO;
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void add(User user) {
        userRepository.save(user);
    }

    /**
     * 转换成menuVOList
     * @param permissionSet
     * @return
     */
    private List<MenuVO> permissionSetToMenuList(Set<Permission> permissionSet) {
        List<MenuVO> list=new ArrayList<>();
        for (Permission permission : permissionSet) {
            MenuVO menuVO = new MenuVO();
            menuVO.setId(permission.getId());
            menuVO.setAuthName(permission.getName());
            menuVO.setPath(permission.getUrl());
            menuVO.setOrderNum(permission.getOrderNum());
            menuVO.setPId(permission.getPId());
            menuVO.setIcon(permission.getIcon());
            list.add(menuVO);
        }
        return list;
    }
}
