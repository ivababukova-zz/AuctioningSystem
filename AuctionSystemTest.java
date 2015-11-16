
import static org.junit.Assert.*;
import java.rmi.Naming;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

public class AuctionSystemTest {

	private static String host;
	private static IAuction ia;
	
	private static final String[] CLOSING_TIMES = {"12.12.2015-12:33",
												   "16.11.2015-23:23",
												   "30.11.2015-15:14",
												   "01.01.2017-00:01",
												   "01.01.2016-00:01"};
	private static final int USER_LIST_SIZE = 30;
	private static final int CREATE_TIMES = 5;
	private static final int LIST_TIMES = 100;
	private static final int INFO_TIMES = 30;
	private static final int BID_TIMES = 30;
	
	@BeforeClass
	public static void initialize() throws Exception {
		System.out.println("Testing for host \"" + host + "\"");
		ia = (IAuction) Naming.lookup("rmi://localhost/AuctionSystem");
	}
	
	@Test
	public void testCreationBidInfo() throws Exception {
		Random random = new Random();
		List<IAuctionUser> userList = new ArrayList<IAuctionUser>(USER_LIST_SIZE);
//		populateUserList(userList, USER_LIST_SIZE);
		
		int id = (random).nextInt(10000); 
		
		for (int i = 0; i < USER_LIST_SIZE; i++) {
			userList.add(new AuctionUser("fakeUser" + id, false));			
		}
	
		List<String> itemIDs = new ArrayList<String>();
		for (int i = 0; i < userList.size(); i++) {
			IAuctionUser user = userList.get(i);
			
			for (int j = 0; j < CREATE_TIMES; j++) {
				
				String title = "The title " + random.nextInt(10000);
				String description = "The description " + random.nextInt(10000);
				double minValue = random.nextDouble() * 10000;
				String closingTimeString = CLOSING_TIMES[random.nextInt(CLOSING_TIMES.length)]; 
				DateFormat format = new SimpleDateFormat("dd.MM.yyyy-HH:mm");
	 	        Date endDate = format.parse(closingTimeString);

				String itemid = ia.createItem(user, title, minValue, endDate, description);
				assertTrue(itemid != "");
				
				itemIDs.add(itemid);
			}
			
			for (int j =0; j < INFO_TIMES; j++) {
				String itemid = itemIDs.get(random.nextInt(itemIDs.size()));
				String result = ia.getItemDetails(itemid, true);

				assertTrue(result != null);
			}
			
			for (int j =0; j < BID_TIMES; j++) {
				String itemID = itemIDs.get(random.nextInt(itemIDs.size()));
				double bid = random.nextDouble() * 10000;
				boolean result = ia.bid(user, itemID, bid);
				
				assertTrue(result);
			}
			
			for (int j =0; j < INFO_TIMES; j++) {
				String itemid = itemIDs.get(random.nextInt(itemIDs.size()));
				String result = ia.getItemDetails(itemid, true);

				assertTrue(result != null);
			}
			
		}
	}

	@Test
	public void testList() throws Exception {
		for (int i = 0; i < LIST_TIMES; i++) {
			String result = ia.getItemsInAuctionList();
			assertTrue(result != null);
		}
	}

	@Test
	public void testSave() throws Exception {
		IAuctionUser simpleUser = new AuctionUser("Simple User", false);
		boolean falseResult = ia.saveCurrentState(simpleUser);
		
		assertFalse(falseResult);
		
		IAuctionUser adminUser = new AuctionUser("Admin User", true);
		boolean trueResult = ia.saveCurrentState(adminUser);
		
		assertTrue(trueResult);		
	}

	@Test
	public void testRestore() throws Exception {
		IAuctionUser simpleUser = new AuctionUser("Simple User", false);
		boolean falseResult = ia.restoreCurrentState(simpleUser);
		
		assertFalse(falseResult);
		
		IAuctionUser adminUser = new AuctionUser("Admin User", true);
		boolean trueResult = ia.restoreCurrentState(adminUser);
		
		assertTrue(trueResult);
	}
	
	
}
