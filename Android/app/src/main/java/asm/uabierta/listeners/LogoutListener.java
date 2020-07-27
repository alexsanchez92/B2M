package asm.uabierta.listeners;

import asm.uabierta.responses.LogoutResponse;

/**
 * Created by Alex on 27/07/2016.
 */
public interface LogoutListener {
    void onLogoutFinish(LogoutResponse json);
}
