package com.example.blogdemo.common.lang;

import lombok.Data;

import java.io.Serializable;


/**
 * 定义数据传输成功与否的返回值
 *
 */
@Data
public class Result implements Serializable {
    private String code;
    private String msg;
    private Object data;
    public static Result succ(Object data) {
        Result m = new Result();
        m.setCode("200"); // 返回0表示成功
        m.setData(data);
        m.setMsg("操作成功");
        return m;
    }
    public static Result succ(String mess, Object data) {
        Result m = new Result();
        m.setCode("200");
        m.setData(data);
        m.setMsg(mess);
        return m;
    }
    public static Result fail(String mess) {
        Result m = new Result();
        m.setCode("410"); // 理论上的状态码应该是400, 这里改为410进行测试
        m.setData(null);
        m.setMsg(mess);
        return m;
    }
    public static Result fail(String mess, Object data) {
        Result m = new Result();
        m.setCode("410");
        m.setData(data);
        m.setMsg(mess);
        return m;
    }
}
