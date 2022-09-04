public class Menus {

    public void welcomeMessage(){
        System.out.print("""
                **********************************
                *           Hello User           *
                *      Input your Selection      *
                *                                *
                *      > 1............Login      *
                *      > 2...Create Account      *
                **********************************
                \s""");
    }

    public void inputUser(){
        System.out.print("""
                >Username: 
                \s""");
    }

    public void inputPassword(){
        System.out.print("""
                >Password: 
                \s""");
    }

    public void clearScreen(){
        System.out.println("\\033[H\\033[2J");
        System.out.flush();
    }

    public void welcomeBackMessage(String username){
        System.out.println("Welcome back"+ username+"!");
    }

    public void mainMenuDisplay(){
        System.out.println("""
                ===============Main Menu================
                       >A.....Account Information
                       >T..........Trade Security
                       >L.........Lookup Security
                       >Q....................Quit
                ========================================
                \s""");
    }

    public void lookUpMenu(){
        System.out.println("""
                ================Search Ticker===============
                    Type the ticker you want to look up
                    You can Lookup single ticker
                    e.g enter AAPL for single stock
                    You can also lookup multiple stocks
                    e.g enter APPL,IBM,TSLA for multiple
                    stocks [separate ticker with comma]
                ============================================
                Please enter your choice (Type go back to go to the Main Menu):\s""");
    }

    public void quoteMessage(GetData getData){
        //TICKER - 8
        //asset type - 11
        //description - 25
        //Current price - 14
        //Net Change - 11
        //NetChange% - 11
        System.out.println("TICKER  ASSET TYPE CURRENT PRICE NET CHANGE NET CHANGE% T.OPEN    T.HIGH    T.LOW     Y.CLOSE");
        for (int i = 0; i < getData.size; i++){
            System.out.printf("%-8s%-11s%-14.3f%-11.2f%-12.2f%-10.2f%-10.2f%-10.2f%-10.2f\n"
                    , getData.getSymbol().get(i), getData.getAssetType().get(i)
                    , getData.getCurrentPrice().get(i), getData.getNetChange().get(i)
                    , getData.getPercentageChange().get(i) , getData.getOpenPrice().get(i)
                    , getData.getHighPrice().get(i), getData.getLowPrice().get(i)
                    , getData.getClosePrice().get(i)); //, for current price
        }
    }

    public void wantToContinue(){
        System.out.println("""
                =========================================================================
                  If you want to continue enter "C" or enter "G" to Go back to the Menu
                =========================================================================
                \s""");
    }

    public void tradeMessage(){
        System.out.println("""
                =============================================================================
                            Enter "B" to buy a security or "S" to sell a security
                            Enter "goback" to go back to the main menu
                =============================================================================
                \s""");
    }


}
