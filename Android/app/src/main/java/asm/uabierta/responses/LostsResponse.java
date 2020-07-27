package asm.uabierta.responses;

import java.util.ArrayList;

import asm.uabierta.models.Found;
import asm.uabierta.models.Lost;

/**
 * Created by Alex on 26/07/2016.
 */
public class LostsResponse {
    private int ts;
    private int success;
    private int error;
    private int count;
    private ArrayList<Lost> data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

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

    public ArrayList<Lost> getData() {
        return data;
    }

    public void setData(ArrayList<Lost> data) {
        this.data = data;
    }
}
