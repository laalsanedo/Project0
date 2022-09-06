import java.sql.*;

public class Database {
    private String URL, username, password;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    // Constructor to initialize the variables.
    public Database(){
        URL = "jdbc:postgresql://localhost:1433/TradingSimulator";
        username = "postgres";
        password = "2525";
        try {
            connection = DriverManager.getConnection(URL, username, password);
        } catch (SQLException e) {
            System.out.println("Error occurred during Database connection process");
        }
    }

    public int getUserID(String username){
        String query = "SELECT id from user_info WHERE username = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while getting the user id.");
        }

        return 0;
    }

    public double getAccountBalance(String username){
        try {
            preparedStatement = connection.prepareStatement("SELECT balance from user_info WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while retrieving account balance");
            System.out.println(e);
        }
        return 0;
    }

    // Check if the username and password exists.
    public boolean checkUser(String username, String password) {
        // Query to check if the username and password exists.
        String query = "SELECT username, password from user_info WHERE username = ? AND password = ?";
        try {
            // Adding the username and the password in the query,
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong during login procedure");
        }
        return false;
    }

    // Register a new user in the database.
    public boolean enterNewUser(String username, String password) {
        String query = "SELECT username from user_info WHERE username = ?";
        try {
            // Executing the query
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            // Check if the username exists
            if (resultSet.next()){
                System.out.println("Username already exists.");
                return false;
            }
            else{
                query = "INSERT INTO user_info (id, username, password, balance)" +
                        "VALUES (nextval('id_increment'), ?, ?, ?)";
                try {
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    preparedStatement.setDouble(3, 10000.00);
                    preparedStatement.executeUpdate();
                    return true;
                } catch (SQLException e) {
                    System.out.println("Something went wrong during registration process");
                    System.out.println(e);
                }
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong during registration (When checking is the username exists.)");
        }
        return false;
    }

    //View user account info.
    public void UserInfo(String username, String password){

        try {
            preparedStatement = connection.prepareStatement("SELECT * from user_info WHERE username = ? AND password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet =preparedStatement.executeQuery();
            System.out.println("GENERAL INFORMATION:");
            if (resultSet.next()){
                System.out.println("Username: "+resultSet.getString(2)+
                        "\nAccount Balance: "+resultSet.getDouble(4));
            }

        } catch (SQLException e) {
            System.out.println("Something went wrong when the application was fetching the User Info");
        }
    }

    //To check if any trade is open of a particular order type and symbol under the user's account
    public boolean checkTrade(String username, String symbol, String order_type){
        int id = getUserID(username);
        String query = "SELECT * from trade_open WHERE user_id = ? AND symbol = ? AND order_type = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, symbol);
            preparedStatement.setString(3, order_type);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                return true;
            }
            else{
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Something went wrong during trade checking process");
        }
        return false;
    }

    //To print all the open trades of a particular order type and symbol under the user's account
    public void getOpenTrade(String username, String symbol, String order_type){
        int id = getUserID(username);
        String query = "SELECT * from trade_open WHERE user_id = ? AND symbol = ? AND order_type = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, symbol);
            preparedStatement.setString(3, order_type);
            resultSet = preparedStatement.executeQuery();

                System.out.println("User ID Trade ID Order Type Symbol No of Shares Entry Price Total ");
            //8 , 11 , 7, 13, 13, 5
            while (resultSet.next()){
                System.out.printf("%-8d%-9d%-11s%-7s%-13d%-12.3f%-5.2f\n", resultSet.getInt(6)
                ,resultSet.getInt(1), resultSet.getString(2)
                ,resultSet.getString(3), resultSet.getInt(4)
                ,resultSet.getDouble(5), (resultSet.getDouble(5)*resultSet.getInt(4)));
            }

        } catch (SQLException e) {
            System.out.println("Something went wrong during trade checking process");
        }
    }

    //To close any open trade of a particular order type and symbol under the user's account
    public boolean closeTrade(String username, String symbol, String order_type){
        int id = getUserID(username);
        refreshBalance(username, id);
        //To find all the open trade of a particular order type and symbol under the user's account
        String query1 = "SELECT * from trade_open WHERE user_id = ? AND symbol = ? AND order_type = ?";
        //To insert the closed trade in table trade history.
        String query2 = "INSERT INTO trade_history (trade_id, order_type, symbol, no_of_shares, buying_price, selling_price, p_l, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        double cprice = new GetData(symbol).getCurrentPrice().get(0);
        if (order_type.equals("buy")){
            try {
                preparedStatement = connection.prepareStatement(query1);
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, symbol);
                preparedStatement.setString(3, order_type);
                resultSet = preparedStatement.executeQuery();

                PreparedStatement preparedStatement1 = connection.prepareStatement(query2);
                while (resultSet.next()){
                    preparedStatement1.setInt(1, resultSet.getInt(1));
                    preparedStatement1.setString(2, resultSet.getString(2));
                    preparedStatement1.setString(3, resultSet.getString(3));
                    preparedStatement1.setInt(4, resultSet.getInt(4));
                    preparedStatement1.setDouble(5, resultSet.getDouble(5));
                    preparedStatement1.setDouble(6, cprice);
                    preparedStatement1.setDouble(7, resultSet.getDouble(4)*(cprice - resultSet.getDouble(5)));
                    preparedStatement1.setInt(8, id);
                    preparedStatement1.executeUpdate();
                    orderTotalClose(username, resultSet.getDouble(5), resultSet.getInt(4));
                }
                return true;
            } catch (SQLException e) {
                System.out.println("Error occurred when closing a buy trade");
            }

        } else if (order_type.equals("sell")) {
            try {
                preparedStatement = connection.prepareStatement(query1);
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, symbol);
                preparedStatement.setString(3, order_type);
                resultSet = preparedStatement.executeQuery();

                PreparedStatement preparedStatement1 = connection.prepareStatement(query2);
                while (resultSet.next()){
                    preparedStatement1.setInt(1, resultSet.getInt(1));
                    preparedStatement1.setString(2, resultSet.getString(2));
                    preparedStatement1.setString(3, resultSet.getString(3));
                    preparedStatement1.setInt(4, resultSet.getInt(4));
                    preparedStatement1.setDouble(5, cprice); //change to coloum and not cprice after addding the update function.
                    preparedStatement1.setDouble(6, resultSet.getDouble(5));
                    preparedStatement1.setDouble(7, resultSet.getDouble(4)*(resultSet.getDouble(5)-cprice));
                    preparedStatement1.setInt(8, id);
                    preparedStatement1.executeUpdate();
                    orderTotalClose(username, resultSet.getDouble(5), resultSet.getInt(4));

                }

                return true;
            } catch (SQLException e) {
                System.out.println("Error occurred when closing a sell trade");
            }
        }
        return false;
    }

    public boolean alterTables(String username, String symbol, String order_type){
        int id = getUserID(username);
        String query = "DELETE FROM trade_open WHERE user_id = ? AND symbol = ? AND order_type = ?";
        if (closeTrade(username, symbol, order_type)){
            try {
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, symbol);
                preparedStatement.setString(3, order_type);
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.out.println("Error occurred when closing the trade in trade_open table ");
                System.out.println(e);
            }
        }
        return false;
    }

    public boolean openTrade(String username, String symbol, String order_type, int numOfShares){
        int id = getUserID(username);
        refreshBalance(username, id);
        double current_price = new GetData(symbol).getCurrentPrice().get(0);
        String query1 = "INSERT INTO trade_open (trade_id, order_type, symbol, no_of_shares, entry_price, current_price, p_l, user_id)" +
                "values (nextval('trade_id_increment'), ? , ? , ? , ?, ?, ?, ?);";

        try {
            preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setString(1, order_type);
            preparedStatement.setString(2, symbol);
            preparedStatement.setInt(3, numOfShares);
            preparedStatement.setDouble(4, current_price);
            preparedStatement.setDouble(5, current_price);
            preparedStatement.setDouble(6, 0);
            preparedStatement.setInt(7, id);
            preparedStatement.executeUpdate();
            orderTotalOpen(username, (current_price*numOfShares));

            return true;
        } catch (SQLException e) {
            System.out.println("Error occurred while opening a new trade");
            System.out.println(e);
        }
        return false;
    }


    //before opening a trade
    //1) check if there are any open trades under the user's account
    //2) if yes then get all the symbols and their current price respectively.
    //3) once we have the current price then we can update the p/l on open_trade.
    //4) we will subtract the new p/L from the current account balance and the result will be the new account balance.
    //5) if new balance is less than 0 show an error that the account is 0 and should the account be funded with 10,000 again;

    public void refreshBalance( String username, int id) {

        PreparedStatement preparedStatement1 , preparedStatement2, preparedStatement3, preparedStatement4;
        double updatedBalance = 0;
        ResultSet resultSet1;
        double balance = getAccountBalance(username);

        String query1 = "select symbol, entry_price, no_of_shares, order_type, p_l, balance, trade_id, current_price " +
                "from trade_open join user_info on user_id = ?";
        String query2 = "UPDATE user_info set balance = ? where id = ?";
        String query4 = "UPDATE trade_open set current_price = ?, p_l = ? where trade_id =?";
        try {
            // To get symbol, price, and p_l to update the balance
            preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            //While to update the new balance
            while(resultSet.next()){
                double current_price = new GetData(resultSet.getString(1)).getCurrentPrice().get(0);
                double p_l = 0;

                if (resultSet.getString(4).equals("buy")){
                    p_l = (current_price - resultSet.getDouble(2))*resultSet.getInt(3);
                }
                else {
                    p_l = (resultSet.getDouble(2) - current_price)*resultSet.getInt(3);
                }
                balance += p_l;

                //updating the current price and p_l:
                preparedStatement1 = connection.prepareStatement(query4);
                preparedStatement1.setDouble(1, current_price);
                preparedStatement1.setDouble(2, p_l);
                preparedStatement1.setInt(3, resultSet.getInt(7));
                preparedStatement1.executeUpdate();
            }


            //update the balance
            preparedStatement2 = connection.prepareStatement(query2);
            preparedStatement2.setDouble(1, balance);
            preparedStatement2.setInt(2, id);
            preparedStatement2.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error occurred while updating account balance");
            System.out.println(e);
        }

    }

    public void orderTotalOpen(String username, double total) {
        double balance = getAccountBalance(username);
        balance = balance - total;
        String query = "UPDATE user_info SET balance = ? WHERE username = ?";

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, balance);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error occured while updating balance after opening the trade");
        }

    }

    public void orderTotalClose(String username, double entry_price, int numOfShares) {
        double balance = getAccountBalance(username);
        balance = balance + ( entry_price * numOfShares );
        String query = "UPDATE user_info SET balance = ? WHERE username = ?";

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, balance);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error occured while updating balance after opening the trade");
        }

    }

    public int getTotalClosedTrades(String username){
        int id = getUserID(username);
        String query = "SELECT count(trade_id) from trade_history WHERE user_id = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while fetching total closed trade data from the database");
            System.out.println(e);
        }
        return 0;
    }

    public int getTotalOpenTrades(String username){
        int id = getUserID(username);
        String query = "SELECT count(trade_id) from trade_open WHERE user_id = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while fetching total open trade data from the database");
            System.out.println(e);
        }
        return 0;
    }

    public double getOpenTradePL(String username){
        int id= getUserID(username);
        String query = "SELECT sum(p_l) from trade_open WHERE user_id = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return 0;
    }

    public double getClosedTradePL(String username){
        int id= getUserID(username);
        String query = "SELECT sum(p_l) from trade_history WHERE user_id = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return 0;
    }

    public int getNumOfCWinners(String username){
        int id = getUserID(username);
        String query = "SELECT count(trade_id) from trade_history WHERE p_l >=0 AND user_id = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public int getNumOfOWinners(String username){
        int id = getUserID(username);
        String query = "SELECT count(trade_id) from trade_open WHERE p_l >= 0 AND user_id = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

}
