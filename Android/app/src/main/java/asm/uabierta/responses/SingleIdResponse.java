package asm.uabierta.responses;

import java.util.ArrayList;

import asm.uabierta.models.Building;

/**
 * Created by Alex on 26/07/2016.
 */
public class SingleIdResponse {
    private int ts;
    private int success;
    private int error;
    private Integer data;

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

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }
}
