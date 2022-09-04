import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.Reader;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;

public class testing {

    public static void main(String[] args) {
        String symbols = "AAPL,TSLA";
        TDAPI tdapi = new TDAPI(symbols);
        HttpResponse<String> str = tdapi.Response();
        String response = str.body();

        //Convert string into JSON Object so that we can access the values easily.
        JSONObject jsonObject = (JSONObject) JSONValue.parse(response);
        //Convert the object into small unit to work with the data.
        String[] strings = symbols.split(",");
        ArrayList<JSONObject> objects = new ArrayList<>();

        for (int i = 0; i < strings.length; i++){
            objects.add((JSONObject) jsonObject.get(strings[i]));

        }

        for(JSONObject x : objects){
            System.out.println(x.get("closePrice"));
        }
    }
}
