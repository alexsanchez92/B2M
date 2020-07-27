package asm.uabierta.events;

import asm.uabierta.responses.SingleIdResponse;
import asm.uabierta.responses.SingleStringResponse;

/**
 * Created by alex on 16/02/16.
 */
public class SimpleResponseEvent {
    private boolean withError = false;
    private int success;
    private String message;
    private Integer id, code;
    private String data;
    private String caseEvent;

    public SimpleResponseEvent(boolean withError, String message) {
        this.withError = withError;
        this.message = message;
    }

    public SimpleResponseEvent(boolean withError, Integer code) {
        this.withError = withError;
        this.code = code;
    }

    public SimpleResponseEvent(boolean withError, SingleIdResponse response, String caseEvent) {
        this.withError = withError;
        this.success = response.isSuccess();
        this.id = response.getData();
        this.caseEvent = caseEvent;
    }

    public SimpleResponseEvent(boolean withError, SingleStringResponse response, String caseEvent) {
        this.withError = withError;
        this.success = response.isSuccess();
        this.data = response.getData();
        this.caseEvent = caseEvent;
    }

    public boolean isWithError() {
        return withError;
    }

    public String getMessage() {
        return message;
    }

    public Integer getSuccess() {
        return success;
    }

    public String getCaseEvent() {
        return caseEvent;
    }

    public Integer getId() {
        return id;
    }

    public Integer getCode() {
        return code;
    }
}