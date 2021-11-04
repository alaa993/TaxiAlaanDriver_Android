package com.taxialaan.drivers.Api.response;

import com.google.gson.annotations.SerializedName;

public class TransactionItem {

	@SerializedName("amount")
	private long amount;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("remain_balance")
	private long remainBalance;

	@SerializedName("admin_id")
	private String adminId;

	@SerializedName("transactionable_type")
	private String transactionableType;

	@SerializedName("description")
	private String description;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("commission")
	private long commission;

	@SerializedName("id")
	private int id;

	@SerializedName("type")
	private String type;

	@SerializedName("transactionable_id")
	private int transactionableId;

	@SerializedName("status")
	private String status;

    @SerializedName("cash")
    private long cash;

    @SerializedName("discount")
    private long discount;


    public long getCash() {
        return cash;
    }

    public void setCash(long cash) {
        this.cash = cash;
    }

    public long getDiscount() {
        return discount;
    }

    public void setDiscount(long discount) {
        this.discount = discount;
    }

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public long getRemainBalance() {
		return remainBalance;
	}

	public void setRemainBalance(long remainBalance) {
		this.remainBalance = remainBalance;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getTransactionableType() {
		return transactionableType;
	}

	public void setTransactionableType(String transactionableType) {
		this.transactionableType = transactionableType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public long getCommission() {
		return commission;
	}

	public void setCommission(long commission) {
		this.commission = commission;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTransactionableId() {
		return transactionableId;
	}

	public void setTransactionableId(int transactionableId) {
		this.transactionableId = transactionableId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}