package com.adkun.markdown.common;

/**
 * 返回前端的json
 * @author adkun
 */
public class ResponseModel {

    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_FAILURE = 1;

    /**
     * 状态
     */
    private int status;

    /**
     * 数据
     */
    private Object data;

    public ResponseModel() {
        this.status = STATUS_SUCCESS;
    }

    public ResponseModel(Object data) {
        this.status = STATUS_SUCCESS;
        this.data = data;
    }

    public ResponseModel(int status, Object data) {
        this.status = status;
        this.data = data;
    }

    public static int getStatusSuccess() {
        return STATUS_SUCCESS;
    }

    public static int getStatusFailure() {
        return STATUS_FAILURE;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseModel{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }
}
