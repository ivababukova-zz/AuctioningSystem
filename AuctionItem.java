

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class AuctionItem
						extends java.rmi.server.UnicastRemoteObject
						implements IAuctionItem{

	private ArrayList<Item> items;
	
	protected AuctionItem() throws RemoteException {
		super();
		this.items = new ArrayList<>();
	}

	private class Item{	
		private String ownerid;
		private ArrayList<String> biddersid;
		private float currPrice;
		private Date endDate;
		private String itemName;
		private String itemid;
		private String maxBidderid;
		
		Item(String owner, String itemName, float minPrice, Date closeDate){
			this.ownerid = owner;
			this.biddersid = new ArrayList<>();
			this.currPrice = minPrice;
			this.endDate = closeDate;
			this.itemName = itemName;
			this.itemid = UUID.randomUUID().toString();
			this.maxBidderid = null;
		}
		
		private boolean isDateExpired() throws ParseException{
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm");
			Date date = new Date();
			String todayStr = dateFormat.format(date);
			Date today = dateFormat.parse(todayStr);
			if (this.endDate.before(today)) {
				return true;
			}
			return false;
		}
	}
	
	public String create(String owner, String itemName, float minPrice, Date closeDate)
		throws RemoteException {
		Item i = new Item(owner, itemName, minPrice, closeDate);
		items.add(i);
		return i.itemid;
	}
	
	public ArrayList<String> getAllItemNames(){
		ArrayList<String> names = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			names.add(item.itemName);
		}
		return names;
	}
	
	private Item itemsIterator(String itemId) throws java.rmi.RemoteException {
		Item item = null;
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).itemid.equals(itemId)) {
				return items.get(i);
			}
		}
		return item;
	}
	
	@Override
	public String getOwnerId(String itemId) 
			throws RemoteException  {
		String id = "";
		Item i = itemsIterator(itemId);
		if (i != null) return i.ownerid;
		return id;
	}	
	
	@Override
	public ArrayList<String> getBiddersid(String itemId)
			throws RemoteException  {
		ArrayList<String> bidders = new ArrayList<String>();
		Item i = itemsIterator(itemId);
		if (i != null) return i.biddersid;
		return bidders;
	}	
	
	@Override
	public Date getEndDate(String itemId)
			throws RemoteException  {

		Date d = new Date();
		Item i = itemsIterator(itemId);
		if (i != null) return i.endDate;
		return d;
	}
	@Override
	public float getCurrentPrice(String itemId)
			throws RemoteException  {

		float price = 0;
		Item i = itemsIterator(itemId);
		if (i != null) return i.currPrice;
		return price;
	}
	@Override
	public String getItemName(String itemId)
			throws RemoteException  {
		
		String name = null;
		Item i = itemsIterator(itemId);
		if (i != null) return i.itemName;
		return name;
	}

	@Override
	public String getMaxBidderid(String itemId)
			throws RemoteException  {
		String id = null;
		Item i = itemsIterator(itemId);
		if (i != null) return i.maxBidderid;
		return id;
	}
	
	
	@Override
	public boolean bid(String bidderid, String itemid, float newPrice) throws RemoteException {
		Item i = itemsIterator(itemid);
		if (i != null) {
			if (i.currPrice < newPrice) {
				i.maxBidderid = bidderid;
				i.currPrice = newPrice;
				i.biddersid.add(bidderid);
				return true;
			}
		}
		if (i == null) System.out.println("The item does not exist");
		return false;
	}
/* TODO 
	private void print(){
		System.out.print("Item sold: " + i.itemid + " Owner: " + i.ownerid
                + " item price: " + i.currPrice + " New owner: "
		         + i.maxBidderid);
	}
*/

	@Override
	public ArrayList<String> getAllItemIDsWithName(String name) throws RemoteException {
		ArrayList<String> ids = new ArrayList<>();		
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).itemName.equals(name)) {
				ids.add(items.get(i).itemid);
			}
		}		
		return ids;
	}

	@Override
	public boolean isExpired(String itemid) throws RemoteException {
		Item i = itemsIterator(itemid);
		try {
			if (!i.isDateExpired()) {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return true;
	}
}
