import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.net.MulticastSocket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class AuctionClient {

	private static Scanner scanner = new Scanner(System.in);
	
	private static String mainMenu(){	
		
		System.out.println("This is the main menu. Please choose one of the following options: ");
 	    System.out.println("    '1' for adding new auction item");
 	    System.out.println("    '2' for viewing current items in auction");
 	    System.out.println("    '3' to bid");
 	    System.out.println("    'exit' to exit this system");
 	    String answer = scanner.next();
 	    return answer;
	}
	
	private static String addItemMenu(){
		System.out.println("This is the menu for adding new item to the auction.");
		System.out.println("    Type 'main' to go to the main menu");
	    System.out.println("    To add new auction item, type:\n"
	    		         + "        <your id>\n"
	        			 + "        <new item name>\n"
	    				 + "        <item start price pounds>\n"
	    				 + "        <end date: dd.MM.yyyy-HH:mm>\n");
	    String answer = scanner.next();
        return answer;
	}
	
	private static String viewItemsMenu(){
		System.out.println("This is the menu for viewing items in the current auction.");
	    System.out.println("    type 'main' to go to the main menu");
	    System.out.println("    type 'item-name' get all items with specific name");
	    System.out.println("    type 'item-id' to get the item with this id");
	    System.out.println("    type 'all' to get all current items in auction");
	    String answer = scanner.next();
	    return answer;
	}
	
	private static String bidMenu(){
        System.out.println("This is te bid menu.\n"
        				 + "    To bid, type: <item ID> of the item you want to bid for");
	    System.out.println("    To go to main menu type 'main'");
	    String answer = scanner.next();
	    return answer;
	}
	
  public static void main(String[] args) throws MalformedURLException {
        
    String reg_host = "localhost";
    int reg_port = 1099;
        
    if (args.length == 1) {
      reg_port = Integer.parseInt(args[0]);
    } else if (args.length == 2) {
      reg_host = args[0];
      reg_port = Integer.parseInt(args[1]);
    }
         
    try {
      IAuctionItem ai = (IAuctionItem)
 	                //Naming.lookup("rmi://localhost/CalculatorService");
 	                Naming.lookup("rmi://" + reg_host + ":" + reg_port + "/CalculatorService");
 	  String answer = mainMenu();
 	  
 	  while (!answer.equals("exit")) {  
 	    if (answer.equals("1")) {
 	      String ownerid = addItemMenu();
 	      if (ownerid.equals("main")) {
 	        answer = mainMenu();
 	      }
 	      else {
 	        String itemName = scanner.next();
 	        float startPrice = scanner.nextFloat();
 	        String dateStr = scanner.next();
 	    	
 	        DateFormat format = new SimpleDateFormat("dd.MM.yyyy-HH:mm");
 	        Date endDate = format.parse(dateStr);
 	        
 	        System.out.println("Auction item was created. This is its id: "
 	    						+ ai.create(ownerid, itemName, startPrice, endDate));
 	      }		
 	    }
        if (answer.equals("3")) {
    	    System.out.println("You want to bid, great! Which item do you want to bid for?");
     	    String option = bidMenu();
     	    if (option.equals("main") || option.equals(null)){
     	    	answer = mainMenu();
     	    }
     	    else {
     	       String itemid = option;
     	       if (ai.getItemName(itemid) == null) {
           		 System.out.println("The item requested doesn't exist or "
				         + "is no longer in auction. Try again.");
		         option = bidMenu();
     	       }
     	       else if(ai.isExpired(itemid)) {
     	    	   System.out.println("This item is no longer in auction. Try with something else");
     	    	   option = bidMenu();
     	       }
     	       else {
                   System.out.println(
             		   "Item's owner: " + ai.getOwnerId(itemid)
             		 + "\nItem's current price: " + ai.getCurrentPrice(itemid)
             		 + "\nItem's auction end date: " + ai.getEndDate(itemid));
                   System.out.println("        type <newPrice> to propose new price."
               					+ " The new price must be higher than the current"
               					+ " price for this to be a valid bid.\n"
               					+ "        type <your id>.");
                   float newprice = scanner.nextFloat();
                   String bidderid = scanner.next();
                   System.out.println("Bidding ...");
                   if (ai.bid(bidderid, itemid, newprice)){
            	       System.out.println("Bid was successful :)");
            	       System.out.println(
                 		   "\nItem's new price: " + ai.getCurrentPrice(itemid)
                 		 + "\nItem's new max bidder: " + ai.getMaxBidderid(itemid));
                 }
                 else {
            	   System.out.println("Oh no :( Somebody proposed higher price than you. Try again");
                 }
     	       }
               answer = mainMenu();
             }
        }
 	    if (answer.equals("2")) {
            String option = viewItemsMenu();
            if (option.equals("main")) {
     	        answer = mainMenu();
     	    }
            while (!option.equals("main")) {
              if (option.equals("item-name")){
            	String itemName = scanner.next();
            	
            	ArrayList<String> items = new ArrayList<>();
            	items = ai.getAllItemIDsWithName(itemName);
            	System.out.println("items size: " + items.size());
            	if (items.size() == 0) {
            		System.out.println("The item requested doesn't exist or "
            				         + "is no longer in auction. Try again.");
            		option = viewItemsMenu();
            	}
            	else {	
            	    System.out.println("\nThe ids of all " + itemName + ":");
            	    for(int i = 0; i < items.size(); i++) {
            	    	System.out.println(items.get(i));
            	    }
            	    System.out.println(" ");
            	    option = viewItemsMenu();
            	}
              }
              else if (option.equals("item-id")){
            	String itemid = scanner.next();
            	String item = ai.getItemName(itemid);
            	if (item == null) {
            		System.out.println("The item requested doesn't exist or "
            				         + "is no longer in auction. Try again.\n\n");
            		answer = mainMenu();
            		break;
            	}
            	else {
            	    System.out.println(
            	    		"The name of this item: " +  item
            	    	  + "\nType 'more' to view all item's properties"
            	    	  + "\nType 'main' to go to the main menu");
                    option = scanner.next();
                    System.out.println(" ");
                    if (option.equals("main")) {
            	      option = viewItemsMenu();
                    }
                    else if (option.equals("more")) {
                      System.out.println(
                    		   "Item's owner: " + ai.getOwnerId(itemid)
                    		 + "\nItem's current price: " + ai.getCurrentPrice(itemid)
                    		 + "\nItem's auction end date: " + ai.getEndDate(itemid));
                      System.out.println(" ");
                      option = viewItemsMenu();
                    }
            	}
              }
              else if (option.equals("all")) {
            	  System.out.println("All items currently in auction: ");
            	  ArrayList<String> names = ai.getAllItemNames();
            	  for (int i = 0; i < names.size(); i++){
            		  System.out.println(names.get(i));
            	  }
            	  System.out.println(" ");
            	  option = viewItemsMenu();
              }
              else {
                System.out.println("Wrong input. Try again.");
       	        option = viewItemsMenu();
       	        break;
              }
            }
 	    }
 	    else if(!answer.equals("1") && !answer.equals("2") && 
 	    		!answer.equals("3") && !answer.equals("exit")){
 	      System.out.println("Wrong input. Try again.");
 	      answer = mainMenu();
 	    }
 	  }
	  System.out.println("Bye bye :)");
 	}
    catch (MalformedURLException murle) {
      System.out.println();
      System.out.println("MalformedURLException");
      System.out.println(murle);
    }
    catch (RemoteException re) {
      System.out.println();
      System.out.println("RemoteException");
      System.out.println(re);
    }
    catch (NotBoundException nbe) {
      System.out.println();
      System.out.println("NotBoundException");
      System.out.println(nbe);
    }
    catch (java.lang.ArithmeticException ae) {
      System.out.println();
      System.out.println("java.lang.ArithmeticException");
      System.out.println(ae);
    } catch (ParseException e) {
		System.out.println("The format of the date is wrong."
				        + " Please restart and try again.");
	}
  }
}
