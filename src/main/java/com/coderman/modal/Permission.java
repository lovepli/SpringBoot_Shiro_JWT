package com.coderman.modal;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author zhangyukang
 * @Date 2020/3/1 10:19
 * @Version 1.0
 **/
@Data
@Entity
@Table(name = "system_shiro_permission")
public class Permission extends BaseEntity {
    @Column(unique = true)
    private String name;                //权限名 唯一
    @Column(unique = true)
    private String url;                 //访问地址信息 唯一
    private String description;         //描述信息
    //省略getter/setter
    private Long pId;                   //父级ID
    private int type;                   //类型：0 菜单，1：按钮
    private int orderNum;               //排序
    private String icon;                   //图标
}
