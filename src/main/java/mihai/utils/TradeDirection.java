package mihai.utils;

/**
 * Created by mcojocariu on 2/6/2017.
 */
public enum TradeDirection {
    BUY,
    SELL;

    public static TradeDirection getRandomDirection() {
        TradeDirection[] directionArray = TradeDirection.values();
        int index = (int) Math.floor(Math.random() * directionArray.length);
        return directionArray[index];
    }
}