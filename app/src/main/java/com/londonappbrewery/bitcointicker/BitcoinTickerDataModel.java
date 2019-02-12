package com.londonappbrewery.bitcointicker;

import org.json.JSONException;
import org.json.JSONObject;

public class BitcoinTickerDataModel {
private double lastPrice;

    public BitcoinTickerDataModel(JSONObject jsonData){
        try {
            lastPrice = jsonData.getDouble("last");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public double getLastPrice() {
        return lastPrice;
    }
}
