package top.sacz.timtool.net.entity;


import androidx.annotation.NonNull;

import com.alibaba.fastjson2.JSON;

import java.io.Serializable;

public class QSResult<T> implements Serializable {

    private int code;
    private String msg;
    private int action;
    private T data;


    public int getCode() {
        return code;
    }

    public QSResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public QSResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public int getAction() {
        return action;
    }

    public QSResult<T> setAction(int action) {
        this.action = action;
        return this;
    }

    public T getData() {
        return data;
    }

    public QSResult<T> setData(T data) {
        this.data = data;
        return this;
    }


    public boolean isSuccess() {
        //打印调用站
        return this.getCode() == 200;
    }


    @NonNull
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
