package view;

public class Designer {
	private long id;
	private String name;
	private String position;
	private long shop_id;
	
	public Designer() {
		
	}
	
	public Designer(long id, String name, String position, long shop_id) {
		this.id = id;
		this.name = name;
		this.position = position;
		this.shop_id = shop_id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public long getShop_id() {
		return shop_id;
	}

	public void setShop_id(long shop_id) {
		this.shop_id = shop_id;
	}
	
}
