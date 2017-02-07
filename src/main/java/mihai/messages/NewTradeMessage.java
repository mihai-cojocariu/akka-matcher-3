package mihai.messages;

import mihai.dto.Trade;

import java.io.Serializable;

/**
 * Created by mcojocariu on 1/31/2017.
 */
public class NewTradeMessage implements Serializable {
    private Trade trade;

    public NewTradeMessage(Trade trade){
        this.trade = trade;
    }

    public Trade getTrade() {
        return trade;
    }
}
