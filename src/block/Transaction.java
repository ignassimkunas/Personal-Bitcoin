package block;

public class Transaction {
	private User fromUser;
	private User toUser;
	private String id;
	private int transferAmount;
	
	Transaction(User fromUser, User toUser, int transferAmount, String id){
		this.setFromUser(fromUser);
		this.setToUser(toUser);
		this.setTransferAmount(transferAmount);
		this.id = id;
	}
	public User getFromUser() {
		return fromUser;
	}
	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}
	public User getToUser() {
		return toUser;
	}
	public void setToUser(User toUser) {
		this.toUser = toUser;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getTransferAmount() {
		return transferAmount;
	}
	public void setTransferAmount(int transferAmount) {
		this.transferAmount = transferAmount;
	}
}
