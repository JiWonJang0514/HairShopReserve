package view;

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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.JDBCUtil;

public class AdminController {
	@FXML
	private AnchorPane subPane;
	@FXML
	private Label shopName;

	@FXML
	private Button logoutBtn;
	
	@FXML
	private ChoiceBox<String> designerChoiceBox;
	private ObservableList<String> designerChoiceList;
	
	@FXML
	private Button designerBtn;
	@FXML
	private Button allBtn;
	
	@FXML
	private ListView<Reservation> reserveList;
	private ObservableList<Reservation> items;
	
	String logPhone;
	String logName;
	
	long shopId;
	
	ArrayList<Designer> designerList;
	
	public void initialize() {
		System.out.println("initialize");
		
		getLogInfo();
		
		loadShopInfo();
		designerList = new ArrayList<Designer>();
		loadDesigner();
		
		items = FXCollections.observableArrayList();
		String sql = "SELECT s.name shop, d.name designer, d.position, rs.treatment, rs.date, rs.id, rs.shop_id, rs.designer_id FROM reservations rs INNER JOIN shop s ON s.id=rs.shop_id INNER JOIN designer d ON d.id=designer_id WHERE rs.shop_id='" + shopId + "' order by date asc";
		getItems(sql);
		reserveList.setItems(items);
		
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
	
	
	public void loadShopInfo() {
		//DB
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		
		String shopSql = "SELECT * FROM `shop` WHERE admin_phone='" + logPhone + "'";
		
		ResultSet rs = null;
		
		try {
			
			//log get
			pstmt  = con.prepareStatement(shopSql);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				shopId = rs.getLong("id");
				
				shopName.setText(rs.getString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삽입 실패!");
		}
	}
	
	
	public void loadDesigner() {
		//DB 디자이너 초이스박스 처리
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		
		String designerSql = "SELECT * FROM `designer` WHERE shop_id=" + shopId;
		
		ResultSet rs = null;
		
		ArrayList<String> data = new ArrayList<String>();
		
		try {
			pstmt  = con.prepareStatement(designerSql);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {	
				Designer d = new Designer(rs.getLong("id"), rs.getString("name"), rs.getString("position"), rs.getLong("shop_id"));
				designerList.add(d);
				
				String s = d.getName() + " " + d.getPosition();
				data.add(s);	
			}
			
			designerChoiceList = FXCollections.observableArrayList(data);
			designerChoiceBox.setItems(designerChoiceList);
			designerChoiceBox.setValue(data.get(0));
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삽입 실패!");
		}
	}
	
	
	
	
	//예약 리스트 불러오기
	public void getItems(String sql) {
		items.clear();
		
		//예약 리스트 불러오기
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		String itemsSql = sql;
		
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
//					System.out.println(shop + ", " + designer + ", " + treatment + ", " + date + ", " + id);
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
	
	
	public void designerBtnHandler() {
		int idx = designerChoiceBox.getSelectionModel().getSelectedIndex();
		long designerId = designerList.get(idx).getId();
		
		String sql = "SELECT s.name shop, d.name designer, d.position, rs.treatment, rs.date, rs.id, rs.shop_id, rs.designer_id FROM reservations rs INNER JOIN shop s ON s.id=rs.shop_id INNER JOIN designer d ON d.id=designer_id WHERE rs.designer_id='" + designerId + "' order by date asc";
		getItems(sql);
	}
	
	public void allBtnHandler() {
		String sql = "SELECT s.name shop, d.name designer, d.position, rs.treatment, rs.date, rs.id, rs.shop_id, rs.designer_id FROM reservations rs INNER JOIN shop s ON s.id=rs.shop_id INNER JOIN designer d ON d.id=designer_id WHERE rs.shop_id='" + shopId + "' order by date asc";
		getItems(sql);
	}
}