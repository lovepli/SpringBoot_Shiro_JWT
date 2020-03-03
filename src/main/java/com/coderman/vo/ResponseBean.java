package com.coderman.vo;

import com.coderman.enums.BaseCodeInterface;
import com.coderman.enums.ResultCodeEnum;
import lombok.Data;

@Data
public class ResponseBean {

    // http 状态码
    private int code;

    // 返回信息
    private String msg;

    // 返回的数据
    private Object data;

    public ResponseBean(){}

    public ResponseBean(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功
     *
     * @return
     */
    public static ResponseBean success() {
       return success(null);
    }

    /**
     * 成功
     *
     * @return
     */
    public static ResponseBean success(Object data) {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setCode(ResultCodeEnum.SUCCESS.getResultCode());
        responseBean.setMsg(ResultCodeEnum.SUCCESS.getResultMsg());
        responseBean.setData(data);
        return responseBean;
    }



    /**
     * 失败
     */
    public static ResponseBean error(BaseCodeInterface errorInfo) {
        ResponseBean rb = new ResponseBean();
        rb.setCode(errorInfo.getResultCode());
        rb.setMsg(errorInfo.getResultMsg());
        rb.setMsg(null);
        return rb;
    }

    /**
     * 失败
     */
    public static ResponseBean error(int code, String message) {
        ResponseBean rb = new ResponseBean();
        rb.setCode(code);
        rb.setMsg(message);
        rb.setData(null);
        return rb;
    }

    /**
     * 失败
     */
    public static ResponseBean error( String message) {
        ResponseBean rb = new ResponseBean();
        rb.setCode(-1);
        rb.setMsg(message);
        rb.setData(null);
        return rb;
    }
}
