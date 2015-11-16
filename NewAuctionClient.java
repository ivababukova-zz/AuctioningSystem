import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;


public class NewAuctionClient {
	
  private static final String commandList = 
		  	  "This is the auctioning system commands available. Type:\n"
			+ "help - show commands\n"
			+ "create - Create an auction item\n"
			+ "view-price - View current price of an item\n"
			+ "list - List all the auctions\n"
			+ "bid - Place a bid for an item\n"
			+ "info - Get a description of an item\n"
			+ "full-info - Get full details of an item\n"
			+ "save - save the systems state\n"
			+ "restore - Restore the system's state from last save\n"
			+ "quit - Quit";
	
  public static void main(String[] args) throws MalformedURLException {
	  if(args.length < 1) {
	    	  System.err.println("USAGE: java NewAuctionClient <isAdmin> where isAdmin is true/false");
	    	  System.exit(0);
	  }
		  
	  IAuctionUser user;
	  IAuction ai;
		         
	  try {
		  boolean isAdmin = Boolean.parseBoolean(args[0]);		  
		  ai = (IAuction)Naming.lookup("rmi://localhost/AuctionSystem");
		  
		  user = new AuctionUser("Test User", isAdmin);  
	  } catch (Exception e) {
		  e.printStackTrace();
		  return;
	  }
	
	  Scanner scanner = new Scanner(System.in);
	  System.out.println("\nHi there. This is Iva's Auctioning System. Enjoy!\n");
	  System.out.println("Type \"help\" for displaying the commands");
	  
	  while(true) {
		  System.out.print(">> ");		  
		  String command = null;
		  if (!scanner.hasNextLine()) {
				scanner.close();
				return;
		  }
		  command = scanner.nextLine();
		  try {
			  if (command.equals("create"))
				  doCreate(scanner, ai, user);
			  else if (command.equals("help"))
				  System.out.println(commandList);
			  else if (command.equals("list"))
				  doList(ai);
			  else if (command.equals("bid"))
				  doBid(scanner, ai, user);
			  else if (command.equals("view-price"))
				  doPrintPrice(scanner, ai);
			  else if (command.equals("info"))
				  doGetItemDescription(scanner, ai);
			  else if (command.equals("full-info"))
				  doGetFullItemDescription(scanner, ai);
			  else if (command.equals("save"))
					ai.saveCurrentState(user);
			  else if (command.equals("restore"))
					ai.restoreCurrentState(user);
			  else if (command.equals("quit")) {
				  scanner.close();
				  System.exit(0);
			  } else
				  System.err.println("Wrong command! Type \"help\" for assistance");
			} catch (Exception e) {
				System.err.println("Exception while performing operation");
				e.printStackTrace();
			}
	  }
  }

   /* creates an element */
	private static void doCreate(Scanner scanner, IAuction am,
			IAuctionUser user) throws RemoteException, ParseException {
		try {
			System.out
					.print("Please insert the title of the item in auction\n>>> ");
			String title = scanner.nextLine();
			System.out
					.print("Please insert the description of the item in auction\n>>> ");
			String description = scanner.nextLine();

			System.out
					.print("Please insert the starting value of the item in auction in GBP\n>>> ");
			String minValueString = scanner.nextLine();
			Scanner valueScanner = new Scanner(minValueString);
			double minValue = valueScanner.nextDouble();
			valueScanner.close();

			System.out
					.print("Please insert the closing time of the item in auction in the format dd.MM.yyyy-HH:mm\n>>> ");
			String closingTimeString = scanner.nextLine();
			
 	        DateFormat format = new SimpleDateFormat("dd.MM.yyyy-HH:mm");
 	        Date endDate = format.parse(closingTimeString);
 	        System.out.println("The end date you entered: " + endDate); 	        
 	        String itemId = am.createItem(user, title, minValue, endDate, description);
			if (itemId == "")
				System.out.println("Auction item was not created!");
			else
				System.out.println("Auction item with ID = " + itemId + " was created.");
		} catch (InputMismatchException e) {
			System.err.println("Wrong input!");
		}
	}
	
	private static void doPrintPrice(Scanner scanner, IAuction am) throws RemoteException {
		System.out
		.print("Please insert the id of the price of the item you are interested\n>>> ");
		String idString = scanner.nextLine();
		Scanner idScanner = new Scanner(idString);
		String id = idScanner.next();
		idScanner.close();
		String itemdet = am.getItemDetails(id, false);
		if (itemdet == null) {
			System.out.println("Wrong id or the item doesn't exist");
		}else {
			double price = am.getCurrentPrice(id);
			System.out.println(price);
		}
	}
	
	private static void doList(IAuction am) throws RemoteException {
		String auctionList = am.getItemsInAuctionList();
		System.out.println(auctionList);
	}

	/* performs bidding */
	private static void doBid(Scanner scanner, IAuction am,
			IAuctionUser user) throws RemoteException {
		try {
			System.out
					.print("Please insert the id of the item in auction you want to bid at\n>>> ");
			String idString = scanner.nextLine();
			Scanner idScanner = new Scanner(idString);
			String id = idScanner.next();
			idScanner.close();

			System.out
					.print("Please insert the amount in GBP that you want to bid\n>>> ");
			String minValueString = scanner.nextLine();
			Scanner valueScanner = new Scanner(minValueString);
			double amount = valueScanner.nextDouble();
			valueScanner.close();

			am.bid(user, id, amount);
		} catch (InputMismatchException e) {
			System.err.println("Wrong input");
		}
	}
	
	/* method that gets the item full description */
	private static void doGetFullItemDescription(Scanner scanner, IAuction am)
			throws RemoteException {
		try {
			System.out
				.print("Please insert the id of the item in auction you want to be displayed\n>>> ");
			String idString = scanner.nextLine();
			Scanner idScanner = new Scanner(idString);
			String id = idScanner.next();
			idScanner.close();
			String auctionDetails = am.getItemDetails(id, true);
			if (auctionDetails == null)
				System.out.println("No such auction exists");
			else
				System.out.println(auctionDetails);
		} catch (InputMismatchException e) {
			System.err.println("Wrong input");
		}
	}
	
	/* method that gets the item short description */
	private static void doGetItemDescription(Scanner scanner, IAuction am)
			throws RemoteException {
		try {
			System.out
					.print("Please insert the id of the item in auction you want to be displayed\n>>> ");
			String idString = scanner.nextLine();
			Scanner idScanner = new Scanner(idString);
			String id = idScanner.next();
			idScanner.close();

			String auctionDetails = am.getItemDetails(id, false);
			if (auctionDetails == null)
				System.out.println("No such item exists");
			else
				System.out.println(auctionDetails);
		} catch (InputMismatchException e) {
			System.err.println("Wrong input");
		}
	}
  
}
