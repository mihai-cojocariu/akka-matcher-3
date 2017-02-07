package mihai.utils;

import akka.actor.ActorRef;
import mihai.dto.CcpTrade;
import mihai.dto.Trade;
import mihai.messages.NewCcpTradeMessage;
import mihai.messages.NewTradeMessage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Created by mcojocariu on 2/2/2017.
 */
public class Utils {
    public static void loadTrades(ActorRef supervisor, ActorRef testActor, Integer numberOfTrades) {
        Integer tradeExchangeReference = 0;
        Integer ccpTradeExchangeReference = 0;

        for(int i=1; i<=numberOfTrades; i++) {
            tradeExchangeReference += 2;
            Trade trade = Trade.aTrade(tradeExchangeReference.toString());
            NewTradeMessage newTradeMessage = new NewTradeMessage(trade);
            supervisor.tell(newTradeMessage, testActor);

            ccpTradeExchangeReference += 3;
            CcpTrade ccpTrade = CcpTrade.aCcpTrade(ccpTradeExchangeReference.toString());
            NewCcpTradeMessage newCcpTradeMessage = new NewCcpTradeMessage(ccpTrade);
            supervisor.tell(newCcpTradeMessage, testActor);
        }
    }

    public static void loadFullTrades(ActorRef supervisor, ActorRef testActor) {
        String[] tradesArray = new String[] {
                "T100, EX100, 2017-02-07, ISIN100, BUY, 100, EUR, 200",
                "T101, EX101, 2017-02-06, ISIN101, BUY, 100, EUR, 201",
                "T102, EX102, 2017-02-07, ISIN102, BUY, 100, EUR, 202",
                "T103, EX103, 2017-02-07, ISIN103, BUY, 100, EUR, 203",
                "T104, EX104, 2017-02-07, ISIN104, BUY, 100, EUR, 204"
        };

        String[] ccpTradesArray = new String[] {
                "EX100, 2017-02-07, ISIN100, BUY, 100, EUR, 200",
                "EX101, 2017-02-07, ISIN101, BUY, 100, EUR, 200",
                "EX102, 2017-02-07, ISIN102, BUY, 100, EUR, 205",
                "EX103, 2017-02-07, ISIN103, BUY, 100, EUR, 235",
                "EX110, 2017-02-07, ISIN104, BUY, 100, EUR, 200"
        };

        for (int i=0; i<tradesArray.length; i++) {
            String[] parts = tradesArray[i].split(",");
            Trade trade = new Trade.TradeBuilder(parts[1].trim())
                    .withReference(parts[0].trim())
                    .withTradeDate(Utils.getZonedDateTime(parts[2].trim()))
                    .withIsin(parts[3].trim())
                    .withDirection(TradeDirection.valueOf(parts[4].trim()))
                    .withQuantity(Integer.parseInt(parts[5].trim()))
                    .withCurrency(parts[6].trim())
                    .withAmount(Float.parseFloat(parts[7].trim()))
                    .build();
            NewTradeMessage newTradeMessage = new NewTradeMessage(trade);
            supervisor.tell(newTradeMessage, testActor);
        }

        for (int i=0; i<ccpTradesArray.length; i++) {
            String[] parts = ccpTradesArray[i].split(",");
            CcpTrade ccpTrade = new CcpTrade.CcpTradeBuilder(parts[0].trim())
                    .withTradeDate(Utils.getZonedDateTime(parts[1].trim()))
                    .withIsin(parts[2].trim())
                    .withDirection(TradeDirection.valueOf(parts[3].trim()))
                    .withQuantity(Integer.parseInt(parts[4].trim()))
                    .withCurrency(parts[5].trim())
                    .withAmount(Float.parseFloat(parts[6].trim()))
                    .build();
            NewCcpTradeMessage newCcpTradeMessage = new NewCcpTradeMessage(ccpTrade);
            supervisor.tell(newCcpTradeMessage, testActor);
        }
    }

    public static ZonedDateTime getZonedDateTime(String dateString) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT, Locale.ENGLISH);
        LocalDate date = LocalDate.parse(dateString, dtf);
        return date.atStartOfDay(ZoneId.of(Constants.UTC_TIMEZONE));
    }
}
