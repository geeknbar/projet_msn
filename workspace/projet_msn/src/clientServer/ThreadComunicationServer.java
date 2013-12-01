package clientServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import dataLink.Protocol;
import dataLink.ProtocolTCP;

/**
 * 
 * @author Dorian, Mickaël, Raphaël, Thibault
 * 
 * Thread de comunication d'un serveur vers un client. Il permet de gérer les
 * demandes client. Connection, Déconnection, demande de lien etc...
 * 
 */
public class ThreadComunicationServer extends Thread
{
	/**
	 * Serveur à qui appartient le thread de communication.
	 * 
	 * @see Server
	 */
	private Server server;
	/**
	 * Socket de communication.
	 * 
	 * @see Socket
	 */
	private Socket socket;
	/**
	 * Protocol de communication.
	 * 
	 * @see Protocol
	 */
	private Protocol protocol;
	/**
	 * Paramètre permettant d'arrêter le thread.
	 */
	private boolean running;
	/**
	 * Variable temporaire utile au traitement des requêtes.
	 */
	private String tempVar;

	/**
	 * Constructeur par défaut du Thread.
	 * 
	 * @param server
	 * @param socket
	 */
	public ThreadComunicationServer(Server server, Socket socket)
	{
		this.server = server;
		this.socket = socket;
		this.protocol = new ProtocolTCP(socket);
	}

	@Override
	public void run()
	{
		try
		{
			System.out.println("Lancement du Thread de communication");
			this.running = true;
			while (running)
			{
				Thread.sleep(500);
				// On attent que le client nous envoie un message
				String message = protocol.readMessage();
				// On traite ensuite le message reçu.
				this.messageTraitement(message);
			}
			System.out.println("Arret du Thread de communication");
			this.socket.close();
			this.protocol.close();
		} catch (IOException | InterruptedException e)
		{
			System.err.println("Erreur du ThreadComunicationServer, message: " + e.getMessage());
		}
	}

	/**
	 * Méthode permettant de traiter la réception d'un message.
	 * 
	 * @param message
	 */
	private void messageTraitement(String message)
	{
		StringTokenizer token = new StringTokenizer(message, ":");
		System.out.println("Début du traitement du message : " + message);
		String firstToken = token.nextToken();
		if (token.hasMoreTokens())
		{
			String nextToken = token.nextToken();
			switch (firstToken)
			{
			case "request":
				this.messageTraitementRequest(nextToken, token);
				break;
			case "reply":
				this.messageTraitementReply(nextToken, token);
				break;
			case "end":
				this.stopThread();
				break;

			default:
				this.stopThread();
				break;
			}
		}
	}

	/**
	 * Méthode permettant de traiter les demandes d'un serveur.
	 * 
	 * @see Server
	 * @param message
	 * @param token
	 */
	private void messageTraitementRequest(String message, StringTokenizer token)
	{
		switch (message)
		{
		case "register":
			this.registerClient(token);
			break;
		case "unregister":
			this.unregisterClient(token);
			break;
		case "list":
			this.askListClient(token);
			break;
		case "clientConnection":
			this.getClientConnection(token);
			break;
		default:
			break;
		}
	}

	/**
	 * Message permettant de traiter les réponses d'un serveur.
	 * 
	 * @see Server
	 * @param message
	 * @param token
	 */
	private void messageTraitementReply(String message, StringTokenizer token)
	{
		switch (message)
		{
		case "register":
			this.registerClient(token);
			break;
		case "unregister":
			this.unregisterClient(token);
			break;
		case "list":
			this.askListClient(token);
			break;
		case "clientConnection":
			this.getClientConnection(token);
			break;

		default:
			this.stopThread();
			break;
		}
	}

	/**
	 * Méthode permettant traiter le processus de desenregistrement
	 * 
	 * @param token
	 */
	private void unregisterClient(StringTokenizer token)
	{
		if (token.hasMoreTokens())
		{
			String id = token.nextToken();
			this.server.removeClient(id);
			this.protocol.sendMessage("reply:unregister:DONE");
			this.stopThread();
		} else
		{
			this.protocol.sendMessage("reply:unregister:ERROR");
			this.stopThread();
		}
	}

	/**
	 * Méthode permettant de gérer le processus d'enregistrement.
	 * 
	 * @param token
	 */
	private void registerClient(StringTokenizer token)
	{
		// Si on a un élément de plus dans le token, alors il s'agit d'un reply
		if (token.hasMoreTokens())
		{
			String nextToken = token.nextToken();
			if (token.hasMoreTokens())
			{
				switch (nextToken)
				{
				case "name":
					// On informe le client qu'on a bien reçu son nom
					// this.protocol.sendMessage("reply:register:name:OK");
					this.tempVar = token.nextToken();
					if (!this.tempVar.trim().equals(""))
					{
						this.protocol.sendMessage("request:register:port");
					} else
					{
						this.protocol.sendMessage("reply:register:ERROR");
						this.stopThread();
					}
					break;
				case "port":
					// this.protocol.sendMessage("reply:register:port:OK");
					if (token.hasMoreTokens())
					{
						String stringPort = token.nextToken();
						int port = Integer.parseInt(stringPort);
						String id = this.server.addClient(this.tempVar, this.socket, port);
						if (id != null)
						{
							this.protocol.sendMessage("reply:register:id:" + id);
							// System.out.println(this.server.getClients());
							// this.stopThread();
						} else
						{
							this.protocol.sendMessage("reply:register:ERROR");
							this.stopThread();
						}
					} else
					{
						this.protocol.sendMessage("reply:register:ERROR");
					}
					break;
				case "id":
					this.protocol.sendMessage("reply:register:DONE");
					System.out.println(this.server.getClients());
					this.stopThread();
					break;
				default:
					this.stopThread();
					break;
				}
			} else
			{
				this.protocol.sendMessage("reply:register:ERROR");
				this.stopThread();
			}
		}
		// Sinon c'est qu'il s'agit d'une request
		else
		{
			System.out.println("Demande d'enregistrement");
			// On envoie un autre pour demander son nom
			this.protocol.sendMessage("request:register:name");
		}
	}

	/**
	 * Méthode permettant de gérer le processus de demande de list Client au
	 * serveur.
	 * 
	 * @param token
	 */
	public void askListClient(StringTokenizer token)
	{
		if (token.hasMoreTokens())
		{
			this.stopThread();
		} else
		{
			this.protocol.sendMessage("reply:list:" + this.server.getListClient());
		}
	}

	/**
	 * Méthode permettant de gérer le processus de demande d'information de
	 * connection client.
	 * 
	 * @param token
	 */
	public void getClientConnection(StringTokenizer token)
	{
		if (token.hasMoreTokens())
		{
			String nextToken = token.nextToken();
			if (nextToken.length() > 20)
			{
				String requested = this.server.getClient(nextToken);
				if (requested != null)
				{
					this.protocol.sendMessage("reply:clientConnection:" + requested);
				} else
				{
					this.protocol.sendMessage("reply:clientConnection:ERROR");
					this.stopThread();
				}
			} else
			{
				if (nextToken.equals("DONE"))
				{
					this.stopThread();
				} else if (nextToken.equals("ERROR"))
				{
					this.stopThread();
				} else
				{
					this.protocol.sendMessage("reply:clientConnection:ERROR");
					this.stopThread();
				}
			}
		} else
		{
			this.protocol.sendMessage("reply:clientConnection:ERROR");
			this.stopThread();
		}
	}

	/**
	 * Méthode permettant d'arrêter le thread
	 */
	public void stopThread()
	{
		this.running = false;
	}

	public Socket getSocket()
	{
		return socket;
	}

	public void setSocket(Socket socket)
	{
		this.socket = socket;
	}

	public Protocol getProtocol()
	{
		return protocol;
	}

	public void setProtocol(Protocol protocol)
	{
		this.protocol = protocol;
	}

	public boolean isRunning()
	{
		return running;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}
}
