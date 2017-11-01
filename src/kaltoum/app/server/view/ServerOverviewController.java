package kaltoum.app.server.view;

import java.io.FileNotFoundException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import kaltoum.app.server.*;


public class ServerOverviewController {
	private TextArea messages= new TextArea();
	@FXML
	private TextField port;
	@FXML
	private Label status;
	
	@FXML
	private void initialize() {
		port.setText("7777");
		status.setText("Disconnected");
	}
	
public ServerOverviewController() {
	
		
	}
 private Server s;
	@FXML
	private void handleStart() throws FileNotFoundException {
		int p=Integer.parseInt(port.getText());
		s=createServer(p);
		
	}
	@FXML
	private void handleStop() throws Exception {
		status.setText("Disconnected");
		s.closeConnection();
		
	}
	private Server createServer(int port) throws FileNotFoundException {
		status.setText("Connected");
		return new Server(port ,data -> {
				Platform.runLater(()-> {
					messages.appendText(data.toString() + "\n");
					});
				});}

}
