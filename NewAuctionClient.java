import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;


public class NewAuctionClient {
	
  private static final String commandList = 
			  "help - show commands\n"
			+ "create - Create an auction item\n"
			+ "list - List all the auctions\n"
			+ "bid - Place a bid for an item\n"
			+ "info - Get a description of an item\n"
			+ "full-info - Get full details of an item\n"
			+ "quit - Quit";
	
  public static void main(String[] args) throws MalformedURLException {
	 /* if(args.length < 1) {
	    	  System.err.println("USAGE: <isAdmin>");
	    	  System.exit(0);
	  }
		  */
	  String reg_host = "localhost";
	  int reg_port = 1099;
	  IAuctionUser user;
	  IAuction ai;
		         
	  try {
		  //boolean isAdmin = Boolean.parseBoolean(args[1]);
		  boolean isAdmin = true;
		  //ai = (IAuction)Naming.lookup("rmi://" + reg_host + ":" + reg_port + "/AuctionSystem");
		  
		  ai = (IAuction)Naming.lookup("rmi://localhost/CalculatorService");
		  
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
		  // System.out.println("Command input: " + command);
		  try {
			  if (command.startsWith("create"))
				  doCreate(scanner, ai, user);
			  else if (command.startsWith("help"))
				  System.out.println(commandList);
			  else if (command.startsWith("list"))
				  doList(ai);
			  else if (command.startsWith("bid"))
				  doBid(scanner, ai, user);
			  else if (command.startsWith("info"))
				  doGetItemDescription(scanner, ai);
			  else if (command.startsWith("full-info"))
				  doGetFullItemDescription(scanner, ai);
			  else if (command.startsWith("quit")) {
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
	private static void printHelp() {
		System.out.println("");
	}

	private static void doCreate(Scanner scanner, IAuction am,
			IAuctionUser user) throws RemoteException, ParseException {
		try {
			System.out
					.print("Please insert the title of the auction item\n>>> ");
			String title = scanner.nextLine();
			System.out
					.print("Please insert the description of the auction item\n>>> ");
			String description = scanner.nextLine();

			System.out
					.print("Please insert the starting value of the auction item in GBP\n>>> ");
			String minValueString = scanner.nextLine();
			Scanner valueScanner = new Scanner(minValueString);
			double minValue = valueScanner.nextDouble();
			valueScanner.close();

			System.out
					.print("Please insert the closing time of the auction in the format dd.MM.yyyy-HH:mm\n>>> ");
			String closingTimeString = scanner.nextLine();
			
 	        DateFormat format = new SimpleDateFormat("dd.MM.yyyy-HH:mm");
 	        Date endDate = format.parse(closingTimeString);
 	        System.out.println("The end date you entered: " + endDate); 	        
 	        String itemId = am.createItem(user, title, minValue, endDate, description);
			System.out.println("********");
			if (itemId == "")
				System.out.println("Auction item was not created!");
			else
				System.out.println("Auction item with ID = " + itemId + " was created.");
		} catch (InputMismatchException e) {
			System.err.println("Wrong input!");
		}
	}
	
	private static void doList(IAuction am) throws RemoteException {
		String auctionList = am.getItemsInAuctionList();
		System.out.println(auctionList);
	}

	private static void doBid(Scanner scanner, IAuction am,
			IAuctionUser user) throws RemoteException {
		try {
			System.out
					.print("Please insert the id of the auction you want to bid at\n>>> ");
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
	
	private static void doGetFullItemDescription(Scanner scanner, IAuction am)
			throws RemoteException {
		try {
			System.out
				.print("Please insert the id of the auction you want to be displayed\n>>> ");
			String idString = scanner.nextLine();
			Scanner idScanner = new Scanner(idString);
			String id = idScanner.next();
			idScanner.close();
			System.out.println("here");
			String auctionDetails = am.getItemDetails(id, true);
			if (auctionDetails == null)
				System.out.println("No such auction exists");
			else
				System.out.println(auctionDetails);
		} catch (InputMismatchException e) {
			System.err.println("Wrong input");
		}
	}

	private static void doGetItemDescription(Scanner scanner, IAuction am)
			throws RemoteException {
		try {
			System.out
					.print("Please insert the id of the auction you want to be displayed\n>>> ");
			String idString = scanner.nextLine();
			Scanner idScanner = new Scanner(idString);
			String id = idScanner.next();
			idScanner.close();

			String auctionDetails = am.getItemDetails(id, false);
			if (auctionDetails == null)
				System.out.println("No such auction exists");
			else
				System.out.println(auctionDetails);
		} catch (InputMismatchException e) {
			System.err.println("Wrong input");
		}
	}
  
}
