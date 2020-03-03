package com.coderman.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhangyukang
 * @Date 2020/3/2 11:28
 * @Version 1.0
 **/
@Data
public class UserPageQueryVO {
    @NotNull(message = "页码不能为空")
    private int pageNo;
    @NotNull(message = "每页显示条数不能为为空")
    private int pageSize;

    private String username;
}
