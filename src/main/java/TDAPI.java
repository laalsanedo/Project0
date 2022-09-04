import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class TDAPI {
    // HTTP objects needed to
    private HttpResponse<String> response;
    private HttpRequest request;
    private HttpClient client;
    private String API,  symbols, baseURL;

    private int size;

    //Constructor to initialize some variables
    public TDAPI(@NotNull String symbol){
        client = HttpClient.newHttpClient();
        baseURL = "https://api.tdameritrade.com/v1/marketdata/quotes";
        API = "?apikey=UX2GWPMOIXPKSIKW2RWWF7UPCBZVOTIU";
        symbols = symbol.replaceAll(", ", ",");
    }

    //Method to get build the URL
    public String buildURL(){
        return baseURL+API+"&symbol="+symbols.replaceAll(",", "%2C");
    }

    //Method to get response from the endpoint.
    public HttpResponse<String> Response(){

        //Building the request.
        request = HttpRequest.newBuilder(URI.create(buildURL()))
                .build();

        //Sending request to get a response.
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("Something went wrong while sending request.");
        }
        return response;
    }

    //Method to parse the result from the response.
    public JSONObject parseQuote(){
        Response();
        JSONObject object = (JSONObject) JSONValue.parse(response.body());
        return object;
    }

    //getting a JSONObject arraylist to store the quotes.
    public ArrayList<JSONObject> getQuotes(){
        String[] symbols = this.symbols.split(",");
        size = symbols.length;
        ArrayList<JSONObject> objectsArray = new ArrayList<>();
        for (int i = 0; i < symbols.length; i++){
            objectsArray.add((JSONObject) parseQuote().get(symbols[i]));
        }
        return objectsArray;
    }

    public int getSize(){
        return size;
    }


}
