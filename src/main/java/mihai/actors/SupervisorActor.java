package mihai.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ActorRefRoutee;
import akka.routing.BroadcastRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import mihai.messages.AggregatorMessage;
import mihai.messages.CancelCcpTradeMessage;
import mihai.messages.CancelTradeMessage;
import mihai.messages.NewCcpTradeMessage;
import mihai.messages.NewTradeMessage;
import mihai.messages.TradesRequest;
import mihai.utils.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mcojocariu on 2/3/2017.
 */
public class SupervisorActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private Router router;
    private int nbOfChildren = 0;
    private ActorRef aggregatorActor = null;

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
            performGetTrades((TradesRequest) message);
        } else {
            unhandled(message);
        }
    }

    private void performGetTrades(TradesRequest tradesRequest) {
        if (aggregatorActor == null) {
            aggregatorActor = getContext().actorOf(Props.create(Constants.AGGREGATOR_CLASS), Constants.AGGREGATOR_CLASS.getSimpleName());
        }
        AggregatorMessage aggregatorMessage = new AggregatorMessage(tradesRequest.getRequestId(), nbOfChildren, getSender());
        aggregatorActor.tell(aggregatorMessage, getSelf());

        router = getBroadcastRouter();
        router.route(tradesRequest, getSelf());
    }

    private void performCancelCcpTrade(CancelCcpTradeMessage cancelCcpTradeMessage) {
        ActorRef actor = getChildActor(cancelCcpTradeMessage.getCcpTrade().getExchangeReference());
        actor.tell(cancelCcpTradeMessage, getSelf());
    }

    private void performCancelTrade(CancelTradeMessage cancelTradeMessage) {
        ActorRef actor = getChildActor(cancelTradeMessage.getTrade().getExchangeReference());
        actor.tell(cancelTradeMessage, getSelf());
    }

    private void performNewCcpTrade(NewCcpTradeMessage newCcpTradeMessage) {
        ActorRef actor = getChildActor(newCcpTradeMessage.getCcpTrade().getExchangeReference());
        actor.tell(newCcpTradeMessage, getSelf());
    }

    private void performNewTrade(NewTradeMessage newTradeMessage) {
        ActorRef actor = getChildActor(newTradeMessage.getTrade().getExchangeReference());
        actor.tell(newTradeMessage, getSelf());
    }

    private ActorRef getChildActor(String exchangeReference) {
        String childActorName = getChildActorName(exchangeReference);
        ActorRef actor = getContext().getChild(childActorName);
        if (actor == null) {
            actor = getContext().actorOf(Props.create(Constants.WORKER_CLASS), childActorName);
            nbOfChildren++;
        }
        return actor;
    }

    private String getChildActorName(String exchangeReference) {
        return Constants.WORKER_CLASS.getSimpleName() + "_" + String.valueOf(exchangeReference.charAt(0)).toUpperCase();
    }

    private Router getBroadcastRouter() {
        List<Routee> routees = new ArrayList<>();
        Iterator<ActorRef> childActorsIterator = getContext().getChildren().iterator();
        while (childActorsIterator.hasNext()) {
            ActorRef r = childActorsIterator.next();
//            getContext().watch(r);
            if (!r.path().toString().endsWith(Constants.AGGREGATOR_CLASS.getSimpleName())) {
                routees.add(new ActorRefRoutee(r));
            }
        }
        return new Router(new BroadcastRoutingLogic(), routees);
    }
}
