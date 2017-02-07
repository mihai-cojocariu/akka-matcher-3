package mihai.messages;

import mihai.dto.Trade;

import java.io.Serializable;

/**
 * Created by mcojocariu on 2/2/2017.
 */
public class CancelTradeMessage implements Serializable {
    private Trade trade;

    public CancelTradeMessage(Trade trade) {
        this.trade = trade;
    }

    public Trade getTrade() {
        return trade;
    }
}
