package com.adkun.markdown.common;

/**
 * 项目异常
 * @author adkun
 */
public class MdProjException extends RuntimeException {
    private int code;
    private String message;

    public MdProjException(int code) {
        super();
        this.code = code;
    }

    public MdProjException(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
