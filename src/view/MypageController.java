package view;

import java.net.UnknownServiceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.JDBCUtil;

public class MypageController {
	@FXML
	private Label userName;
	@FXML
	private ChoiceBox<String> designerChoiceBox;
	private ObservableList<String> designerChoiceList;
	@FXML
	private ChoiceBox<String> treatmentChoiceBox;
	private ObservableList<String> treatmentChoiceList;
	@FXML
	private DatePicker reserveDatePicker;
	
	@FXML
	private Button changeBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private Button homeBtn;
	@FXML
	private Button logoutBtn;
	
	@FXML
	private ListView<Reservation> reserveList;
	private ObservableList<Reservation> items;
	
	String logPhone;
	String logName;
	
	ArrayList<Designer> designerList;
	ArrayList<String> treatmentData;
	Reservation r;
	
	
	public void initialize() {
		System.out.println("initialize");
		
		getLogInfo();
		userName.setText(logName);
		
		designerList = new ArrayList<Designer>();
		treatmentData = new ArrayList<String>();
		
		
		items = FXCollections.observableArrayList();
		getItems();
		reserveList.setItems(items);
		
		
		//디자이너, 시술종류 초이스박스
		designerChoiceList = FXCollections.observableArrayList();
		designerChoiceBox.setItems(designerChoiceList);
		
		//treatmentData 초기값
		treatmentData.add("펌");
		treatmentData.add("매직");
		treatmentData.add("염색");
		treatmentData.add("컷트");
		treatmentData.add("클리닉");
		
		treatmentChoiceList = FXCollections.observableArrayList(treatmentData);
		treatmentChoiceBox.setItems(treatmentChoiceList);
		
	}
	
	public void getLogInfo() {
		//DB
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		
		String logGetSql = "SELECT * FROM log_info";
		
		ResultSet rs = null;
		
		try {
			
			//log get
			pstmt  = con.prepareStatement(logGetSql);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				logPhone = rs.getString("phone");	
				logName = rs.getString("name");	
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삽입 실패!");
		}
		
		System.out.println(logPhone + ", " + logName);
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
	
	
	//예약 리스트 불러오기
	public void getItems() {
		items.clear();
		
		//예약 리스트 불러오기
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		String itemsSql = "SELECT s.name shop, d.name designer, d.position, rs.treatment, rs.date, rs.id, rs.shop_id, rs.designer_id FROM reservations rs INNER JOIN shop s ON s.id=rs.shop_id INNER JOIN designer d ON d.id=designer_id WHERE phone='" + logPhone + "'";
		
		ResultSet rs = null;
		try {
			pstmt  = con.prepareStatement(itemsSql);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				String shop = rs.getString("shop");
				String designer = rs.getString("designer") + " " + rs.getString("position");
				String treatment = rs.getString("treatment");
				LocalDate date = LocalDate.parse(rs.getString("date"));
				long id = rs.getLong("id");
				long shop_id = rs.getLong("shop_id");
				long designer_id = rs.getLong("designer_id");
			
				items.add(new Reservation(shop, designer, treatment, date, id, shop_id, designer_id));
//				System.out.println(shop + ", " + designer + ", " + treatment + ", " + date + ", " + id);
			}
		

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삽입 실패!");
		} 
	}
	
	
	public void logoutBtnHandler() {
		//DB
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
		
		//메인->로그인 전환
		changeScene("/view/LoginLayout.fxml", logoutBtn);
	}

	public void changeBtnHandler() {
		System.out.println("예약 수정!");
		
		int idx = designerChoiceBox.getSelectionModel().getSelectedIndex();
		long designerId = designerList.get(idx).getId();
		
		//DB
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		String updateSql = "UPDATE `reservations` SET `designer_id`='" + designerId + "',`treatment`='" + treatmentChoiceBox.getValue() + "',`date`='" + reserveDatePicker.getValue() + "' WHERE id='" + r.getReserve_id() + "'";

		try {
			pstmt  = con.prepareStatement(updateSql);
			pstmt.executeUpdate();
			
			Alert reserveCompleteALert = new Alert(AlertType.INFORMATION);
			reserveCompleteALert.setTitle("예약 완료");
			reserveCompleteALert.setHeaderText("예약 수정이 정상적으로 처리되었습니다.");
			reserveCompleteALert.show();
			
			
			getItems();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삽입 실패!");
		} 
	}
	
	public void cancelBtnHandler() {
		System.out.println("예약 취소!");
		
		//DB
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		String deleteSql = "DELETE FROM `reservations` WHERE id='" + r.getReserve_id() + "'";

		try {
			pstmt  = con.prepareStatement(deleteSql);
			pstmt.executeUpdate();
			
			Alert reserveCompleteALert = new Alert(AlertType.INFORMATION);
			reserveCompleteALert.setTitle("예약 취소");
			reserveCompleteALert.setHeaderText("예약 취소가 정상적으로 처리되었습니다.");
			reserveCompleteALert.show();
			
			
			getItems();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삽입 실패!");
		} 
	}
	
	public void homeBtnHandler() {
		//마이페이지->메인 전환
		changeScene("/view/MainLayout.fxml", homeBtn);
	}
	
	
	//리스트 아이템 클릭 이벤트
	public void reserveListClick() {
		//디자이너 데이터리스트 리셋
		designerList.clear();
		//디자이너 초이스리스트 리셋
		designerChoiceList.clear();
		
		int idx = reserveList.getSelectionModel().getSelectedIndex();
		if (idx >= 0) {
			
			r = items.get(idx);
			
			//DB 디자이너 초이스박스 처리
			JDBCUtil db = new JDBCUtil();
			Connection con = db.getConnection();
			
			PreparedStatement pstmt = null;
			
			String designerSql = "SELECT * FROM `designer` WHERE shop_id=" + r.getShop_id();
			
			ResultSet rs = null;
			
			ArrayList<String> data = new ArrayList<String>();
			
			try {
				pstmt  = con.prepareStatement(designerSql);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {	
					Designer d = new Designer(rs.getLong("id"), rs.getString("name"), rs.getString("position"), rs.getLong("shop_id"));
					designerList.add(d);
					
					String s = d.getName() + " " + d.getPosition();
					
					if (rs.getLong("id") == r.getDesigner_id()) {
						designerChoiceBox.setValue(s);
					}
					
					data.add(s);	
				}
				
				designerChoiceList = FXCollections.observableArrayList(data);
				designerChoiceBox.setItems(designerChoiceList);
				
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("삽입 실패!");
			}
			
			
			//시술종류 초이스박스 처리
			treatmentChoiceList = FXCollections.observableArrayList(treatmentData);
			treatmentChoiceBox.setItems(treatmentChoiceList);
			treatmentChoiceBox.setValue(r.getTreatment());

			//데이트픽커 처리
			reserveDatePicker.setValue(r.getDate());
		}
	}
}


