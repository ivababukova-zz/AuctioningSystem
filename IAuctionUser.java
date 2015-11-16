import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IAuctionUser extends Remote{

	/**
	 * Method used for notifying the user a specific
	 * message specified as a @param
	 * */
	public void notify(String info) throws RemoteException;
	
	/**
	 * returns the name of the user
	 * */
	public String getName() throws RemoteException; 
	
	/**
	 * returns the id of the user
	 * */
	public String getId() throws RemoteException;
	
	/**
	 * is the user administrator or not
	 * */
	public boolean isAdmin() throws RemoteException;
}
