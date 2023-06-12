package com.app.mvpdemo.businessframe.base;

import com.app.mvpdemo.util.base.MicroPlatException;

/**
 * normal business logic exception
 *
 * @author weiwen
 * @since  2022/9/19 10:24 AM
 */
public class BusinessLogicException extends MicroPlatException {

    public BusinessLogicException(int code, String detailMessage) {
        super(code, detailMessage);

    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    @Override
    public String toString() {
        return "BusinessLogicException{} " + super.toString();
    }
}
