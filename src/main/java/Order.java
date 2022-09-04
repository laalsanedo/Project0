public class Order {
    private String symbol;
    private int orderSize;

    public void setSymbol(String symbol){
        this.symbol = symbol;
    }

    public String getSymbol(){
        return symbol;
    }

    public void setOrderSize(int orderSize){
        this.orderSize = orderSize;
    }

    public int getOrderSize(){
        return orderSize;
    }
}
