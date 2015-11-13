

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;


public interface IAuctionItem  extends Remote{
	
	// enable a user to create auction items (specifying name, minimum item
	// value and closing date/time; returning unique id), --> constructor
	public String create(String owner, String itemName, float minPrice, Date closeDate)	throws RemoteException;	
		
	// bid against existing auction items
    // return true if bid was successful and price was changed
	// return false otherwise
	public boolean bid(String bidderid, String itemid, float newPrice) throws RemoteException ; // or pass id instead???		
		
	// By the end of an auction, the owner as well as the bidders of the item
	// should be notified of the result (e.g., winner id, winning bid, price 
	// not met, etc.)

	public String getItemName(String itemId) throws RemoteException;
	
	public float getCurrentPrice(String itemId) throws RemoteException;
	
	public Date getEndDate(String itemId) throws RemoteException;
	
	public ArrayList<String> getBiddersid(String itemId) throws RemoteException;
	
	public String getMaxBidderid(String itemId) throws RemoteException;
	
	public String getOwnerId(String itemId) throws RemoteException;
	
	public ArrayList<String> getAllItemIDsWithName(String name) throws RemoteException;
	
	public boolean isExpired(String itemid) throws RemoteException;
	
	public ArrayList<String> getAllItemNames() throws RemoteException;
}
