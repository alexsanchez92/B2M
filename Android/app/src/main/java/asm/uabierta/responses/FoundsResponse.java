package asm.uabierta.responses;

import java.util.ArrayList;

import asm.uabierta.models.Found;

/**
 * Created by Alex on 26/07/2016.
 */
public class FoundsResponse {
    private int ts;
    private int success;
    private int error;
    private int count;
    private ArrayList<Found> data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Found> getData() {
        return data;
    }

    public void setData(ArrayList<Found> data) {
        this.data = data;
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
}
