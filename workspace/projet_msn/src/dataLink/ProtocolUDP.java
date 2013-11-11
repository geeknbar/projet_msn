/**
 * 
 */
package dataLink;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

import clientServer.ThreadListenerUDP;

/**
 * @author Mickael
 * 
 */
public class ProtocolUDP
{
	public DatagramSocket socket;
	public DatagramPacket writer;
	public DatagramPacket reader;
	private static byte bufferReader[];
	private final static int sizeBufferReader = 60000;

	public ProtocolUDP(DatagramPacket writer, DatagramPacket reader)
	{
		this.writer = writer;
		this.reader = reader;
	}

	public ProtocolUDP(DatagramSocket socket)
	{
		this.socket = socket;
		this.writer = new DatagramPacket(null, 0);
		this.reader = new DatagramPacket(bufferReader, sizeBufferReader);
	}

	public ProtocolUDP() {
		// TODO Auto-generated constructor stub
	}

	public void sendMessage(String message)
	{
		byte buffer[];
		try
		{
			buffer = message.getBytes("UTF-8");
			int length = buffer.length;
			this.writer = new DatagramPacket(buffer, length);
			this.socket.send(writer);
		} catch (IOException e)
		{
			System.err.println("Erreur d'envoie du message de ProtocolUDP, message:" + e.getMessage());
		}
	}

	public String readMessage()
	{
		try
		{
			bufferReader = new byte[sizeBufferReader];
			DatagramPacket data = new DatagramPacket(bufferReader, sizeBufferReader);
			socket.receive(data);
			return new String(data.getData());
		} catch (IOException e)
		{
			System.err.println("Erreur de reception de message de ProtocolUDP, message:" + e.getMessage());
		}
		return null;
	}

	public DatagramPacket getWriter()
	{
		return writer;
	}

	public void setWriter(DatagramPacket writer)
	{
		this.writer = writer;
	}

	public DatagramPacket getReader()
	{
		return reader;
	}

	public void setReader(DatagramPacket reader)
	{
		this.reader = reader;
	}
}
