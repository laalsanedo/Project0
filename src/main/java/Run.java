import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class Run {
    //"turned_on" will be used to control whether the user wants the application to run or quit.
    private boolean turned_on;

    //Used to store user's selection when the menu is shown.
    String input, username, password, symbol;

    // menu object is used to call methods that will print the menu.
    Menus menu;
    Scanner scanner;
    GetData getData;
    InspectDatabase inspect;
    Order order;

    private final static Logger log = LogManager.getLogger("Run");



    // Constructor to initialize the variables.
    public Run(){
        menu = new Menus();
        scanner = new Scanner(System.in);
        turned_on = true;
        inspect = new InspectDatabase();
        order = new Order();
    }

    //the application starts here
    public void run(){
        userLogin();
        mainMenu();
    }

    //method for login functionality.
    public void userLogin(){

        boolean result;
        do{
            menu.welcomeMessage();
            input = scanner.nextLine();
            if (input.equals("1")){
                //Login page.
                menu.inputUser();
                username = scanner.nextLine();
                menu.inputPassword();
                password = scanner.nextLine();

                // Checks if the username and password matches one that is in the database.
                do{
                    result = inspect.checkUserinfo(username, password);

                    if (!result){
                        menu.clearScreen();
                        System.out.println("Please try again...");
                        menu.inputUser();
                        username = scanner.nextLine();
                        menu.inputPassword();
                        password = scanner.nextLine();
                    }

                }while (result == false);
                menu.clearScreen();
                turned_on = false;
            }
            else{
                //Create new user.
                menu.inputUser();
                username = scanner.nextLine();
                menu.inputPassword();
                password = scanner.nextLine();
                // check if the username exists and secure the password(if possible).
                do{
                    result = inspect.registrationComplete(username, password);

                    if (result != true){
                        menu.clearScreen();
                        System.out.println("Please try again...");
                        menu.inputUser();
                        username = scanner.nextLine();
                        menu.inputPassword();
                        password = scanner.nextLine();
                    }

                }while (result == false);
                menu.clearScreen();
                System.out.println("Congratulations on creating a new account "+username+".\n" +
                        "Your trading simulator account has been funded with $10,000\n" +
                        "Happy Trading\n" +
                        "Press enter to continue.");
                input = scanner.nextLine();
                menu.clearScreen();
                turned_on = false;
            }
        }while (turned_on);

        // turns on so that other parts can reuse the variable.
        turned_on = true;

    }

    //main menu functionality... appears once the user has logged in or created a new account.
    public void mainMenu(){
        menu.welcomeBackMessage(username);
        do{
            menu.mainMenuDisplay();
            input = scanner.nextLine().toUpperCase();

            switch (input){

                // For looking up account
                case ("A"):{

                    menu.clearScreen();
                    int CT = inspect.getTotalClosedTrades(username);
                    int OT = inspect.getTotalOpenTrades(username);
                    double CPL = inspect.getClosedTradePL(username);
                    double OPL = inspect.getOpenTradePL(username);
                    int CW = inspect.getNumOfCWinner(username);
                    int OW = inspect.getNumOfOWinners(username);
                    double WTper = ((CW+OW)*100/(CT+OT));
                    double WCper = 0;
                    if (CT != 0 ){
                        WCper = CW*100/CT;
                    }
                    double WOper = 0;

                    if (OT != 0){
                        WOper = OW*100/OT;
                    }


                    inspect.UserInfo(username, password);
                    System.out.println("\n\nACCOUNT STATISTICS:");
                    System.out.println("Total Trades (incl open trade): "+(CT+OT));
                    System.out.printf("Total Profit or Loss (incl open trade): %,.2f\n",(OPL+CPL));
                    System.out.println("Total Winners (incl open trade): "+(CW+OW));
                    System.out.println("Total Winners in percentage (incl open trade): "+WTper+"%");
                    System.out.println("Total Losers (incl open trade): "+((CT+OT) - (CW+OW)));
                    System.out.println("Total Losers in percentage (incl open trade): "+(100-WTper)+"%");

                    System.out.println("\n\nOPEN TRADE STATISTICS");
                    System.out.println("Total Open Trades: "+ OT);
                    System.out.printf("Total Profit or Loss on Open Trades: %,.2f\n", OPL);
                    System.out.println("Total Winners currently open: "+OW);
                    System.out.println("Total Winner currently open in percentage: "+WOper);
                    System.out.println("Total Losers currently open: "+(OT -OW));
                    System.out.println("Total Losers currently open in percentage: " +((WOper != 0)?(100 - WOper): 0));

                    System.out.println("\n\nCLOSED TRADE STATISTICS");
                    System.out.println("Total Closed Trades: "+ CT);
                    System.out.printf("Total Profit or Loss on Closed Trades: %,.2f\n", CPL);
                    System.out.println("Total Winners currently closed: "+CW);
                    System.out.println("Total Winner currently closed in percentage: "+ WCper);
                    System.out.println("Total Losers currently closed: "+(CT-CW));
                    System.out.println("Total Losers currently closed in percentage: "+(100 - WCper));


                    System.out.println("\nIf you want to go back to the main menu enter 'C': ");
                    input = scanner.nextLine().toUpperCase();
                    if (input.equals("C") || !input.equals("C")) {
                        break;
                    }
                }

                // For buying a security
                case ("T"):{
                    menu.clearScreen();
                    do {
                        menu.tradeMessage();
                        input = scanner.nextLine().toUpperCase();

                        //if user wants to buy a security.
                        if (input.equals("B")){

                            System.out.print("Enter a symbol name: ");
                            order.setSymbol(scanner.nextLine().toUpperCase());

                            //check if a counter trade exists in the system. if it does then get the information and ask the user if they would like to close that trade.
                            if (inspect.checkTrade(username, order.getSymbol(), "sell")){
                                System.out.println("A counter trade was found: ");
                                inspect.getOpenTrades(username, order.getSymbol(), "sell");
                                System.out.println("Proceed to close out the trade? (Y/N)");
                                input = scanner.nextLine().toUpperCase();
                                if (input.equals("Y")){
                                    if (inspect.alterTables(username, order.getSymbol(), "sell")){

                                        System.out.println("CONFIRMATION: trade successfully closed");
                                    }
                                    else{

                                        System.out.println("TRADE COULD NOT BE CLOSED");
                                    }
                                }
                                else if (input.equals("N")){
                                    break;
                                }
                            }
                            else{
                                System.out.println("How many stocks of "+order.getSymbol()+" do you want to buy?");
                                order.setOrderSize(scanner.nextInt());
                                //check if the order can be fulfilled with the amount in the balance.
                                if (inspect.getAccountBalance(username) >= (order.getOrderSize() * new GetData(order.getSymbol()).getCurrentPrice().get(0))) {
                                    if (inspect.openTrade(username, order.getSymbol(), "buy", order.getOrderSize())) {
                                        System.out.println("CONFIRMATION: successfully opened a new order");
                                    }
                                }
                                else{
                                    System.out.println("Sorry you do not have enough funds");
                                }
                            }

                        }

                        // if a customer wants to short a security
                        if (input.equals("S")){
                            System.out.print("Enter a symbol name: ");
                            order.setSymbol(scanner.nextLine().toUpperCase());
                            //check if a counter trade exists in the system. if it does then get the information and ask the user if they would like to close that trade.
                            if (inspect.checkTrade(username, order.getSymbol(), "buy")){
                                System.out.println("A counter trade was found: ");
                                inspect.getOpenTrades(username, order.getSymbol(), "buy");
                                System.out.println("Proceed to close out the trade? (Y/N)");
                                input = scanner.nextLine().toUpperCase();
                                if (input.equals("Y")){
                                    if (inspect.alterTables(username, order.getSymbol(), "buy")){

                                        System.out.println("CONFIRMATION: trade successfully closed");
                                    }
                                    else{

                                        System.out.println("TRADE COULD NOT BE CLOSED");
                                    }
                                }
                                else if (input.equals("N")){
                                    break;
                                }
                            }
                            else{
                                System.out.println("How many stocks of "+order.getSymbol()+" do you want to short?");
                                order.setOrderSize(scanner.nextInt());
                                //check if the order can be fulfilled with the amount in the balance.
                                if (inspect.getAccountBalance(username) >= (order.getOrderSize()*new GetData(order.getSymbol()).getCurrentPrice().get(0))) {
                                    if (inspect.openTrade(username, order.getSymbol(), "sell", order.getOrderSize())) {
                                        System.out.println("CONFIRMATION: successfully opened a new order");
                                    }
                                }
                                else {
                                    System.out.println("Sorry you do not have enough funds");
                                }
                            }
                        }

                        //if the user wants to go back to main menu
                        if (input.equals("GOBACK") || input.equals("GO BACK")){
                            turned_on = false;
                            menu.clearScreen();
                        }


                    }while(turned_on);
                    turned_on = true;
                    break;
                }

                // For looking up a security
                case ("L"):{
                    menu.clearScreen();
                    //For looking up a security functionality.
                    do{
                        menu.lookUpMenu();
                        input = scanner.nextLine().toUpperCase();
                        getData = new GetData(input);
                        menu.quoteMessage(getData);
                        menu.wantToContinue();
                        input = scanner.nextLine().toUpperCase();

                        if (input.equals("C")){
                            menu.clearScreen();
                        }
                        if (input.equals("G")){
                            menu.clearScreen();
                            break;
                        }

                        //After looking up the symbol ask the user to continue looking for other symbols
                        // or buy or sell particular security from the lookup section.
                        // or go back to main menu.
                    }while(turned_on);

                    //Turing it ture so that other cases can reuse the turned_on.
                    turned_on = true;
                    break;
                }

                // For quiting the application
                case ("Q"):{
                    turned_on = false;
                }
            }
        }while(turned_on);

    }

}
