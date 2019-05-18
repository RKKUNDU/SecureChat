import java.io.*; 
import java.util.*; 
import java.net.*; 
import java.math.BigInteger;

//Rohit Kundu 
public class Server 
{ 
	static Vector<ClientHandler> ar = new Vector<>(); 
	static int i = 0; 
	public static void main(String[] args) throws IOException 
	{ 
		ServerSocket ss = new ServerSocket(1234); 
		Socket s; 
		while (true) 
		{ 
			s = ss.accept(); 
			System.out.println("New client request received : " + s); 
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 

			BigInteger a=new BigInteger(dis.readUTF());
			BigInteger n=new BigInteger(dis.readUTF());
			BigInteger x=new BigInteger(dis.readUTF());

			int privateKey=(int)(Math.random()*15)+1;
			BigInteger y=a.pow(privateKey).mod(n);

			dos.writeUTF(y.toString());

			BigInteger key=x.pow(privateKey).mod(n);
			// System.out.println("Public Key is "+key);

			ClientHandler mtch = new ClientHandler(s,"clientDefaultName", key, dis, dos); 
			Thread t = new Thread(mtch); 			
			ar.add(mtch); 
			t.start(); 
			i++; 
		} 
	} 
} 
class ShowOnlineUsers
{
	
	public static void show() {
		if(Server.ar.size()==0)
		{
			System.out.println("Nobody is online.");
			return;
		}
		System.out.print(Server.i+" user online. Online User List - [");
		StringBuilder sb=new StringBuilder();
		for (ClientHandler client : Server.ar) 
		{ 
			if(client.isloggedin&& !client.getName().equals("clientDefaultName"))
				sb.append(client.getName()+",");
		} 
		if(sb.length()>0)
			sb.deleteCharAt(sb.length()-1);
		System.out.println(sb+"]");
	} 
	public static void showUsedNickNames() {
		System.out.print("Used Nicknames- [");
		StringBuilder sb=new StringBuilder();
		for (ClientHandler client : Server.ar) 
		{ 
			if(client.isloggedin&& !client.getName().equals("clientDefaultName"))
				sb.append(client.getName()+",");
		} 
		if(sb.length()>0)
			sb.deleteCharAt(sb.length()-1);
		System.out.println(sb+"]");
	} 
}
class ClientHandler implements Runnable 
{ 
	Scanner scn = new Scanner(System.in); 
	private String name; 
	final DataInputStream dis; 
	final DataOutputStream dos; 
	Socket s; 
	boolean isloggedin; 
	private BigInteger key;
	public ClientHandler(Socket s, String name,BigInteger key, DataInputStream dis, DataOutputStream dos) { 
		this.dis = dis; 
		this.dos = dos; 
		this.name = name; 
		this.s = s; 
		this.isloggedin=true; 
		this.key=key;
	} 
	String getName()
	{
		return this.name;
	}
	@Override
	public void run() { 
		String received; 
		boolean nicknameSet=false;
		while (true) 
		{ 
			try
			{ 
				received = dis.readUTF(); 				
				if(!nicknameSet)
				{
					boolean duplicate=false;
					for(ClientHandler c : Server.ar)
					{
						if(c.name.equals(received))
						{
							duplicate=true;
							break;
						}
					}
					if(duplicate){
						dos.writeUTF("Nickname Not Set");
						System.out.println("Nickname not available");
						ShowOnlineUsers.showUsedNickNames();
						continue;
					}
					this.name=received;
					nicknameSet=true;
					System.out.println("Nickname "+received+" is set");
					ShowOnlineUsers.show();
					dos.writeUTF("");
					continue;
				}
				System.out.println("Received Message: "+received); 
				if(received.equals("logout")){ 
					this.isloggedin=false; 
					Server.i--;
					System.out.println(this.name+" has logged out");
					this.s.close(); 
					Server.ar.remove(this);
					ShowOnlineUsers.show();
					break; 
				} 
				received=decrypt(received,this.key.toString());
				if(!received.contains("@"))
				{
					for (ClientHandler client : Server.ar) 
					{ 
						if (client.isloggedin==true&& !client.name.equals(this.name)&& !client.getName().equals("clientDefaultName") ) 
						{ 
							//client.dos.writeUTF(this.name+" : "+received); 
							encrypt(this.name+" : "+received,client.key.toString(),client.dos);
						} 
					} 
					continue;
				}
				StringTokenizer st = new StringTokenizer(received, "@");
				String MsgToSend = st.nextToken(); 
				String recipient = st.nextToken(); 
				for (ClientHandler client : Server.ar) 
				{ 
					if (client.name.equals(recipient) && client.isloggedin==true && !this.name.equals(recipient)) 
					{ 
						//client.dos.writeUTF(this.name+" : "+MsgToSend); 
						encrypt(this.name+" : "+MsgToSend,client.key.toString(),client.dos);
						break; 
					} 
				} 
			} catch (IOException e) {
				e.printStackTrace();
				this.isloggedin=false;
				Server.i--;
				ShowOnlineUsers.show();
				break;
			} 
		} 
		try
		{ 
			this.dis.close(); 
			this.dos.close(); 
			
		}catch(IOException e){ 
			e.printStackTrace(); 
		} 
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
		System.out.println("Decrypted Message: "+sb);
		return new String(sb);
	}
} 
