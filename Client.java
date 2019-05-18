import java.io.*;
import java.math.BigInteger;
import java.net.*; 
import java.util.Scanner;
//Rohit Kundu
public class Client 
{ 
	final static int ServerPort = 1234; 
	public static void main(String args[]) throws UnknownHostException, IOException 
	{ 
		Scanner scn = new Scanner(System.in); 
		InetAddress ip = InetAddress.getByName("localhost"); 
		Socket s = new Socket(ip, ServerPort); 
		DataInputStream dis = new DataInputStream(s.getInputStream()); 
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());

		int rand1=(int)(Math.random()*10000)+1111;
		long rand2=(long)(Math.random()*10000000000000000L)+(long)11111111111L;

		BigInteger a=new BigInteger(Integer.toString(rand1));
		BigInteger n=new BigInteger(Long.toString(rand2));
		int privateKey=(int)(Math.random()*15)+1;

		BigInteger x=a.pow(privateKey).mod(n);

		dos.writeUTF(a.toString());
		dos.writeUTF(n.toString());
		dos.writeUTF(x.toString());
		// System.out.print("Client : a="+a+" n="+n+" x="+x);

		BigInteger y=new BigInteger(dis.readUTF());
		// System.out.println(" y="+y);

		BigInteger key=y.pow(privateKey).mod(n);
		// System.out.println("Public Key is "+key);
		String NickName="";
		String input="Nickname Not Set";
		while(input.equals("Nickname Not Set")){
			while(NickName.equals("")){
				System.out.println("Please set your nick name to begin chatting");
				NickName=scn.nextLine();
				NickName=NickName.trim();
			}
			dos.writeUTF(NickName);
			input=dis.readUTF();
			NickName="";
		}
		Thread sendMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 
				while (true) { 
					try { 
						String msg = scn.nextLine(); 
						if(msg.equals(""))
							continue;
						if(msg.equalsIgnoreCase("logout"))
						{
							dos.writeUTF(msg);
							s.close();
							System.out.println("logged out");
							break;
						}
						//dos.writeUTF(msg); 
						encrypt(msg,key.toString(),dos);
					} catch (IOException e) { 
						e.printStackTrace(); 
						break;
					} 
				} 
			} 
		}); 
		Thread readMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 

				while (true) { 
					try { 
						String msg = dis.readUTF(); 
						System.out.println(decrypt(msg,key.toString())); 
					} catch (IOException e) { 
						// e.printStackTrace(); 
						break;
					} 
				} 
			} 
		}); 
		sendMessage.start(); 
		readMessage.start(); 
	} 
	static void encrypt(String msg,String key,DataOutputStream dos) throws IOException
	{
		StringBuffer sb=new StringBuffer("");
		for(int i=0;i<msg.length();i++)
		{
			sb.append((char)(msg.charAt(i)+key.charAt(i%key.length())));
		}
		// System.out.println("Encrypted Message: "+sb);
		dos.writeUTF(new String(sb));
	}
	static String decrypt(String msg,String key) throws IOException
	{
		StringBuffer sb=new StringBuffer("");
		for(int i=0;i<msg.length();i++)
		{
			sb.append((char)(msg.charAt(i)-key.charAt(i%key.length())));
		}
		// System.out.println("Decrypted Message: "+sb);
		return new String(sb);
	}
} 
