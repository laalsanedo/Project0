import org.json.simple.JSONObject;

import java.util.ArrayList;

public class GetData {

    private TDAPI tdapi;
    private ArrayList<JSONObject> objects;
    private ArrayList<Double> data;
    private ArrayList<Long> dataL;
    private ArrayList<String> dataS;
    int size;

    //Constructor
    public GetData(String symbols) {
        tdapi = new TDAPI(symbols);
        objects = tdapi.getQuotes();
        data = new ArrayList<>();
        dataL = new ArrayList<>();
        dataS = new ArrayList<>();
        size = tdapi.getSize();

    }

    //Get the Asset Type
    public ArrayList<String> getAssetType() {
        dataS.clear();
        for (JSONObject object : this.objects) {
            dataS.add((String) object.get("assetType"));
        }
        return dataS;
    }

    //Get the Description
    public ArrayList<String> getDescription() {
        dataS.clear();
        for (JSONObject object : this.objects) {
            dataS.add((String) object.get("description"));
        }
        return dataS;
    }

    // is the quote delayed
    public ArrayList<String> getSymbol() {
        dataS.clear();
        for (JSONObject object : this.objects) {
            dataS.add((String) object.get("symbol"));
        }
        return dataS;
    }

    //Get current price for the symbols specified
    public ArrayList<Double> getCurrentPrice() {
        data.clear();
        for (JSONObject object : this.objects) {
            data.add((Double) object.get("lastPrice"));
        }
        return data;
    }

    //Get open price for the symbols specified
    public ArrayList<Double> getOpenPrice() {
        data.clear();
        for (JSONObject object : this.objects) {
            data.add((Double) object.get("openPrice"));
        }
        return data;
    }

    //Get close price for the symbols specified
    public ArrayList<Double> getClosePrice() {
        data.clear();
        for (JSONObject object : this.objects) {
            data.add((Double) object.get("closePrice"));
        }
        return data;
    }

    //Get high price for the symbols specified
    public ArrayList<Double> getHighPrice() {
        data.clear();
        for (JSONObject object : this.objects) {
            data.add((Double) object.get("highPrice"));
        }
        return data;
    }

    //Get low price for the symbols specified
    public ArrayList<Double> getLowPrice() {
        data.clear();
        for (JSONObject object : this.objects) {
            data.add((Double) object.get("lowPrice"));
        }
        return data;
    }

    //Get Volume for the symbols specified
    public ArrayList<Long> getVolume() {
        //dataL.isEmpty()? true: dataL.clear();
        for (JSONObject object : this.objects) {
            dataL.add((Long) object.get("totalVolume"));
        }
        return dataL;
    }

    //Get the time when the quote was generated
    public ArrayList<Long> getQuoteTime() {
        dataL.clear();
        for (JSONObject object : this.objects) {
            dataL.add((Long) object.get("quoteTimeInLong"));
        }
        return dataL;
    }

    //Get the net change in dollars
    public ArrayList<Double> getNetChange() {
        data.clear();
        for (JSONObject object : this.objects) {
            data.add((Double) object.get("netChange"));
        }
        return data;
    }

    //Get the Percentage Change
    public ArrayList<Double> getPercentageChange() {
        data.clear();
        for (JSONObject object : this.objects) {
            data.add((Double) object.get("netPercentChangeInDouble"));
        }
        return data;
    }





}