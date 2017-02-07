package mihai.messages;

import mihai.dto.CcpTrade;
import mihai.dto.Trade;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mcojocariu on 2/1/2017.
 */
public class TradesResponseMessage implements Serializable{
    private String requestId;
    private List<Trade> trades;
    private List<CcpTrade> ccpTrades;

    public TradesResponseMessage(String requestId, List<Trade> trades, List<CcpTrade> ccpTrades){
        this.requestId = requestId;
        this.trades = trades;
        this.ccpTrades = ccpTrades;
    }

    public String getRequestId() {
        return requestId;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public List<CcpTrade> getCcpTrades() {
        return ccpTrades;
    }
}
