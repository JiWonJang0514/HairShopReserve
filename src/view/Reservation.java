package view;

import java.time.LocalDate;

public class Reservation {
	private String shop;
	private String designer;
	private String treatment;
	private LocalDate date;
	private long reserve_id;
	private long shop_id;
	private long designer_id;
	
	
	public Reservation() {
		//nothing
		
//		Reservation r = new Reservation();
	}
	
	public Reservation(String shop, String designer, String treatment, LocalDate date, long reserve_id, 
			long shop_id, long designer_id) {
		this.shop = shop;
		this.designer = designer;
		this.treatment = treatment;
		this.date = date;
		this.reserve_id = reserve_id;
		this.shop_id  = shop_id;
		this.designer_id = designer_id;
	}


	//get set
	public String getShop() {
		return shop;
	}

	public void setShop(String shop) {
		this.shop = shop;
	}

	public String getDesigner() {
		return designer;
	}

	public void setDesigner(String designer) {
		this.designer = designer;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public long getReserve_id() {
		return reserve_id;
	}

	public void setReserve_id(long reserve_id) {
		this.reserve_id = reserve_id;
	}
	
	public long getShop_id() {
		return shop_id;
	}

	public void setShop_id(long shop_id) {
		this.shop_id = shop_id;
	}

	public long getDesigner_id() {
		return designer_id;
	}

	public void setDesigner_id(long designer_id) {
		this.designer_id = designer_id;
	}
	

	@Override
	public String toString() {
		return this.reserve_id + "  [  " + this.shop + "  |  " + this.designer + "  |  " + this.treatment + "  |  " + this.date + "  ]";
	}
}	
