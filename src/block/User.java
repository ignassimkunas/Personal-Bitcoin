package block;

import java.util.Random;

public class User {
	
	private String name;
	private String publicKey;
	private int privateKey; 
	private int shimCoin;
	
	User(String name){
		this.name = StringUtil.applySha256(name);
		Random random = new Random();
		privateKey = random.nextInt(1000000000);
		publicKey = StringUtil.applySha256(Integer.toString(privateKey));
		shimCoin = random.nextInt(999901) +  100;
	}
	public int getBalance() {
		return shimCoin;
	}
	public void setBalance(int balance) {
		shimCoin = balance;
	}
	public int getPrivateKey() {
		return privateKey;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public String getName() {
		return name;
	}	
}
