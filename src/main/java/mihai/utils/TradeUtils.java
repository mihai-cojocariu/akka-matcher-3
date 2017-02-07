package mihai.utils;

import mihai.dto.CcpTrade;
import mihai.dto.Trade;

import java.util.Objects;

/**
 * Created by mcojocariu on 2/7/2017.
 */
public class TradeUtils {
    public static boolean areTradesEconomicsMatched(Trade trade, CcpTrade ccpTrade) {
        if (Objects.equals(trade.getTradeDate(), ccpTrade.getTradeDate())
                && Objects.equals(trade.getIsin(), ccpTrade.getIsin())
                && Objects.equals(trade.getDirection(), ccpTrade.getDirection())
                && Objects.equals(trade.getQuantity(), ccpTrade.getQuantity())
                && Objects.equals(trade.getCurrency(), ccpTrade.getCurrency())
                && Objects.equals(trade.getAmount(), ccpTrade.getAmount())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean areTradesEconomicsMatchedWithinToleranceForAmount(Trade trade, CcpTrade ccpTrade) {
        if (areTradesEconomicsMatchedApartForAmount(trade, ccpTrade)) {
            if (trade.getAmount() != null && ccpTrade.getAmount() != null) {
                Float difference = Math.abs(trade.getAmount() - ccpTrade.getAmount());
                if (difference <= Constants.TRADE_AMOUNT_TOLERANCE) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean areTradesEconomicsMatchedOutsideOfToleranceForAmount(Trade trade, CcpTrade ccpTrade) {
        if (areTradesEconomicsMatchedApartForAmount(trade, ccpTrade)) {
            if (trade.getAmount() != null && ccpTrade.getAmount() != null) {
                Float difference = Math.abs(trade.getAmount() - ccpTrade.getAmount());
                if (difference > Constants.TRADE_AMOUNT_TOLERANCE) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean areTradesEconomicsMatchedApartForAmount(Trade trade, CcpTrade ccpTrade) {
        if (Objects.equals(trade.getTradeDate(), ccpTrade.getTradeDate())
                && Objects.equals(trade.getIsin(), ccpTrade.getIsin())
                && Objects.equals(trade.getDirection(), ccpTrade.getDirection())
                && Objects.equals(trade.getQuantity(), ccpTrade.getQuantity())
                && Objects.equals(trade.getCurrency(), ccpTrade.getCurrency())) {
            return true;
        } else {
            return false;
        }
    }
}
