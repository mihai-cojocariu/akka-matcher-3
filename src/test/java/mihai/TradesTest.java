package mihai;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import mihai.actors.SupervisorActor;
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
import mihai.utils.Utils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by mcojocariu on 1/31/2017.
 */
public class TradesTest {
    private static ActorSystem system;
    private LoggingAdapter log = Logging.getLogger(system, this);
    private Class supervisorClass = SupervisorActor.class;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        system.shutdown();
        system.awaitTermination(Duration.create("10 seconds"));
    }

    @After
    public void stopActors() {
        new JavaTestKit(system) {{
            ActorSelection supervisor = system.actorSelection("/user/" + supervisorClass.getSimpleName());
            supervisor.tell(PoisonPill.getInstance(), null);

            ActorSelection aggregator = system.actorSelection("/user/" + Constants.AGGREGATOR_CLASS.getSimpleName());
            aggregator.tell(PoisonPill.getInstance(), null);
        }};
    }

    @Test
    public void canAddATrade() {
        new JavaTestKit(system) {{
            log.debug("Starting canAddATrade()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final Trade trade = Trade.aTrade();
            supervisor.tell(new NewTradeMessage(trade), getTestActor());
            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_TRADES), getTestActor());

            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(true, response.getTrades().contains(trade));
                    assertEquals(1, response.getTrades().size());
                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void canAddACcpTrade() {
        new JavaTestKit(system) {{
            log.debug("Starting canAddACcpTrade()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final CcpTrade ccpTrade = CcpTrade.aCcpTrade();
            supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());
            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_CCP_TRADES), getTestActor());

            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(ccpTrade, response.getCcpTrades().get(0));
                    assertEquals(1, response.getCcpTrades().size());
                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void canCancelATrade() {
        new JavaTestKit(system) {{
            log.debug("Starting canCancelATrade()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final Trade trade1 = Trade.aTrade();
            final Trade trade2 = Trade.aTrade();
            supervisor.tell(new NewTradeMessage(trade1), getTestActor());
            supervisor.tell(new NewTradeMessage(trade2), getTestActor());
            supervisor.tell(new CancelTradeMessage(trade1), getTestActor());
            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_TRADES), getTestActor());

            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(trade2, response.getTrades().get(0));
                    assertEquals(1, response.getTrades().size());
                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void canCancelACcpTrade() {
        new JavaTestKit(system) {{
            log.debug("Starting canCancelACcpTrade()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final CcpTrade ccpTrade1 = CcpTrade.aCcpTrade();
            final CcpTrade ccpTrade2 = CcpTrade.aCcpTrade();
            supervisor.tell(new NewCcpTradeMessage(ccpTrade1), getTestActor());
            supervisor.tell(new NewCcpTradeMessage(ccpTrade2), getTestActor());
            supervisor.tell(new CancelCcpTradeMessage(ccpTrade1), getTestActor());
            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_CCP_TRADES), getTestActor());

            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(ccpTrade2, response.getCcpTrades().get(0));
                    assertEquals(1, response.getCcpTrades().size());
                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void canPerformAMatch() {
        new JavaTestKit(system) {{
            log.debug("Starting canPerformAMatch()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final Trade trade = Trade.aTrade();
            final CcpTrade ccpTrade = CcpTrade.aCcpTrade(trade.getExchangeReference());

            supervisor.tell(new NewTradeMessage(trade), getTestActor());
            supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());
            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_UNMATCHED_TRADES), getTestActor());

            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(0, response.getCcpTrades().size());
                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void canIdentifyUnmatchedTrades(){
        new JavaTestKit(system) {{
            log.debug("Starting canIdentifyUnmatchedTrades()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final Trade trade = Trade.aTrade();
            final CcpTrade ccpTrade = CcpTrade.aCcpTrade();

            supervisor.tell(new NewTradeMessage(trade), getTestActor());
            supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());
            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_UNMATCHED_TRADES), getTestActor());

            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(1, response.getTrades().size());
                    assertEquals(1, response.getCcpTrades().size());
                    assertEquals(trade, response.getTrades().get(0));
                    assertEquals(ccpTrade, response.getCcpTrades().get(0));
                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void canIdentifyAnUnmatchPostCancel(){
        new JavaTestKit(system) {{
            log.debug("Starting canIdentifyAnUnmatchPostCancel()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final Trade trade = Trade.aTrade();
            final CcpTrade ccpTrade = CcpTrade.aCcpTrade(trade.getExchangeReference());

            supervisor.tell(new NewTradeMessage(trade), getTestActor());
            supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());
            supervisor.tell(new CancelTradeMessage(trade), getTestActor());
            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_UNMATCHED_TRADES), getTestActor());

            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(0, response.getTrades().size());
                    assertEquals(1, response.getCcpTrades().size());
                    assertEquals(ccpTrade, response.getCcpTrades().get(0));
                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void testUnmatchedTrades() {
        new JavaTestKit(system) {
            {
                log.debug("Starting testUnmatchedTrades()...");

                final int numberOfTrades = 30000;
                final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

                // load trades
                Long startLoadTimestamp = System.currentTimeMillis();

                Utils.loadTrades(supervisor, getTestActor(), numberOfTrades);

                Long endLoadTimestamp = System.currentTimeMillis();
                Long diffLoad = endLoadTimestamp - startLoadTimestamp;
                log.debug("Trades loading duration (ms): " + diffLoad);


                // match trades and ccp trades
                Long startMatchTimestamp = System.currentTimeMillis();

                supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_UNMATCHED_TRADES), getTestActor());
                TradesResponseMessage response = expectMsgClass(new FiniteDuration(20, TimeUnit.SECONDS), TradesResponseMessage.class);

                new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                    protected void run() {
                        Long endMatchTimestamp = System.currentTimeMillis();
                        Long diffMatch = endMatchTimestamp - startMatchTimestamp;
                        log.debug("Trades matching duration (ms): " + diffMatch);
                        log.debug("Unmatched trades : {}, Unmatched CCP trades: {}", response.getTrades().size(), response.getCcpTrades().size());
                        logUsedMemory();
                    }
                };
            }
        };
    }

    @Test
    public void testFullyMatchedTrades() {
        new JavaTestKit(system) {
            {
                log.debug("Starting testFullyMatchedTrades()...");

                final int numberOfTrades = 30000;
                final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

                // load trades
                Long startLoadTimestamp = System.currentTimeMillis();

                Utils.loadTrades(supervisor, getTestActor(), numberOfTrades);

                Long endLoadTimestamp = System.currentTimeMillis();
                Long diffLoad = endLoadTimestamp - startLoadTimestamp;
                log.debug("Trades loading duration (ms): " + diffLoad);


                // match trades and ccp trades
                Long startMatchTimestamp = System.currentTimeMillis();

                supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_FULLY_MATCHED_TRADES), getTestActor());
                TradesResponseMessage response = expectMsgClass(new FiniteDuration(20, TimeUnit.SECONDS), TradesResponseMessage.class);

                new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                    protected void run() {
                        Long endMatchTimestamp = System.currentTimeMillis();
                        Long diffMatch = endMatchTimestamp - startMatchTimestamp;
                        log.debug("Trades matching duration (ms): " + diffMatch);
                        log.debug("Matched trades: {}, Matched CCP trades: {}", response.getTrades().size(), response.getCcpTrades().size());
                        logUsedMemory();
                    }
                };
            }
        };
    }

    @Test
    public void testMatchedTradesWithoutEconomics() {
        new JavaTestKit(system) {{
            log.debug("Starting testMatchedTradesWithoutEconomics()...");
            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();
            Utils.loadFullTrades(supervisor, getTestActor());
            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_MATCHED_TRADES_WITHOUT_ECONOMICS), getTestActor());
            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(4, response.getTrades().size());
                    assertEquals(4, response.getCcpTrades().size());
                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void testMatchedTradesWithinToleranceForAmount() {
        new JavaTestKit(system) {{
            log.debug("Starting testMatchedTradesWithinToleranceForAmount()...");
            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();
            Utils.loadFullTrades(supervisor, getTestActor());
            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_MATCHED_WITHIN_TOLERANCE_FOR_AMOUNT), getTestActor());
            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(2, response.getTrades().size());
                    assertEquals(2, response.getCcpTrades().size());
                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void testMatchedTradesOutsideOfToleranceForAmount() {
        new JavaTestKit(system) {{
            log.debug("Starting testMatchedTradesOutsideOfToleranceForAmount()...");
            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();
            Utils.loadFullTrades(supervisor, getTestActor());
            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_MATCHED_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT), getTestActor());
            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(1, response.getTrades().size());
                    assertEquals(1, response.getCcpTrades().size());
                    logUsedMemory();
                }
            };
        }};
    }

    private TestActorRef getSupervisorActor() {
        String name = supervisorClass.getSimpleName();
        return TestActorRef.create(system, Props.create(supervisorClass), name);
    }

    private void logUsedMemory() {
        log.debug("Used memory {}", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }
}
