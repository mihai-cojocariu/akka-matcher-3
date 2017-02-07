package mihai.utils;

import akka.actor.ActorRef;
import mihai.dto.CcpTrade;
import mihai.dto.Trade;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mcojocariu on 2/6/2017.
 */
public class RequestInfo {
    private ActorRef responseReceiverActor;
    private Integer nbOfAnswersExpected;
    private Integer nbOfAnswersReceived = 0;
    List<Trade> tradesList = new ArrayList<>();
    List<CcpTrade> ccpTradesList = new ArrayList<>();

    public RequestInfo() {
    }

    public RequestInfo(ActorRef responseReceiverActor, Integer nbOfAnswersExpected) {
        this.responseReceiverActor = responseReceiverActor;
        this.nbOfAnswersExpected = nbOfAnswersExpected;
    }

    public ActorRef getResponseReceiverActor() {
        return responseReceiverActor;
    }

    public void setResponseReceiverActor(ActorRef responseReceiverActor) {
        this.responseReceiverActor = responseReceiverActor;
    }

    public Integer getNbOfAnswersExpected() {
        return nbOfAnswersExpected;
    }

    public List<Trade> getTradesList() {
        return tradesList;
    }

    public List<CcpTrade> getCcpTradesList() {
        return ccpTradesList;
    }

    public void setNbOfAnswersExpected(int nbOfAnswersExpected) {
        this.nbOfAnswersExpected = nbOfAnswersExpected;
    }

    public Integer getNbOfAnswersReceived() {
        return nbOfAnswersReceived;
    }

    public void setNbOfAnswersReceived(Integer nbOfAnswersReceived) {
        this.nbOfAnswersReceived = nbOfAnswersReceived;
    }
}
