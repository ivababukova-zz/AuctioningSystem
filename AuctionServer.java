import java.net.MulticastSocket;
import java.rmi.Naming;	//Import naming classes to bind to rmiregistry

public class AuctionServer {
	
	static int port = 1099;
	public AuctionServer() {
		try {
	       	IAuctionItem items = new AuctionItem();
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
	
	// handle a dynamically-changing set of auctions
	
	// deal with requests from clients
	
	// maintain the state of the various ongoing auctions
	
	// Multiple clients are expected to be launched from machines in
	// the network and invoke the appropriate methods on the server
	
	// Auction information must be retained and queryable by a client
	// application for a given time period after the auction closes
	// (in reality, this would be a number of days; for the purposes of
	// testing your program, you will need to choose an
	// appropriate/configurable time unit)
	
	// The server should also be able to save/restore auction state
	// to/from permanent storage. The latter feature should be used for
	// bootstrapping the server with some initial auction items (for
	// system testing purposes).
}
