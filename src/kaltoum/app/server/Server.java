package kaltoum.app.server;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;

import javafx.concurrent.Task;

public class Server {
	private ArrayList<String> Patients;
	private String csvFile = "./1.csv";
	private int nbClients;
	private int port;
	private ServerSocket server;
	private Socket[] socket;
	private ObjectOutputStream[] outi;
	private ObjectInputStream[] in;
	
	private Consumer<Serializable> onReceiveCallBack;
	
	//constructor
	public Server(int port,Consumer<Serializable> onReceiveCallBack) throws FileNotFoundException {
		//Initialize number of clients
		nbClients=0;
		//initialize fields
		this.onReceiveCallBack=onReceiveCallBack;
		this.port=port;
		this.socket=new Socket[3];
		this.outi=new ObjectOutputStream[3];
		this.in=new ObjectInputStream[3];
		//read from the csvFile
		
		Scanner s=new Scanner(new File(csvFile));
		Patients=new ArrayList<String>();
		while(s.hasNext()) Patients.add(s.next());
		s.close();
		
		//launch the thread responsible for receiving and sending sockets
		Task<Void> task=new ConnectionThread();
		Thread t=new Thread(task);
		t.setDaemon(true);
		t.start();
	}
	
	public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}

	class ConnectionThread extends Task<Void>{

		@Override
		protected Void call() throws Exception {
			try( ServerSocket server = new ServerSocket(port)){
				setServer(server);
				
				while(nbClients<3) {
					try {
						System.out.println(nbClients);
					socket[nbClients]=server.accept();
					Task<Void> task= new EchoTask(socket[nbClients]);
					Thread t=new Thread(task);
					t.start();
					nbClients++;
					
					}catch(Exception e) {
						System.out.println("Connexion Closed!  1");
					}
					
				}
				
				
				
			}catch(Exception e) {System.out.println("Connexion Closed!  1.5");}
			return null;
		}
		
	}
	private class EchoTask extends Task<Void>{
		private Socket socket;
		//private ObjectOutputStream out;
		
		public EchoTask(Socket socket) {
			this.socket=socket;
		}

		@Override
		protected Void call() throws Exception {
			try(ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
				
					FileWriter w= new FileWriter(csvFile,true);
					ObjectInputStream in=new ObjectInputStream(socket.getInputStream()))
			{
				for(int i=0;i<Patients.size();i++) out.writeObject(Patients.get(i));
				outi[nbClients-1]=out;
				
				socket.setTcpNoDelay(true);
				
				while(true) {
					Serializable data=(Serializable)in.readObject();
					
						
						Send(data,nbClients,w);
						
					onReceiveCallBack.accept(data);
					
				}
			}catch(Exception e) {System.out.println("Connexion Closed!  2");}
			
			return null;
		}
		
	}
	public void Send(Serializable data,int nbClients,FileWriter w) throws Exception{
		Patients.add(data.toString());
		w.append(data.toString());
		for(int i=0;i<nbClients;i++)
		outi[i].writeObject(data);
		
	}
	public void closeConnection() throws Exception{
		for(int i=0;i<nbClients;i++)
			socket[i].close();
	}

}
