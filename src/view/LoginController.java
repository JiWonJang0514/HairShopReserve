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
		
		//log_info 초기화 
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		String logOutSql = "delete from log_info";

		try {
			pstmt  = con.prepareStatement(logOutSql);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삽입 실패!");
		} 
	}
	
	
	//화면전환 메소드
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
			pleaseInputALert.setTitle("입력 필요");
			pleaseInputALert.setHeaderText("가입에 필요한 모든 필드를 입력해주세요.");
			pleaseInputALert.show();	
		} else {
			insertUser(inputPhone, inputPw, inputName);	
			
			phone.setText("");
			pw.setText("");
			name.setText("");
		}
	}
	
	
	
	//로그인 확인 처리
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
				
				
				//로그인포 셋
				String logSetSql = "insert into log_info(`phone`, `name`) value('" + rs.getString("phone") + "', '" + rs.getString("name") + "')";

				try {
					pstmt  = con.prepareStatement(logSetSql);
					pstmt.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("삽입 실패!");
				} 
				
				if (type.equals("admin")) {
					//로그인->관리자페이지 전환
					changeScene("/view/AdminLayout.fxml", joinBtn);
				} else {
					//로그인->메인 전환
					changeScene("/view/MainLayout.fxml", joinBtn);	
				}

					
			} else {
				Alert pleaseLoginALert = new Alert(AlertType.INFORMATION);
				pleaseLoginALert.setTitle("로그인 필요");
				pleaseLoginALert.setHeaderText("일치하는 아이디, 비밀번호, 이름이 없습니다 :(");
				pleaseLoginALert.setContentText("다시 입력해주세요.");
				pleaseLoginALert.show();	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삽입 실패!");
		}
	}
	
	
	//회원가입 처리
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
			joinCompleteALert.setTitle("회원가입 완료");
			joinCompleteALert.setHeaderText(name + "님 환영합니다 :)");
			joinCompleteALert.setContentText("회원가입이 정상적으로 처리되었습니다. 로그인해주세요.");
			joinCompleteALert.show();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삽입 실패!");
		} 
	}
}
