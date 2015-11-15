

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
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
	 * Place a bid for an item
	 * returns true if successful and false otherwise
	 * */	
	public boolean bid(IAuctionUser user, String itemid, double newPrice) throws RemoteException ; // or pass id instead???		
	
	public double getCurrentPrice(String itemId) throws RemoteException;
	
	public String getItemsInAuctionList() throws RemoteException;
	
	public String getItemDetails(String itemId, boolean fullDetailsRequest) throws RemoteException;
}
