package view;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.JDBCUtil;

public class LoginController {
	@FXML
	private TextField phone;
	@FXML
	private PasswordField pw;
	@FXML
	private TextField name;
	@FXML
	private Button loginBtn;
	@FXML
	private Button joinBtn;
	

	
	public void initialize() {
		System.out.println("initialize");
		
		//log_info �ʱ�ȭ 
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		String logOutSql = "delete from log_info";

		try {
			pstmt  = con.prepareStatement(logOutSql);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("���� ����!");
		} 
	}
	
	
	//ȭ����ȯ �޼ҵ�
	public void changeScene(String url, Button btn) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(url));
			AnchorPane root = (AnchorPane)loader.load();
			Scene scene = new Scene(root,800,500);
			
			Stage primaStage = (Stage) btn.getScene().getWindow();
			primaStage.setScene(scene);
			primaStage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void loginBtnHandler() {
		String inputPhone = phone.getText();
		String inputPw = pw.getText();
		String inputName = name.getText();
		
		loginCheck(inputPhone, inputPw, inputName);
	}
	
	public void joinBtnHandler() {
		String inputPhone = phone.getText();
		String inputPw = pw.getText();
		String inputName = name.getText();
		
		if (inputPhone.equals("") || inputPw.equals("") || inputName.equals("")) {
			Alert pleaseInputALert = new Alert(AlertType.INFORMATION);
			pleaseInputALert.setTitle("�Է� �ʿ�");
			pleaseInputALert.setHeaderText("���Կ� �ʿ��� ��� �ʵ带 �Է����ּ���.");
			pleaseInputALert.show();	
		} else {
			insertUser(inputPhone, inputPw, inputName);	
			
			phone.setText("");
			pw.setText("");
			name.setText("");
		}
	}
	
	
	
	//�α��� Ȯ�� ó��
	public void loginCheck(String inputPhone, String inputPw, String inputName) {
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		String loginSql = "SELECT * FROM `users` WHERE phone='" + inputPhone + "' AND pw='" + inputPw + "' AND name='" + inputName + "'";
		
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(loginSql);
			rs = pstmt.executeQuery();
			
			
			if (rs.next()) {
				String type = rs.getString("type");
				
				
				//�α����� ��
				String logSetSql = "insert into log_info(`phone`, `name`) value('" + rs.getString("phone") + "', '" + rs.getString("name") + "')";

				try {
					pstmt  = con.prepareStatement(logSetSql);
					pstmt.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("���� ����!");
				} 
				
				if (type.equals("admin")) {
					//�α���->������������ ��ȯ
					changeScene("/view/AdminLayout.fxml", joinBtn);
				} else {
					//�α���->���� ��ȯ
					changeScene("/view/MainLayout.fxml", joinBtn);	
				}

					
			} else {
				Alert pleaseLoginALert = new Alert(AlertType.INFORMATION);
				pleaseLoginALert.setTitle("�α��� �ʿ�");
				pleaseLoginALert.setHeaderText("��ġ�ϴ� ���̵�, ��й�ȣ, �̸��� �����ϴ� :(");
				pleaseLoginALert.setContentText("�ٽ� �Է����ּ���.");
				pleaseLoginALert.show();	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("���� ����!");
		}
	}
	
	
	//ȸ������ ó��
	public void insertUser(String phone, String pw, String name) {
		//DB
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		String joinSql = "insert into users(`phone`, `pw`, `name`, `type`) value('" + phone + "', '" + pw +"', '" + name + "', '')";

		try {
			pstmt  = con.prepareStatement(joinSql);
			pstmt.executeUpdate();
			
			Alert joinCompleteALert = new Alert(AlertType.INFORMATION);
			joinCompleteALert.setTitle("ȸ������ �Ϸ�");
			joinCompleteALert.setHeaderText(name + "�� ȯ���մϴ� :)");
			joinCompleteALert.setContentText("ȸ�������� ���������� ó���Ǿ����ϴ�. �α������ּ���.");
			joinCompleteALert.show();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("���� ����!");
		} 
	}
}
