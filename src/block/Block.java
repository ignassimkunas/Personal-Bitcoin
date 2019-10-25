package block;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Block {
	//header
	private String hash;
	private String prevHash;
	private long timeStamp;
	private String version;
	private String merkelRootHash;
	private String nonce;
	private static long difficultyTarget = 4;
	//data
	private ArrayList<Transaction> transactions;
	
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	public String getNonce() {
		return nonce;
	}
	public ArrayList<Transaction> getTransactions(){
		return transactions;
	}
	String fromBeginingToEnd(String input) {
		char[] digit = input.toCharArray();
		char first = digit[0];
		for(int i = 0; i < digit.length -1; i++) {
			digit[i] = digit[i+1];
		}
		digit[digit.length - 1] = first;
		input = new String(digit);
		return input;
	}
	
	String toHash(String input){
		String binaryString = "";
		// ascii kodus į binary ir viską sudeda
		for (char c : input.toCharArray()){
			binaryString += Integer.toString((int)c, 2);
		}
		binaryString += "1";
		int counter = 1;
		//jei trumpesnis, prideda tą patį, kas antrą kartą apversdamas.
		if (binaryString.length() < 78) {
			while(binaryString.length() < 78) {
				if (counter % 2 == 0) {
					binaryString += binaryString;
				}
				else {
					StringBuilder temp = new StringBuilder();
					temp.append(binaryString);
					binaryString += temp.reverse().toString();
				}
				counter++;
			}
		}
		//jei ilgesnis, sutrumpina
		if (binaryString.length() > 78){
			binaryString = binaryString.substring(0, 78);
		}
		StringBuilder builder = new StringBuilder();
		builder.append(binaryString);
		//apverčia ir apkeičia pradžioje esančius bitus su gale esančiais
		binaryString = builder.reverse().toString();
		for (int i = 0; i < 10; i++) {
			binaryString = fromBeginingToEnd(binaryString);
		}
		BigInteger result = new BigInteger(binaryString, 2);
		String hashString = result.toString(16);
		if (hashString.length() < 20) {
			hashString += input.charAt(0);
		}
		return hashString;
	}
	public String calculateShaHash() {
		String calculatedhash = StringUtil.applySha256(prevHash + Long.toString(timeStamp) + nonce + version);
		return calculatedhash;
	}
	public String calculateHash() {
		String data = "";
		data += prevHash + Long.toString(timeStamp) + nonce + version + Long.toString(difficultyTarget);
		return toHash(data);
	}
	public String getPrevHash() {
		return prevHash;
	}
	Block(ArrayList<Transaction> transactions, String prevHash){
		this.transactions = transactions;
		this.prevHash = prevHash;
		version = "1";
		timeStamp = new Date().getTime();
		Random random = new Random();
		nonce = Integer.toString(random.nextInt(10000));
		hash = calculateShaHash();
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public long getDifficultyTarget() {
		return difficultyTarget;
	}
}
