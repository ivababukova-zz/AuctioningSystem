

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;


public interface IAuction  extends Remote{
	
	// enable a user to create auction items (specifying name, minimum item
	// value and closing date/time; returning unique id), --> constructor
	public String createItem(IAuctionUser user,
							 String itemName,
							 double minPrice,
							 Date closeDate,
							 String descr)	throws RemoteException;	
	
	/**
	 * Place a bid for an item (calls the item.bid method that performs the actual bid)
	 * returns true if successful and false otherwise
	 * */	
	public boolean bid(IAuctionUser user, String itemid, double newPrice)
			throws RemoteException ;
	
	/**
	 * returns the current price of an item with @param itemid specified
	 * */
	public double getCurrentPrice(String itemId)
			throws RemoteException;
	
	/**
	 * returns a list of all items that are currently in auction
	 * */
	public String getItemsInAuctionList()
			throws RemoteException;
	
	/**
	 * returns the details if an item with @param itemId
	 * if fullDetailsRequest is true, returns the item full details
	 * if the fullDetailsRequest is false, returns only the name and description
	 * of the item.
	 * */
	public String getItemDetails(String itemId, boolean fullDetailsRequest)
			throws RemoteException;
	
	/**
	 * Commands the server to save its state. This 
	 * method succeeds only for administrators.
	 * @param user the AuctionUser who sends the
	 * 		  command
	 */
	public boolean saveCurrentState(IAuctionUser user) 
			throws RemoteException;

	/**
	 * Commands the server to restore its state. This 
	 * method succeeds only for administrators.
	 * @param user the AuctionUser who sends the
	 * 		  command
	 */
	public boolean restoreCurrentState(IAuctionUser user) 
			throws RemoteException;
}
