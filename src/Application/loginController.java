package Application;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Date;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JOptionPane;

public class loginController implements Initializable {
    
    Image closedEye = new Image("/icons/eyes closed.png");
    Image openEye = new Image("/icons/eyes open.png");
    
    @FXML
    ImageView eyesImageView;
    
    @FXML
    private Pane loginPane;

    @FXML
    private Pane signUpPane;

    @FXML
    private TextField email;

    @FXML
    private PasswordField signupPassword;
    
    @FXML
    private TextField signupName;
    
    @FXML
    private TextField signupEmail;
    
    @FXML
    private TextField signupAccountNumber;
    
    @FXML
    private DatePicker signupDOB;    
    
    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField shownPassword;

    @FXML
    private ToggleButton toggleButton;
   
    @FXML
    void showPassword(ActionEvent event) {
        if(toggleButton.isSelected()){
            shownPassword.setVisible(true);
            eyesImageView.setImage(closedEye);
        }
        else{
            shownPassword.setVisible(false);
            eyesImageView.setImage(openEye);
        }
    }
    

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    // Strings which hold css elements to easily re-use in the application
    protected
    String errorStyle = String.format("-fx-border-color: RED;");
    String successStyle = String.format("-fx-border-color: #A9A9A9;");
    
    private boolean emailAlreadyExists() throws SQLException{
        conn = MySQLConnect.connectDB();
        String sql = "Select * From users Where email = ?";
        pst = conn.prepareStatement(sql);
        pst.setString(1, signupEmail.getText());
        ResultSet rs = pst.executeQuery();
        
        return rs.next();
    }
    private boolean accountNumberAlreadyExists() throws SQLException{
        conn = MySQLConnect.connectDB();
        String sql = "Select * From users Where account_number = ?";
        pst = conn.prepareStatement(sql);
        pst.setString(1, signupAccountNumber.getText());
        ResultSet rs = pst.executeQuery();
        return rs.next();
    }
    
    private void validateInput() throws SQLException{
        if(signupName.getText().isBlank() || (signupName.getText().length() < 10)){
            signupName.setStyle(errorStyle);
        }
        else{signupName.setStyle(successStyle);}
        
        if(signupEmail.getText().isBlank() || (emailAlreadyExists())){
            signupEmail.setStyle(errorStyle);
        }
        else{signupEmail.setStyle(successStyle);}

        if(signupAccountNumber.getText().isBlank() || (signupAccountNumber.getText().length() < 8) || accountNumberAlreadyExists()){
            signupAccountNumber.setStyle(errorStyle);
        }
        else{signupAccountNumber.setStyle(successStyle);}
        
        if(signupPassword.getText().isBlank() || (signupPassword.getText().length() < 8)){
            signupPassword.setStyle(errorStyle);
        }
        else{signupPassword.setStyle(successStyle);}
        if(signupDOB.getValue().toString().isBlank()){
            signupDOB.setStyle(errorStyle);
        }
        else{signupDOB.setStyle(successStyle);}
    }
    
    
    private void closeWindow(){
          Stage stage = (Stage) email.getScene().getWindow();
          stage.close();}

    @FXML
    private void Login(ActionEvent event) throws Exception{
        conn = MySQLConnect.connectDB();
        String sql = "Select * From users Where email = ? And password = ?";
        try{
            pst = conn.prepareStatement(sql);
            pst.setString(1, email.getText());
            pst.setString(2, loginPassword.getText());
            rs = pst.executeQuery();
            if(rs.next()){
                JOptionPane.showMessageDialog(null, "Successful login!");
                closeWindow();
                try{
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    DashboardController dController = fxmlLoader.getController();
                    User c = new User(rs.getString(2), rs.getString(3) , rs.getDate(6).toLocalDate(), rs.getInt(4), rs.getDouble(5));
                    dController.currentUser = c;
                    dController.setLabels();
                    Stage stage = new Stage();      
                    stage.setTitle("ABC Bank");
                    stage.getIcons().add(new Image("/icons/icon.png"));
                    stage.setScene(new Scene(root1));
                    stage.show();
                } catch(Exception e){
                    System.out.println(e.getCause());
                    System.out.println(e.getStackTrace());
                }
            }
            else{
                email.setStyle(errorStyle);
                loginPassword.setStyle(errorStyle);
                JOptionPane.showMessageDialog(null, "Email or Password Is Invalid!");
            }
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    
    public void addUser(ActionEvent Action) throws SQLException{
        conn = MySQLConnect.connectDB();
        String sql = "insert into users (name, email, account_number,balance ,dob, password) values (?,?,?,?,?,?)";
        try{
            validateInput();
            User newUser = new User(signupName.getText(), signupEmail.getText(), signupDOB.getValue(), Integer.parseInt(signupAccountNumber.getText()));
            pst = conn.prepareStatement(sql);
            pst.setString(1, newUser.getName());
            pst.setString(2, newUser.getEmail());
            pst.setInt(3, newUser.getAcc_num());
            pst.setDouble(4, newUser.getBalance());
            pst.setDate(5,Date.valueOf(newUser.getDob()));
            pst.setString(6, signupPassword.getText());
            pst.execute();
            JOptionPane.showMessageDialog(null, "Successful SignUp!");
            showLoginPane();
        }
        catch(Exception e){
            //JOptionPane.showMessageDialog(null, e);
            JOptionPane.showMessageDialog(null, "Please Fill In The Data Correctly!");
        }
    }
    
    public void showLoginPane(){
        loginPane.setVisible(true);
        signUpPane.setVisible(false);
    }
    
    public void showSignUPPane(){
        loginPane.setVisible(false);
        signUpPane.setVisible(true);
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Bindings.bindBidirectional(signupPassword.textProperty(), shownPassword.textProperty());
        showLoginPane();
    }
}
