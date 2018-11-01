package org.bitcoingoldj.core;

public class EquihashResult {

    private boolean isValid;
    private final String message;

    public EquihashResult(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

    public EquihashResult(boolean isValid) {
        this.isValid = isValid;
        this.message = "";
    }

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }
}
