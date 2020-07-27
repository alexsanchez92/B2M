package asm.uabierta.listeners;

import asm.uabierta.responses.FoundsResponse;

/**
 * Created by Alex on 27/07/2016.
 */
public interface LoadFoundsListener {
    void onInitLoad();
    void onFinishLoad(FoundsResponse json);
}
