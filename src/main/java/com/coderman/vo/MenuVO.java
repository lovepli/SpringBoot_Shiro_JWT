package com.coderman.vo;

import lombok.Data;

import java.util.Comparator;
import java.util.List;

/**
 * @Author zhangyukang
 * @Date 2020/3/1 20:10
 * @Version 1.0
 **/
@Data
public class MenuVO {
    private Long id;
    private String authName;
    private String path;
    private List<MenuVO> children;
    private Long pId;

    private Integer orderNum;

    private String icon;

    /*
     * 排序,根据order排序
     */
    public static Comparator<MenuVO> order(){
        Comparator<MenuVO> comparator = (o1, o2) -> {
            if(o1.getOrderNum() != o2.getOrderNum()){
                return o1.getOrderNum() - o2.getOrderNum();
            }
            return 0;
        };
        return comparator;
    }
}
