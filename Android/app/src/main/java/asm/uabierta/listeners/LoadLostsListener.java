package asm.uabierta.listeners;

import asm.uabierta.responses.LostsResponse;

/**
 * Created by Alex on 27/07/2016.
 */
public interface LoadLostsListener {
    void onInitLoad();
    void onFinishLoad(LostsResponse json);
}
