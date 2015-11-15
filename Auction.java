

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class Auction extends UnicastRemoteObject
					 implements IAuction{

	private static final long serialVersionUID = 1L;
	private Map<String, Item> auctionItems;
	private String itemId;
	
	protected Auction() throws RemoteException {
		super();
		this.auctionItems = new ConcurrentHashMap<String, Item>();
		this.itemId = UUID.randomUUID().toString();
	}

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


}
