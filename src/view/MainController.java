//할일
// 1. 로그아웃(로그DB 지우기)
// 2. 가게에 없는 디자이너 선택하면??? 예외처리..


package view;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.JDBCUtil;

public class MainController {
	@FXML
	private AnchorPane shopPane;
	
	@FXML
	private Button logoutBtn;
	
	@FXML
	private Label shopNameLabel;
	@FXML
	private Label openingTime;
	@FXML
	private Label offDay;
	
	@FXML
	private Label designer1;
	@FXML
	private Label designer2;
	@FXML
	private Label designer3;
	@FXML
	private Label designer4;
	@FXML
	private Label designer5;
	@FXML
	private Label designer6;
	
	@FXML
	private Button reserveBtn;
	@FXML
	private Button mypageBtn;
	@FXML
	private Button prevBtn;
	@FXML
	private Button nextBtn;
	@FXML
	private Button page1Btn;
	@FXML
	private Button page2Btn;
	@FXML
	private Button page3Btn;
	@FXML
	private Button page4Btn;
	
	@FXML
	private ChoiceBox<String> designerChoiceBox;
	private ObservableList<String> designerChoiceList;
	@FXML
	private ChoiceBox<String> treatmentChoiceBox;
	private ObservableList<String> treatmentChoiceList;
	@FXML
	private DatePicker reserveDatePicker;

	
	
	//변수 선언
	String logPhone;
	String logName;
	
	int page;
	ArrayList<Designer> designerList;
	
	public void initialize() {
		System.out.println("initialize");
		
		getLogInfo();
		
		treatmentChoiceList = FXCollections.observableArrayList("펌", "매직", "염색", "컷트", "클리닉");
		treatmentChoiceBox.setValue("펌");
		treatmentChoiceBox.setItems(treatmentChoiceList);
		
		page = 1;
		designerList = new ArrayList<Designer>();
		page1BtnHandler();
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
	
	public void reserveBtnHandler() {
		System.out.println("예약하기!");
		
		String treatment = treatmentChoiceBox.getValue();
		LocalDate date = reserveDatePicker.getValue();
		
		if (date == null) {
			Alert pleaseDateALert = new Alert(AlertType.INFORMATION);
			pleaseDateALert.setTitle("입력 필요");
			pleaseDateALert.setHeaderText("날짜를 선택해주세요.");
			pleaseDateALert.show();	
		} else {
			int idx = designerChoiceBox.getSelectionModel().getSelectedIndex();
			long designerId = designerList.get(idx).getId();
			
			//인서트 호출
			insertReserve(page, designerId, treatment, date, logPhone);
		}
		
		
	}
	
	
	public void insertReserve(int shopId, long designerId, String treatment, LocalDate date, String phone) {
		//DB
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		String reserveSql = "insert into reservations(`shop_id`, `designer_id`, `treatment`, `date`, `phone`) value('" + shopId + "', '" + designerId +"', '" + treatment +"', '" + date +"', '" + phone +"')";

		try {
			pstmt  = con.prepareStatement(reserveSql);
			pstmt.executeUpdate();
			
			Alert reserveCompleteALert = new Alert(AlertType.INFORMATION);
			reserveCompleteALert.setTitle("예약 완료");
			reserveCompleteALert.setHeaderText("예약이 완료되었습니다.");
			reserveCompleteALert.show();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삽입 실패!");
		} 
	}
	
	public void mypageBtnHandler() {
		//메인->마이페이지 전환
		changeScene("/view/MypageLayout.fxml", mypageBtn);
	}
	
	public void prevBtnHandler() {
		if (page == 4) {
			page3BtnHandler();
		} else if (page == 3) {
			page2BtnHandler();
		} else if (page == 2) {
			page1BtnHandler();
		} else if (page == 1) {
			page4BtnHandler();
		}
	}
	
	public void nextBtnHandler() {
		if (page == 1) {
			page2BtnHandler();
		} else if (page == 2) {
			page3BtnHandler();
		} else if (page == 3) {
			page4BtnHandler();
		} else if (page == 4) {
			page1BtnHandler();
		}
	}
	
	public void page1BtnHandler() {
		page = 1;
		
		getShopData();
		getDesignerData();
		
		page1Btn.setStyle("-fx-text-fill: rgb(3, 158, 211);");
		page2Btn.setStyle("-fx-text-fill: black;");
		page3Btn.setStyle("-fx-text-fill: black;");
		page4Btn.setStyle("-fx-text-fill: black;");
		shopPane.setStyle("-fx-background-image: url(/resources/sunsoo.jpg);");
		
	}
	
	public void page2BtnHandler() {
		page = 2;
		
		getShopData();
		getDesignerData();
		
		page2Btn.setStyle("-fx-text-fill: rgb(3, 158, 211);");
		page1Btn.setStyle("-fx-text-fill: black;");
		page3Btn.setStyle("-fx-text-fill: black;");
		page4Btn.setStyle("-fx-text-fill: black;");
		shopPane.setStyle("-fx-background-image: url(/resources/chahong.jpg);");
	}
	
	public void page3BtnHandler() {
		page = 3;
		
		getShopData();
		getDesignerData();
		
		page3Btn.setStyle("-fx-text-fill: rgb(3, 158, 211);");
		page1Btn.setStyle("-fx-text-fill: black;");
		page2Btn.setStyle("-fx-text-fill: black;");
		page4Btn.setStyle("-fx-text-fill: black;");
		shopPane.setStyle("-fx-background-image: url(/resources/jayning.png);");
	}
	
	public void page4BtnHandler() {
		page = 4;
		
		getShopData();
		getDesignerData();
		
		page4Btn.setStyle("-fx-text-fill: rgb(3, 158, 211);");
		page1Btn.setStyle("-fx-text-fill: black;");
		page2Btn.setStyle("-fx-text-fill: black;");
		page3Btn.setStyle("-fx-text-fill: black;");
		shopPane.setStyle("-fx-background-image: url(/resources/comma.jpg);");
	}
	
	//페이지에 맞는 미용실 정보 가져오기
	public void getShopData() {
		//DB
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		String shopSql = "select * from shop where id=" + page;
		
		ResultSet rs = null;
		try {
			pstmt  = con.prepareStatement(shopSql);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				String open_time = rs.getString("open_time");
				String close_time = rs.getString("close_time");
				String day_off = rs.getString("day_off");
//				System.out.print(id + ", " + name + ", " + open_time + ", " + close_time + ", " + day_off);
				
				shopNameLabel.setText(name);
				openingTime.setText(open_time + " - " + close_time);
				offDay.setText(day_off);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삽입 실패!");
		} 
	}
	
	
	//페이지에 맞는 디자이너 데이터 가져오기
	public void getDesignerData() {
		//designerList 초기화
		designerList.clear();
		
		//라벨 초기화
		for (int i = 1; i <= 6; i++) {
			if (i == 1) {
				designer1.setText("");
			} else if (i == 2) {
				designer2.setText("");
			} else if (i == 3) {
				designer3.setText("");
			} else if (i == 4) {
				designer4.setText("");
			} else if (i == 5) {
				designer5.setText("");
			} else if (i == 6) {
				designer6.setText("");
			}
		}
		
		//예약 리스트 불러오기
		JDBCUtil db = new JDBCUtil();
		Connection con = db.getConnection();
		
		PreparedStatement pstmt = null;
		
		String designerSql = "SELECT * FROM `designer` WHERE shop_id=" + page;
		
		ResultSet rs = null;
		
		ArrayList<String> data = new ArrayList<String>();
		
		try {
			pstmt  = con.prepareStatement(designerSql);
			rs = pstmt.executeQuery();
			
			
			int i = 1;
			while (rs.next()) {	
				Designer d = new Designer(rs.getLong("id"), rs.getString("name"), rs.getString("position"), rs.getLong("shop_id"));
				designerList.add(d);
				
				String s = d.getName() + " " + d.getPosition();
				
				if (i == 1) {
					designer1.setText(s);
				} else if (i == 2) {
					designer2.setText(s);
				} else if (i == 3) {
					designer3.setText(s);
				} else if (i == 4) {
					designer4.setText(s);
				} else if (i == 5) {
					designer5.setText(s);
				} else if (i == 6) {
					designer6.setText(s);
				}
				
				data.add(s);
				
				i++;
				
			}
//			System.out.println(data);
			
			designerChoiceList = FXCollections.observableArrayList(data);
			designerChoiceBox.setItems(designerChoiceList);
			designerChoiceBox.setValue(data.get(0));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삽입 실패!");
		}
	}
}
