package mihai.messages;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by mcojocariu on 2/6/2017.
 */
public class AggregatorMessage implements Serializable {
    String requestId;
    private Integer nbOfAnswers;
    ActorRef destinationActor;

    public AggregatorMessage(String requestId, Integer nbOfAnswers, ActorRef destinationActor) {
        this.requestId = requestId;
        this.nbOfAnswers = nbOfAnswers;
        this.destinationActor = destinationActor;
    }

    public String getRequestId() {
        return requestId;
    }

    public Integer getNbOfAnswers() {
        return nbOfAnswers;
    }

    public ActorRef getDestinationActor() {
        return destinationActor;
    }
}
