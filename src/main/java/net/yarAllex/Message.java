package net.yarAllex;

public class Message {
    private String message;
    private Object data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public enum Type {
        sendCanvas("sendCanvas");

        public final String label;

        Type(String label) {
            this.label = label;
        }
    }
}
