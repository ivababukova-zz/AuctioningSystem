import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/* implementation of the remote interface IAuction */
public class Auction extends UnicastRemoteObject
					 implements IAuction{

	private static final String state_file = "state.log";
	
	private static final long serialVersionUID = 1L;
	private Map<String, Item> auctionItems;
	private String itemId;
	
	/* constructor */
	protected Auction() throws RemoteException {
		super();
		this.auctionItems = new ConcurrentHashMap<String, Item>(); // map containing all items
		this.itemId = UUID.randomUUID().toString(); // random uuid generator, assigned to each new item
	}

	/* method that calls the Item constructor to create a new Item 
	 * instance with the specified parameters and assign it an id */
	public String createItem(IAuctionUser owner,
			 String itemName,
			 double minPrice,
			 Date closeDate,
			 String descr)	throws RemoteException {
		
		this.itemId = UUID.randomUUID().toString();
		System.out.println("Creating auction item no. " + itemId);
		Item item = new Item(itemName, minPrice, closeDate, itemId, owner, descr);
		System.out.println("Auction created: " + item.description());
		
		auctionItems.put(itemId, item);
		
		return itemId;
	}
	
	@Override
	public synchronized double getCurrentPrice(String itemId)
			throws RemoteException  {
		Item i = this.auctionItems.get(itemId);
		if (i == null) {
			return 0l;
		}
		return i.price();
	}
	
	@Override
	public boolean bid(IAuctionUser user, String itemid, double newPrice) throws RemoteException {
		Item i = auctionItems.get(itemid);
		if (i == null) {
			user.notify("You have typed wrong id or the item is not in auction anymore");
			return false;
		}
		if (i.bid(user, newPrice)) {
			user.notify("Bid successful. You are the highest bidder at the moment");
			i.sendMssgToBiddersExcept(user, "Sorry, somebody just outbid you. The new item price is " 
					+ newPrice + "GBP");
			return true;
		}
        return false;
	}

	@Override
	public String getItemDetails(String itemId, boolean fullDetailsRequest) throws RemoteException {
		Item i = this.auctionItems.get(itemId);
		if (i != null && !fullDetailsRequest) {
			return i.description();
		}
		else if (i != null && fullDetailsRequest) {
			return i.getItemProperties() + "\n" ;
		}
		return null;
	}

	@Override
	public String getItemsInAuctionList() throws RemoteException {
		String s = "";
		Iterator<Item> allItems = this.auctionItems.values().iterator();
		while (allItems.hasNext()) {
			Item i = allItems.next();
			s = s + " " + i.itemName() + ": " + i.itemId() + "\n";
		}		
		return s;
	}

	@Override
	public boolean saveCurrentState(IAuctionUser user) throws RemoteException {
		if (!user.isAdmin()) {
			user.notify("Only admins are allowed to save the state of the client");
			return false;
		}
		
		try {
			ObjectOutputStream objectOut = new ObjectOutputStream
					(new BufferedOutputStream(new FileOutputStream(state_file)));
			
			objectOut.writeObject(auctionItems);
			objectOut.writeObject(itemId);
			objectOut.close();
			System.out.println("State saved to " + state_file);
			user.notify("State saved");
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean restoreCurrentState(IAuctionUser user) throws RemoteException {
		if (!user.isAdmin()) {
			user.notify("Only admins are allowed to restore the state of the client");
			return false;
		}
		
		try {
			ObjectInputStream objectIn = new ObjectInputStream
					(new BufferedInputStream(new FileInputStream(state_file)));
			
			auctionItems = (Map<String, Item>) objectIn.readObject();
			itemId = (String) objectIn.readObject();
			objectIn.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		for (Item i: auctionItems.values()) {
			i.setUpClosing();
		}
		
		System.out.println("State restored from " + state_file);
		user.notify("State restored");
		return true;
	}


}
