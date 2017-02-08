package mihai.actors;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import mihai.dto.CcpTrade;
import mihai.dto.Trade;
import mihai.messages.CancelCcpTradeMessage;
import mihai.messages.CancelTradeMessage;
import mihai.messages.NewCcpTradeMessage;
import mihai.messages.NewTradeMessage;
import mihai.messages.TradesRequest;
import mihai.messages.TradesResponseMessage;
import mihai.utils.Constants;
import mihai.utils.RequestType;
import mihai.utils.TradeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mcojocariu on 1/31/2017.
 */
public class TradeWorkerActor extends UntypedActor {
    Map<String, Trade> tradeMap = new HashMap<>();
    Map<String, CcpTrade> ccpTradeMap = new HashMap<>();

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof NewTradeMessage) {
            performNewTrade((NewTradeMessage) message);
        } else if (message instanceof NewCcpTradeMessage) {
            performNewCcpTrade((NewCcpTradeMessage) message);
        } else if (message instanceof CancelTradeMessage) {
            performCancelTrade((CancelTradeMessage) message);
        } else if (message instanceof CancelCcpTradeMessage) {
            performCancelCcpTrade((CancelCcpTradeMessage) message);
        } else if (message instanceof TradesRequest) {
            performTradesRequest((TradesRequest) message);
        } else {
            unhandled(message);
        }
    }

    private void performCancelCcpTrade(CancelCcpTradeMessage cancelCcpTradeMessage) {
        CcpTrade ccpTrade = cancelCcpTradeMessage.getCcpTrade();
        ccpTradeMap.remove(ccpTrade.getExchangeReference());
    }

    private void performCancelTrade(CancelTradeMessage cancelTradeMessage) {
        Trade trade = cancelTradeMessage.getTrade();
        tradeMap.remove(trade.getExchangeReference());
    }

    private void performTradesRequest(TradesRequest tradesRequest) {
        String requestId = tradesRequest.getRequestId();

        TradesResponseMessage response = null;
        switch (tradesRequest.getRequestType()) {
            case GET_TRADES:
                List<Trade> trades = new ArrayList<>(tradeMap.values());
                response = new TradesResponseMessage(requestId, trades, Collections.emptyList());
                break;
            case GET_CCP_TRADES:
                List<CcpTrade> ccpTrades = new ArrayList<>(ccpTradeMap.values());
                response = new TradesResponseMessage(requestId, Collections.emptyList(), ccpTrades);
                break;
            case GET_FULLY_MATCHED_TRADES:
            case GET_UNMATCHED_TRADES:
            case GET_MATCHED_TRADES_WITHOUT_ECONOMICS:
            case GET_MATCHED_WITHIN_TOLERANCE_FOR_AMOUNT:
            case GET_MATCHED_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT:
                response = getTradesQueryResponse(requestId, tradesRequest.getRequestType());
                break;
        }

        if (response != null) {
            ActorSelection aggregator = getContext().actorSelection("/user/" + Constants.SUPERVISOR_CLASS.getSimpleName() + "/" + Constants.AGGREGATOR_CLASS.getSimpleName());
            aggregator.tell(response, getSelf());
        }
    }

    private TradesResponseMessage getTradesQueryResponse(String requestId, RequestType requestType) {
        List<Trade> responseTrades = new ArrayList<>();
        List<CcpTrade> responseCcpTrades = new ArrayList<>();

        List<Trade> trades = new ArrayList<>(tradeMap.values());
        for (Trade trade : trades) {
            if (ccpTradeMap.containsKey(trade.getExchangeReference())) {
                CcpTrade ccpTrade = ccpTradeMap.get(trade.getExchangeReference());

                if (RequestType.GET_FULLY_MATCHED_TRADES.equals(requestType)) {
                    if (TradeUtils.areTradesEconomicsMatched(trade, ccpTrade)) {
                        responseTrades.add(trade);
                        responseCcpTrades.add(ccpTrade);
                    }
                }

                if (RequestType.GET_MATCHED_TRADES_WITHOUT_ECONOMICS.equals(requestType)) {
//                    if (!TradeUtils.areTradesEconomicsMatched(trade, ccpTrade)) {
                        responseTrades.add(trade);
                        responseCcpTrades.add(ccpTrade);
//                    }
                }

                if (RequestType.GET_MATCHED_WITHIN_TOLERANCE_FOR_AMOUNT.equals(requestType)) {
                    if (TradeUtils.areTradesEconomicsMatchedWithinToleranceForAmount(trade, ccpTrade)) {
                        responseTrades.add(trade);
                        responseCcpTrades.add(ccpTrade);
                    }
                }

                if (RequestType.GET_MATCHED_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT.equals(requestType)) {
                    if (TradeUtils.areTradesEconomicsMatchedOutsideOfToleranceForAmount(trade, ccpTrade)) {
                        responseTrades.add(trade);
                        responseCcpTrades.add(ccpTrade);
                    }
                }
            } else {
                if (RequestType.GET_UNMATCHED_TRADES.equals(requestType)) {
                    responseTrades.add(trade);
                }
            }
        }

        if (RequestType.GET_UNMATCHED_TRADES.equals(requestType)) {
            List<CcpTrade> ccpTrades = new ArrayList<>(ccpTradeMap.values());
            for (CcpTrade ccpTrade : ccpTrades) {
                if (!tradeMap.containsKey(ccpTrade.getExchangeReference())) {
                    responseCcpTrades.add(ccpTrade);
                }
            }
        }

        return new TradesResponseMessage(requestId, responseTrades, responseCcpTrades);
    }

    private void performNewTrade(NewTradeMessage newTradeMessage) {
        Trade trade = newTradeMessage.getTrade();
        tradeMap.put(trade.getExchangeReference(), trade);
    }

    private void performNewCcpTrade(NewCcpTradeMessage newCcpTradeMessage) {
        CcpTrade ccpTrade = newCcpTradeMessage.getCcpTrade();
        ccpTradeMap.put(ccpTrade.getExchangeReference(), ccpTrade);
    }
}
