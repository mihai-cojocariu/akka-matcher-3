package mihai.messages;

import mihai.dto.CcpTrade;

import java.io.Serializable;

/**
 * Created by mcojocariu on 2/1/2017.
 */
public class NewCcpTradeMessage implements Serializable {
    private CcpTrade ccpTrade;

    public NewCcpTradeMessage(CcpTrade ccpTrade) {
        this.ccpTrade = ccpTrade;
    }

    public CcpTrade getCcpTrade() {
        return ccpTrade;
    }
}
