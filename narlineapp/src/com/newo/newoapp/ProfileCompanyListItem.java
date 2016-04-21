package com.newo.newoapp;

public class ProfileCompanyListItem {
	private int companyId;
	private String company;
	private String location;
	private String lastUpdateTime;
	private int logo;
	private String points = "0";
	// boolean to set visiblity of the counter
	private boolean isCounterVisible = false;

	public ProfileCompanyListItem() {
	}

	public ProfileCompanyListItem(String company, String location,
			String lastUpdateTime, int logo) {
		this.company = company;
		this.location = location;
		this.lastUpdateTime = lastUpdateTime;
		this.logo = logo;
	}

	public ProfileCompanyListItem(String company, String location,
			String lastUpdateTime, int logo, boolean isCounterVisible,
			String points) {
		this.isCounterVisible = isCounterVisible;
		this.company = company;
		this.location = location;
		this.lastUpdateTime = lastUpdateTime;
		this.points = points;
		this.logo = logo;
	}

	public int getCompanyId() {
		return this.companyId;
	}

	public String getCompany() {
		return this.company;
	}

	public String getLocation() {
		return this.location;
	}

	public String getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public int getLogo() {
		return this.logo;
	}

	public String getPoints() {
		return this.points;
	}

	public boolean getCounterVisibility() {
		return this.isCounterVisible;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setLocation(String Location) {
		this.location = location;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public void setLogo(int logo) {
		this.logo = logo;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public void setCounterVisibility(boolean isCounterVisible) {
		this.isCounterVisible = isCounterVisible;
	}
}
