package mihai.messages;

import mihai.dto.CcpTrade;

import java.io.Serializable;

/**
 * Created by mcojocariu on 2/2/2017.
 */
public class CancelCcpTradeMessage implements Serializable {
    private CcpTrade ccpTrade;

    public CancelCcpTradeMessage(CcpTrade ccpTrade) {
        this.ccpTrade = ccpTrade;
    }

    public CcpTrade getCcpTrade() {
        return ccpTrade;
    }
}
