package kaltoum.app.server;




import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;





public class Client {
	private ArrayList<String> Patients;
	private ObservableList<String> patientData = FXCollections.observableArrayList();
	public ObservableList<String> getPatientData(){
		return patientData;
	}
	private String ip;
	private int port;
	private ConnectionThread connThread=new ConnectionThread();
	private Consumer<Serializable> onReceiveCallBack;
	
	public Client(String ip, int port,Consumer<Serializable> onReceiveCallBack) {
		Patients=new ArrayList<String>();
		this.onReceiveCallBack=onReceiveCallBack;
		connThread.setDaemon(true);
		
		this.ip=ip;
		this.port = port;
	}
	public ArrayList<String> getPatients(){
		return Patients;
	}

	protected boolean isServer() {
		
		return false;
	}

	
	protected String getIp() {
		
		return ip;
	}

	
	protected int getPort() {
		
		return port;
	}
	
	public void startConnection() throws Exception{
		connThread.start();
		
	}
	public void Send(Serializable data) throws Exception{
		
			connThread.out.writeObject(data);
		
	}
	public void closeConnection() throws Exception{
		for(int i=0;i<Patients.size();i++)
			System.out.println(getPatients().get(i));
		connThread.socket.close();
	}
	
	

	
	private class ConnectionThread extends Thread{
		private Socket socket;
		private ObjectOutputStream out;
		
		
		@Override
		public void run() {
			try( 
					Socket socket=  new Socket(getIp(),getPort());
					ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
				
					
					ObjectInputStream in=new ObjectInputStream(socket.getInputStream());
					
					){
				this.socket=socket;
				this.out=out;
				socket.setTcpNoDelay(true);
				while(true) {
					Serializable data=(Serializable)in.readObject();
					TreatData(data);
					onReceiveCallBack.accept(data);
					
				}
				
				
			}catch(Exception e) {
				onReceiveCallBack.accept("Connexion Closed!");
				
			}
			
		}
	}
	private void TreatData(Serializable message) {
		Patients.add(message.toString());
		String[] parts=message.toString().split(",");
		String result=parts[0]+","+parts[1];
		Task<Void> task= new addTask(result);
		Thread t=new Thread(task);
		t.start();
		
		
	}
	
	private class addTask extends Task<Void>{
		String result;
		public addTask(String message) {
			result= message;
		}

		@Override
		protected Void call() throws Exception {
			patientData.add(result);
			return null;
		}
		
	}

}
