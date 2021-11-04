package com.taxialaan.drivers.Api.response;

import com.google.gson.annotations.SerializedName;

public class Profile{

	@SerializedName("fleet")
	private String fleet;

	@SerializedName("latitude")
	private double latitude;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("rating")
	private String rating;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("otp")
	private int otp;

	@SerializedName("avatar")
	private String avatar;

	@SerializedName("wallet_id")
	private String walletId;

	@SerializedName("balance")
	private String balance;

	@SerializedName("social_unique_id")
	private String socialUniqueId;

	@SerializedName("service")
	private Service service;

	@SerializedName("sos")
	private String sos;

	@SerializedName("currency")
	private String currency;

	@SerializedName("id")
	private int id;

	@SerializedName("lang")
	private String lang;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("login_by")
	private String loginBy;

	@SerializedName("email")
	private String email;

	@SerializedName("status")
	private String status;

	@SerializedName("longitude")
	private double longitude;

	public String getFleet() {
		return fleet;
	}

	public void setFleet(String fleet) {
		this.fleet = fleet;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public int getOtp() {
		return otp;
	}

	public void setOtp(int otp) {
		this.otp = otp;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getSocialUniqueId() {
		return socialUniqueId;
	}

	public void setSocialUniqueId(String socialUniqueId) {
		this.socialUniqueId = socialUniqueId;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public String getSos() {
		return sos;
	}

	public void setSos(String sos) {
		this.sos = sos;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLoginBy() {
		return loginBy;
	}

	public void setLoginBy(String loginBy) {
		this.loginBy = loginBy;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}