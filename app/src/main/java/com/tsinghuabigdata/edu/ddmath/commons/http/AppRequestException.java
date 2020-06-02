package com.tsinghuabigdata.edu.ddmath.commons.http;

import java.io.IOException;

/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/21.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.ddmath.commons.http
 * @createTime: 2015/11/21 14:19
 */
public class AppRequestException extends RuntimeException {

    private static final long serialVersionUID = -1847119886120171012L;

    private HttpResponse response;

    public AppRequestException(HttpResponse response) {
        this.response = response;
    }

    public AppRequestException(String detailMessage, HttpResponse response) {
        super(detailMessage);
        this.response = response;
    }

    public AppRequestException(String detailMessage, Throwable throwable, HttpResponse response) {
        super(detailMessage, throwable);
        this.response = response;
    }

    public AppRequestException(Throwable throwable, HttpResponse response) {
        super(throwable);
        this.response = response;
    }

    public HttpResponse getResponse() {
        return response;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(response);
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.response = ((HttpResponse) in.readObject());
    }
}
