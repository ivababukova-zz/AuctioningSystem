import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;


public class AuctionUser extends UnicastRemoteObject implements IAuctionUser{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private UUID uuid;
	private boolean isAdmin;
	
	
	protected AuctionUser(String name, boolean isAdmin) throws RemoteException {
		super();
		this.name = name;
		this.uuid = UUID.randomUUID();
		this.isAdmin = isAdmin;
	}

	@Override
	public void notify(String info) throws RemoteException {
		System.out.println("INFO: " + info);
		
	}

	@Override
	public String getName() throws RemoteException {
		return this.name;
	}

	@Override
	public String getId() throws RemoteException {
		return this.uuid.toString();
	}

	@Override
	public boolean isAdmin() throws RemoteException {
		return this.isAdmin;
	}

}
