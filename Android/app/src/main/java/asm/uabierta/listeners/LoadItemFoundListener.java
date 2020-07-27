package asm.uabierta.listeners;

import asm.uabierta.responses.FoundItemResponse;

/**
 * Created by Alex on 27/07/2016.
 */
public interface LoadItemFoundListener {
    void onInitGetFound();
    void onFinishGetFound(FoundItemResponse json);
}
