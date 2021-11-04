package com.taxialaan.drivers.Api.response;

import com.google.gson.annotations.SerializedName;

public class ServiceType{

	@SerializedName("image")
	private String image;

	@SerializedName("calculator")
	private String calculator;

	@SerializedName("distance")
	private int distance;

	@SerializedName("price")
	private int price;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("fixed")
	private int fixed;

	@SerializedName("id")
	private int id;

	@SerializedName("provider_name")
	private String providerName;

	@SerializedName("capacity")
	private int capacity;

	@SerializedName("minute")
	private int minute;

	@SerializedName("status")
	private int status;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setCalculator(String calculator){
		this.calculator = calculator;
	}

	public String getCalculator(){
		return calculator;
	}

	public void setDistance(int distance){
		this.distance = distance;
	}

	public int getDistance(){
		return distance;
	}

	public void setPrice(int price){
		this.price = price;
	}

	public int getPrice(){
		return price;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setFixed(int fixed){
		this.fixed = fixed;
	}

	public int getFixed(){
		return fixed;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setProviderName(String providerName){
		this.providerName = providerName;
	}

	public String getProviderName(){
		return providerName;
	}

	public void setCapacity(int capacity){
		this.capacity = capacity;
	}

	public int getCapacity(){
		return capacity;
	}

	public void setMinute(int minute){
		this.minute = minute;
	}

	public int getMinute(){
		return minute;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}