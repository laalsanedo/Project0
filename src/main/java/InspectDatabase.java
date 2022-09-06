// This class file will return the data from the Database to wherever needed.


public class InspectDatabase {
    private Database database;

    public InspectDatabase() {
        database = new Database();
    }

    public boolean checkUserinfo(String username, String password){
        return database.checkUser(username, password);
    }

    public boolean registrationComplete(String username, String password){
        return database.enterNewUser(username, password);
    }

    public void UserInfo(String username, String password){
        database.UserInfo(username, password);
    }

    public boolean checkTrade(String username, String symbol, String order_type){
        return database.checkTrade(username, symbol, order_type);
    }

    public void getOpenTrades(String username, String symbol, String order_type){
        database.getOpenTrade(username, symbol, order_type);
    }

    public boolean alterTables(String username, String symbol, String order_type){
        return database.alterTables(username, symbol, order_type);
    }

    public double getAccountBalance(String username){
        return database.getAccountBalance(username);
    }

    public boolean openTrade(String username, String symbol, String order_type, int numOfShares){
        return database.openTrade(username, symbol, order_type, numOfShares);
    }

    public int getTotalClosedTrades(String username){
        return database.getTotalClosedTrades(username);
    }

    public int getTotalOpenTrades(String username){
        return database.getTotalOpenTrades(username);
    }

    public double getClosedTradePL(String username){
        return database.getClosedTradePL(username);
    }

    public double getOpenTradePL(String username){
        return database.getOpenTradePL(username);
    }

    public int getNumOfOWinners(String username){
        return database.getNumOfOWinners(username);
    }

    public int getNumOfCWinner(String username){
        return database.getNumOfCWinners(username);
    }


}
