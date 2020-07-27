package asm.uabierta.responses;

import asm.uabierta.models.User;

/**
 * Created by Alex on 26/07/2016.
 */
public class LogoutResponse {
    private int ts;
    private int success;
    private int error;

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
}
