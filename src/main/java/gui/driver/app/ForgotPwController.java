package gui.driver.app;

import java.util.HashMap;
import java.util.Map;
import data.management.DBManager;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.EmailNotification;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class ForgotPwController {
	
	/**
	 * @author dshames
	 *  This class contains the controller of the forgot my password screen
	 */
	
	@FXML
	private Button backButton;
	@FXML
	private TextField idField;
	@FXML
	private TextField emailField;
	@FXML
	private Button restoreButton;
	@FXML
	private Label statusLabel;
	@FXML
	private ProgressIndicator progressIndicator;
	
	@FXML
    protected void initialize(){
		progressIndicator.setVisible(false);
		statusLabel.setVisible(false);
		idField.setFocusTraversable(false);
		emailField.setFocusTraversable(false);
	}
	@FXML
	public void backButtonClicked(ActionEvent event) throws Exception {
		Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
		window.setTitle("Login");
		Parent root = FXMLLoader.load(getClass().getResource("LoginScreen.fxml")); 
		window.setScene(new Scene(root,400,550));		
		window.show();
	}
		
	@FXML
	public void restoreButtonClicked(ActionEvent event) throws Exception {
		
		String id = idField.getText();
		String email = emailField.getText();
		statusLabel.setText("ID or Email are wrong");
		
		if (id.equals("") || email.equals("")){
			statusLabel.setVisible(true);
			return;		
		}
		
		statusLabel.setText("Loading...");
		statusLabel.setStyle(" -fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: normal");
		statusLabel.setVisible(true);
		
		Task<Map<String, Object>> getDetailsTask = new Task<Map<String, Object>>() {
            @Override
            protected Map<String, Object> call() throws Exception {	
        		Map<String, Object> key = new HashMap<String, Object>();
        		key.put("id", id);
            	return DBManager.getObjectFieldsByKey("Driver", key);
	        }
        };
       new Thread(getDetailsTask).start();
       
       progressIndicator.progressProperty().bind(getDetailsTask.progressProperty());
       progressIndicator.setVisible(true); 
       
       getDetailsTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
           @Override
           public void handle(WorkerStateEvent workerStateEvent) {
        	  
        	   Map<String, Object> result =  getDetailsTask.getValue();
        	   String emailFromDb = (String)result.get("email");
        	   String pwFromDb = (String)result.get("password");
        	   
        		if (emailFromDb == null || !emailFromDb.equals(email)) {
        			System.out.println(emailFromDb);
        			statusLabel.setText("ID or Email are wrong");
        			statusLabel.setStyle(" -fx-text-fill: red; -fx-font-size: 15px; -fx-font-weight: bold");
        			statusLabel.setVisible(true);
        			progressIndicator.setVisible(false);
        		} else {
        			
	            	statusLabel.setText("Password Sent. Redirecting to login");
        			statusLabel.setStyle(" -fx-text-fill: green; -fx-font-size: 15px; -fx-font-weight: bold");
        			statusLabel.setVisible(true);
        			Task<Void> sendingEmailTask = new Task<Void>() {
        	            @Override
        	            protected Void call() throws Exception {
        	            	EmailNotification.PasswordReset(emailFromDb, pwFromDb);
        	            	Thread.sleep(1500);
        	        		return null;
        		        }
        	        };
        	       new Thread(sendingEmailTask).start();
        	       
        	       progressIndicator.progressProperty().bind(sendingEmailTask.progressProperty());
        	       progressIndicator.setVisible(true); 
        	       
        	       sendingEmailTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
        	           @Override
        	           public void handle(WorkerStateEvent workerStateEvent) {
        	        	   try{
	        	      		 Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
	        	 			window.setTitle("Login");
	        	 			Parent root = FXMLLoader.load(getClass().getResource("LoginScreen.fxml")); 
	        	 			window.setScene(new Scene(root,400,550));		
	        	 			window.show();
        	        	   } catch(Exception e){
        	        		   
        	        	   }
        	           }
        	       });
        	       
        			
        		}
               
           }
       });
	}
}