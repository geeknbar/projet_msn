/**
 * 
 */
package clientServer;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

/**
 * @author Mickael
 * 
 */
public class Server extends AbstractClientServer
{
	// Liste des clients que connait le serveur

	// Thread d'ecoute du serveur
	private ThreadListenerTCP threadListener;

	/**
	 * Construct Server() Constructeur le la class Server. Initialise les
	 * variables server,clients et threadListener. En ouvrant sur le port 30972
	 */
	public Server()
	{
		super();
		this.threadListener = new ThreadListenerTCP(this, 30970);
	}

	/**
	 * Construct Server(int port) Constructeur de la class Server. Initialise
	 * les variables server,clients et threadListener.
	 * 
	 * @param int numero de port
	 */
	public Server(int port)
	{
		super();
		this.threadListener = new ThreadListenerTCP(this, port);
	}

	/**
	 * Method launch() Méthode permettant de lancer le serveur.
	 */
	public void launch()
	{
		this.threadListener.start();
	}

	/**
	 * Method stopServer() Méthode permettant de stopper le serveur.
	 */
	public void stopServer()
	{
		this.threadListener.stopThread();
	}

	/**
	 * Method addClient
	 * 
	 * @param name
	 *            Nom du client
	 * @param client
	 *            Socket du client
	 * @return true si client ajouté, false sinon.
	 */
	public String addClient(String name, Socket client, int listeningUDPPort)
	{
		if (this.getClients().add(new ClientServerData(name, client.getInetAddress(), listeningUDPPort)))
		{
			return this.getClients().lastElement().getId();
		} else
		{
			return null;
		}
	}

	/**
	 * Method removeClient
	 * 
	 * @param client
	 *            {@link ClientServerData}
	 * @return true si réussit, false sinon
	 */
	public boolean removeClient(ClientServerData client)
	{
		return this.getClients().remove(client);
	}

	/**
	 * Method removeClient
	 * 
	 * @param name
	 *            Nom du client
	 * @return true si réussit, false sinon
	 */
	public boolean removeClient(String id)
	{
		boolean erase = false;
		ClientServerData eraseClient = null;
		for (ClientServerData client : this.getClients())
		{
			if (client.getId().equals(id))
			{
				eraseClient = client;
				erase = true;
			}
		}
		if (erase)
		{
			this.getClients().remove(eraseClient);
		}
		return erase;
	}

	/**
	 * Method removeClient
	 * 
	 * @param ip
	 *            Ip du client {@link InetAddress}
	 * @return true si reussit, false sinon
	 */
	public boolean removeClient(InetAddress ip)
	{
		boolean erase = false;
		for (ClientServerData client : this.getClients())
		{
			if (client.getIp().equals(ip))
			{
				this.getClients().remove(client);
				erase = true;
			}
		}
		return erase;
	}

	public String getListClient()
	{
		String ret = "";
		boolean firstOne = true;
		for (ClientServerData client : this.getClients())
		{
			ret += ((firstOne) ? "" : ",") + client.getId() + "-" + client.getName();
			firstOne = false;
		}
		return ret;
	}

	public String getClient(String id)
	{
		for (ClientServerData client : this.getClients())
		{
			if (client.getId().equals(id))
			{
				return client.getId() + "," + client.getName() + "," + client.getIp().getCanonicalHostName() + "," + client.getPort();
			}
		}
		return null;
	}

	public ThreadListenerTCP getThreadListener()
	{
		return threadListener;
	}

	public void setThreadListener(ThreadListenerTCP threadListener)
	{
		this.threadListener = threadListener;
	}

	@Override
	public void treatIncomeTCP(Object object)
	{
		if (object instanceof Socket)
		{
			ThreadComunicationServer threadClientCom = new ThreadComunicationServer(this, (Socket) object);
			threadClientCom.start();
		} else
		{
			System.err.println("Erreur serveur, treatIncome: mauvaise argument");
		}
	}

	@Override
	public void treatIncomeUDP(String message)
	{

	}

	public static void main(String[] args)
	{
		Server server = new Server();
		server.launch();
	}
}
