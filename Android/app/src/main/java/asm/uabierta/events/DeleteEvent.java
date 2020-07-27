package asm.uabierta.events;

import asm.uabierta.responses.SingleIdResponse;

/**
 * Created by alex on 16/02/16.
 */
public class DeleteEvent {
    private boolean withError = false;
    private int success;
    private String message;
    private Integer id;
    private String caseEvent;

    public DeleteEvent(boolean withError, String message) {
        this.withError = withError;
        this.message = message;
    }

    public DeleteEvent(boolean withError, SingleIdResponse response, String caseEvent) {
        this.withError = withError;
        this.success = response.isSuccess();
        this.id = response.getData();
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
}