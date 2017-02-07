package mihai.messages;

import mihai.utils.RequestType;

import java.io.Serializable;

/**
 * Created by mcojocariu on 2/1/2017.
 */
public class TradesRequest implements Serializable {
    private String requestId;
    private RequestType requestType;

    public TradesRequest(String requestId, RequestType requestType) {
        this.requestId = requestId;
        this.requestType = requestType;
    }

    public String getRequestId() {
        return requestId;
    }

    public RequestType getRequestType() {
        return requestType;
    }
}
