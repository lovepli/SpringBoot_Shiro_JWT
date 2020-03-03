package com.coderman.modal;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @Author zhangyukang
 * @Date 2020/3/1 10:18
 * @Version 1.0
 **/
@Data
@Entity
@Table(name = "system_shiro_role")
public class Role extends BaseEntity {

    @Column(unique = true)
    private String name;                    //角色名 唯一
    private String description;             //描述信息
    @ManyToMany(fetch= FetchType.EAGER)
    private List<Permission> permissions;   //一个用户角色对应多个权限
    //省略getter/setter
}