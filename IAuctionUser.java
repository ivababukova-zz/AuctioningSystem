import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IAuctionUser extends Remote{

	public void notify(String info) throws RemoteException;
	
	public String getName() throws RemoteException; 
	
	public String getId() throws RemoteException;
	
	public boolean isAdmin() throws RemoteException;
}
