package com.coderman.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhangyukang
 * @Date 2020/3/2 12:54
 * @Version 1.0
 **/
@Data
public class PageVO<T> {
    private long total;//总条数
    private List<T> rows=new ArrayList<>();
}
