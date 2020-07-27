package asm.uabierta.listeners;

import asm.uabierta.responses.LostItemResponse;

/**
 * Created by Alex on 27/07/2016.
 */
public interface LoadItemLostListener {
    void onInitGetLost();
    void onFinishGetLost(LostItemResponse json);
}
