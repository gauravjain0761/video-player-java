package com.app.mvpdemo.util.base;

/**
 * normal business logic exception
 *
 * @author weiwen
 * @since  2022/9/19 10:23 AM
 */
public class MicroPlatException extends Exception {
    private int code;
    private String message;

    public MicroPlatException(int code, String detailMessage) {
        super(detailMessage);
        this.code = code;
        this.message = detailMessage;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }


    @Override
    public String toString() {
        return "MicroPlatException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
