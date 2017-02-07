package mihai.actors;

import akka.actor.UntypedActor;
import mihai.messages.AggregatorMessage;
import mihai.messages.TradesResponseMessage;
import mihai.utils.RequestInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mcojocariu on 2/3/2017.
 */
public class TradeAggregatorActor extends UntypedActor {
    private Map<String, RequestInfo> requestsMap = new HashMap<>();

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof TradesResponseMessage) {
            performCollateResponses((TradesResponseMessage) message);
        } else if (message instanceof AggregatorMessage) {
            setUpRequest((AggregatorMessage) message);
        } else {
            unhandled(message);
        }
    }

    private void setUpRequest(AggregatorMessage aggregatorMessage) {
        RequestInfo requestInfo = requestsMap.get(aggregatorMessage.getRequestId());
        if (requestInfo == null) {
            requestInfo = new RequestInfo(aggregatorMessage.getDestinationActor(), aggregatorMessage.getNbOfAnswers());
            requestsMap.put(aggregatorMessage.getRequestId(), requestInfo);
        } else {
            requestInfo.setResponseReceiverActor(aggregatorMessage.getDestinationActor());
            requestInfo.setNbOfAnswersExpected(aggregatorMessage.getNbOfAnswers());
            checkAnswersCompleted(aggregatorMessage.getRequestId(), requestInfo);
        }
    }

    private void checkAnswersCompleted(String requestId, RequestInfo requestInfo) {
        if (requestInfo.getNbOfAnswersExpected() != null && requestInfo.getNbOfAnswersExpected().equals(requestInfo.getNbOfAnswersReceived())) {
            TradesResponseMessage response = new TradesResponseMessage(requestId, requestInfo.getTradesList(), requestInfo.getCcpTradesList());
            requestInfo.getResponseReceiverActor().tell(response, getSelf());
            requestsMap.remove(requestId);
        } else {
            requestsMap.put(requestId, requestInfo);
        }
    }

    private void performCollateResponses(TradesResponseMessage tradesResponseMessage) {
        String requestId = tradesResponseMessage.getRequestId();
        RequestInfo requestInfo = requestsMap.get(requestId);
        if (requestInfo == null) {
            requestInfo = new RequestInfo();
            requestsMap.put(requestId, requestInfo);
        }

        updateRequestInfo(requestInfo, requestInfo.getNbOfAnswersReceived()+1, tradesResponseMessage);

        checkAnswersCompleted(requestId, requestInfo);
    }

    private void updateRequestInfo(RequestInfo requestInfo, int nbOfAnswers, TradesResponseMessage tradesResponseMessage) {
        requestInfo.setNbOfAnswersReceived(nbOfAnswers);
        if (CollectionUtils.isNotEmpty(tradesResponseMessage.getTrades())) {
            requestInfo.getTradesList().addAll(tradesResponseMessage.getTrades());
        }
        if (CollectionUtils.isNotEmpty(tradesResponseMessage.getCcpTrades())) {
            requestInfo.getCcpTradesList().addAll(tradesResponseMessage.getCcpTrades());
        }
    }
}
