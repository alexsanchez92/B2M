package asm.uabierta.responses;

import java.util.ArrayList;

import asm.uabierta.models.Found;
import asm.uabierta.models.User;

/**
 * Created by Alex on 26/07/2016.
 */
public class UserSingleResponse {
    private int ts;
    private int success;
    private Integer code;
    private String exception;
    private User data;

    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public int isSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
