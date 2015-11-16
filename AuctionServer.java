import java.rmi.Naming;	//Import naming classes to bind to rmiregistry

public class AuctionServer {
	
	static int port = 1099;
	public AuctionServer() {
		try {
	       	IAuction items = new Auction();
	       	Naming.rebind("rmi://localhost:" + port + "/AuctionSystem", items);
	     } 
	     catch (Exception e) {
	       System.out.println("Server Error: " + e);
	     }		
	}
	
	public static void main(String args[]) {
	     	//Create the new AuctionSystem server
		new AuctionServer();
	}
}
