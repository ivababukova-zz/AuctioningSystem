import java.net.MulticastSocket;
import java.rmi.Naming;	//Import naming classes to bind to rmiregistry

public class AuctionServer {
	
	static int port = 1099;
	public AuctionServer() {
		try {
	       	IAuction items = new Auction();
	       	Naming.rebind("rmi://localhost:" + port + "/CalculatorService", items);
	     } 
	     catch (Exception e) {
	       System.out.println("Server Error: " + e);
	     }		
	}
	
	public static void main(String args[]) {
	     	//Create the new Calculator server
		if (args.length == 1)
			port = Integer.parseInt(args[0]);
		
		new AuctionServer();
	}
}
