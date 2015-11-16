import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class Item implements Serializable {

	private static final long serialVersionUID = 1L;
	private IAuctionUser owner;
	private double currPrice;
	private Date endDate;
	private String itemid;
	private String descr;
	private String itemName;
	private Map<String, IAuctionUser> bidders;
	private boolean isOpen;
	private IAuctionUser bestBidder;
	
	Item (String itemName,
		 double minPrice,
		 Date closeDate,
		 String id,
		 IAuctionUser own,
		 String descr)  throws RemoteException {
		
		this.owner = own;
		this.bidders = new HashMap<String, IAuctionUser>();
		this.currPrice = minPrice;
		this.endDate = closeDate;
		this.itemName = itemName;
		this.itemid = id;
		this.descr = descr;
		setUpClosing();
	}
	
	/* getter methods */
	public synchronized Date closeDate () {
		return this.endDate;
	}
	
	public synchronized String itemId () {
		return this.itemid;
	}
	
	public synchronized String itemName () {
		return this.itemName;
	}
	
	public synchronized String description () {
		return this.descr;
	}
	
	public synchronized double price () {
		return this.currPrice;
	}

	public synchronized String owner() throws RemoteException {
		return this.owner.getName();
	}
	
	public synchronized String getItemProperties() throws RemoteException {
		return "name of item: " + this.itemName + "\n"
			 + "current price: " + this.currPrice + "\n"
			 + "item description: " + this.descr + "\n"
		 	 + "owner: " + this.owner.getName() + "\n"
		   	 + "item bid end date: " + this.endDate + "\n"
		   	 + "is item in auction: " + this.isOpen + "\n";
	}
	
	/**
	 * this method sets up the closing time of an item.
	 * It is called by the item constructor for each item
	 * sets up a java.util.timer object that notifies users
	 * at the end of an auction
	 * */
	public synchronized void setUpClosing() {
		if (endDate.before(new Date())) {
			this.isOpen = false;
		} else {
			this.isOpen = true;
			(new Timer(true)).schedule(new TimerTask() {
				@Override
				public void run() {
					System.out.println("Timer started!");	
					try {
						Item.this.close();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, endDate);
			System.out.println("Timer set!");
		}
	}
	
	private synchronized void close() throws RemoteException{
		System.out.println("Closing this item: " + this.itemid);
		this.isOpen = false;
		
		if (this.bestBidder == null) {
			System.out.println("No successful bids for this item");
			this.owner.notify(String.format("Hi owner. Unfortunately, your item was not sold,"
					+ "nobody bid for it."));
		}
		else {
			this.bestBidder.notify(String.format("You managed to buy item "
						+ this.itemName + " id: "
						+ this.itemid + " for the price of " 
						+ this.currPrice  + "GBP"));

			String mssg = String.format("Item " + this.itemid
						+ " " + this.itemName
						+ " which you bid for is sold to someone else for "
						+ this.currPrice + "GBP by user "
						+ this.bestBidder.getId());
			sendMssgToBiddersExcept(this.bestBidder, mssg);
		}
	}
	
	public synchronized boolean bid(IAuctionUser bidder, double bid) throws RemoteException{
		if(!isOpen) {
			bidder.notify("Sorry, but this item is not for trading anymore.");
			return false;
		}
		this.bidders.put(bidder.getId(), bidder);
		if (this.currPrice > bid) {
			bidder.notify("Bid unsuccessful. The price you proposed is too low");
			return false;
		}
		this.bestBidder = bidder;
		this.currPrice = bid;
		return true;
	}
	
	public synchronized void sendMssgToBiddersExcept(IAuctionUser user, String mssg) throws RemoteException {
		Iterator<IAuctionUser> biddersIterator = this.bidders.values().iterator();
		while(biddersIterator.hasNext()) {
			IAuctionUser bidder = biddersIterator.next();
			if (!bidder.getId().equals(user.getId())) {
				bidder.notify(mssg);
			}
		}
	}
}
