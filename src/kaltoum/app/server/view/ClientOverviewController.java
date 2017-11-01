package kaltoum.app.server.view;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import kaltoum.app.server.Client;

public class ClientOverviewController {
	@FXML
	private ListView<String> namesPatients;
	
	@FXML
	private Label nachnamePatient;
	@FXML
	private Label vornamePatient;
	@FXML
	private Label diagnosePatient;
	@FXML
	private Label nebendiagnosePatient;
	@FXML
	private TextField nachname;
	@FXML
	private TextField vorname;
	@FXML
	private TextField diagnose;
	@FXML
	private TextField nebendiagnose;
	@FXML
	private Label statu;
	@FXML
	
	private StringBuilder sb;
	private TextArea messages=new TextArea();;
	
	private Client client;
	//private ObservableList<Patient> patientData = FXCollections.observableArrayList();
	
	public ClientOverviewController() {
		
	}
	
	
	private void showPatientDetails(String text) {
		if(text!=null) {
			int i=0;
			while( !client.getPatients().get(i).startsWith(text)) i++;
			
		String[] parts=client.getPatients().get(i).split(",");
		nachnamePatient.setText(parts[0]);
		vornamePatient.setText(parts[1]);
		diagnosePatient.setText(parts[2]);
		nebendiagnosePatient.setText(parts[3]);
		}else {
			nachnamePatient.setText("");
			vornamePatient.setText("");
			diagnosePatient.setText("");
			nebendiagnosePatient.setText("");
		}
		}
	
	
	@FXML
	private void initialize() throws Exception {
		

		client=createClient();
		client.startConnection();
		statu.setText("Connected");
		
		namesPatients.setItems(client.getPatientData());
		namesPatients.getSelectionModel().selectedItemProperty().addListener(
	            (observable, oldValue, newValue) -> showPatientDetails(newValue));
		
		}
	
	@FXML
	private void handleEintragen() throws IOException {
		String message="";
		message+=nachname.getText();
		message+=",";
		message+=vorname.getText();
		message+=",";
		message+=diagnose.getText();
		message+=",";
		message+=nebendiagnose.getText();
		message+="\n";
		
		//message+=sb.toString();
		namesPatients.setItems(client.getPatientData());
		nachname.clear();
		vorname.clear();
		diagnose.clear();
		nebendiagnose.clear();
		System.out.println(message);
		try {
			client.Send(message);
		} catch (Exception e) {
			System.out.println("Did not send m8");
		}
		
	}
	
	@FXML
	private void handleStop() throws Exception {
		statu.setText("Disconnected");
		client.closeConnection();
		
	}
	
	
	private Client createClient() {
		return new Client("127.0.0.1",7777,data -> {
				Platform.runLater(()-> {
					messages.appendText(data.toString() + "\n");
					
					
			        
			        
					});
				});}
	

}
