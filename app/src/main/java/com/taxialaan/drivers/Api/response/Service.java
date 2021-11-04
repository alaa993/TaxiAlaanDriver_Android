package com.taxialaan.drivers.Api.response;

import com.google.gson.annotations.SerializedName;

public class Service{

	@SerializedName("service_type_id")
	private int serviceTypeId;

	@SerializedName("service_model")
	private String serviceModel;

	@SerializedName("service_type")
	private ServiceType serviceType;

	@SerializedName("provider_id")
	private int providerId;

	@SerializedName("id")
	private int id;

	@SerializedName("service_number")
	private String serviceNumber;

	@SerializedName("status")
	private String status;

	public void setServiceTypeId(int serviceTypeId){
		this.serviceTypeId = serviceTypeId;
	}

	public int getServiceTypeId(){
		return serviceTypeId;
	}

	public void setServiceModel(String serviceModel){
		this.serviceModel = serviceModel;
	}

	public String getServiceModel(){
		return serviceModel;
	}

	public void setServiceType(ServiceType serviceType){
		this.serviceType = serviceType;
	}

	public ServiceType getServiceType(){
		return serviceType;
	}

	public void setProviderId(int providerId){
		this.providerId = providerId;
	}

	public int getProviderId(){
		return providerId;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setServiceNumber(String serviceNumber){
		this.serviceNumber = serviceNumber;
	}

	public String getServiceNumber(){
		return serviceNumber;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}