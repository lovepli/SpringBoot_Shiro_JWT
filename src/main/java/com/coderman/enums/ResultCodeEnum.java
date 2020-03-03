package com.coderman.enums;

import lombok.Getter;

/**
 * 返回结果的状态码
 * @Author zhangyukang
 * @Date 2020/3/1 14:51
 * @Version 1.0
 **/
@Getter
public enum  ResultCodeEnum implements BaseCodeInterface {

    // 数据操作错误定义
    SUCCESS(200, "成功!"),
    BODY_NOT_MATCH(400,"请求的数据格式不符!"),
    SIGNATURE_NOT_MATCH(401,"请求的数字签名不匹配!"),
    NOT_FOUND(404, "未找到该资源!"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误!"),
    SERVER_BUSY(503,"服务器正忙，请稍后再试!");

    /** 错误码 */
    private int resultCode;

    /** 错误描述 */
    private String resultMsg;

    ResultCodeEnum(int resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }


}
