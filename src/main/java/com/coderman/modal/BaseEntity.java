package com.coderman.modal;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author zhangyukang
 * @Date 2020/3/1 10:17
 * @Version 1.0
 **/
@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    /**
     * 删除标记
     */
    @Column(name="is_del",nullable=false,length=1)
    private Integer isdel;

    /**
     * 禁用标记
     */
    @Column(name="is_ban",nullable=false,length=1,columnDefinition = "1")
    private Integer isban;
    /**
     * 创建时间
     */
    @Column(name="create_time",nullable=false,length=19)
    private String createtime;
    /**
     * 更新时间
     */
    @Column(name="update_time",nullable=false,length=19)
    private String updatetime;



}