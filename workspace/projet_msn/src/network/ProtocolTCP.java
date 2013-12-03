package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/** 
 * Permet de représenter le protocol TCP pour la communication TCP.
 * 
 * @author Dorian, Mickaël, Raphaël, Thibault
 * @see Protocol
 */
public class ProtocolTCP extends Protocol
{
	/**
	 * 
	 * {@link Socket} Socket de communication.
	 */
	public Socket socket;
	/**
	 * {@link PrintWriter} permettant d'envoyer des messages.
	 */
	public PrintWriter writer;
	/**
	 * {@link BufferedReader} permettant de réceptionner des messages.
	 */
	public BufferedReader reader;

	/**
	 * Constructeur par défaut du protocol par défaut.
	 * 
	 * @param socket socket TCP sur lequel on communique
	 */
	public ProtocolTCP(Socket socket)
	{
		super(socket.getLocalPort());
		try
		{
			this.socket = socket;
			this.writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream())), true);
			this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e)
		{
			System.err.println("Erreur d'initialisation de Protocol, message: " + e.getMessage());
		}
	}
	/**
	 * Méthode permettant d'envoyer un message via une adresse IP et un port
	 * 
	 * @param message message que l'on va envoyer
	 * @param adress adresse IP ou l'on va envoyé le message
	 * @param port port TCP ou l'on va envoyé le message
	 */
	public void sendMessage(String message, InetAddress adress, int port)
	{
		try
		{
			Socket socket = new Socket(adress.getCanonicalHostName(), port);
			PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			writer.println(message);
			socket.close();
		} catch (IOException e)
		{
			System.err.println("Erreur d'envoie de message protocolTCP, message:" + e.getMessage());
		}

	}

	/**
	 * Méthode permettant d'envoyer un message via une adresse IP et un port
	 * 
	 * @param message message que l'on va envoyer
	 */
	public void sendMessage(String message)
	{
		this.writer.println(message);
	}

	/**
	 * Méthode permettant de lire un message
	 * 
	 * @return une string du message lu
	 */
	public String readMessage()
	{
		try
		{
			return this.reader.readLine();
		} catch (IOException e)
		{
			System.err.println("Erreur lecture message dans la class Protocol, message: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Méthode permettant de fermer le protocol
	 * 
	 */
	public void close()
	{
		try
		{
			this.reader.close();
			this.writer.close();
			this.socket.close();
		} catch (IOException e)
		{
			System.err.println("Erreur fermeture dans la class Protocol, message: " + e.getMessage());
		}
	}

	/**
	 * Getter du writer du protocol TCP
	 * 
	 * @return le writer du protocol {@link PrintWriter}
	 */
	public PrintWriter getWriter()
	{
		return writer;
	}

	/**
	 * Setter qui fixe le writer du protocol TCP
	 * 
	 * @param writer
	 *            objet PrintWriter {@link PrintWriter}
	 */
	public void setWriter(PrintWriter writer)
	{
		this.writer = writer;
	}

	/**
	 * Getter du BufferedReader du protocol TCP
	 * 
	 * @return le BufferedReader du protocol TCP{@link BufferedReader}
	 */
	public BufferedReader getReader()
	{
		return reader;
	}

	/**
	 * Setter qui fixe le BufferedReader du protocol TCP
	 * 
	 * @param reader
	 *            objet BufferedReader de java {@link BufferedReader}
	 */
	public void setReader(BufferedReader reader)
	{
		this.reader = reader;
	}
}