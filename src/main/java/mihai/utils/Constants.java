package mihai.utils;

import mihai.actors.SupervisorActor;
import mihai.actors.TradeAggregatorActor;
import mihai.actors.TradeWorkerActor;

/**
 * Created by mcojocariu on 2/6/2017.
 */
public class Constants {
    public static final String UTC_TIMEZONE = "UTC";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final Class SUPERVISOR_CLASS = SupervisorActor.class;
    public static final Class WORKER_CLASS = TradeWorkerActor.class;
    public static final Class AGGREGATOR_CLASS = TradeAggregatorActor.class;
    public static final Float TRADE_AMOUNT_TOLERANCE = 25f;
}