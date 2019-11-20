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
	private static long difficultyTarget = 3;
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
	public String getMerkelRootHash(){return merkelRootHash;}
	private ArrayList<Transaction> constructTransactions(ArrayList<Transaction> transactions){

		if (transactions.size() == 1 || transactions.size() == 0){return transactions;}

		ArrayList<Transaction> newList = new ArrayList<>();
		for (int i =0; i < transactions.size() - 1; i += 2){
			newList.add(mergeHash(transactions.get(i), transactions.get(i+1)));
		}
		//jei nelyginis transakcijų skaičius, tai paskutinį su savim
		if (transactions.size() % 2 != 0) {
			newList.add(mergeHash(transactions.get(transactions.size() - 1), transactions.get(transactions.size() - 1)));
		}
		//grąžiną kaip rekursiją, kol neliks transakcijų
		return constructTransactions(newList);
	}
	private Transaction mergeHash(Transaction trans1, Transaction trans2){
		return new Transaction(new User(),new User(),0 , StringUtil.applySha256(trans1.getId() + trans2.getId()));
	}
	private String fromBeginingToEnd(String input) {
		char[] digit = input.toCharArray();
		char first = digit[0];
		for(int i = 0; i < digit.length -1; i++) {
			digit[i] = digit[i+1];
		}
		digit[digit.length - 1] = first;
		input = new String(digit);
		return input;
	}
	
	private String toHash(String input){
		StringBuilder binaryString = new StringBuilder();
		// ascii kodus į binary ir viską sudeda
		for (char c : input.toCharArray()){
			binaryString.append(Integer.toString((int) c, 2));
		}
		binaryString.append("1");
		int counter = 1;
		//jei trumpesnis, prideda tą patį, kas antrą kartą apversdamas.
		if (binaryString.length() < 78) {
			while(binaryString.length() < 78) {
				if (counter % 2 == 0) {
					binaryString.append(binaryString);
				}
				else {
					StringBuilder temp = new StringBuilder();
					temp.append(binaryString);
					binaryString.append(temp.reverse().toString());
				}
				counter++;
			}
		}
		//jei ilgesnis, sutrumpina
		if (binaryString.length() > 78){
			binaryString = new StringBuilder(binaryString.substring(0, 78));
		}
		StringBuilder builder = new StringBuilder();
		builder.append(binaryString);
		//apverčia ir apkeičia pradžioje esančius bitus su gale esančiais
		binaryString = new StringBuilder(builder.reverse().toString());
		for (int i = 0; i < 10; i++) {
			binaryString = new StringBuilder(fromBeginingToEnd(binaryString.toString()));
		}
		BigInteger result = new BigInteger(binaryString.toString(), 2);
		String hashString = result.toString(16);
		if (hashString.length() < 20) {
			hashString += input.charAt(0);
		}
		return hashString;
	}
	String calculateShaHash() {
		return StringUtil.applySha256(prevHash + timeStamp + nonce + version);
	}

	public String calculateHash() {
		String data = "";
		data += prevHash + timeStamp + nonce + version + difficultyTarget;
		return toHash(data);
	}
	String getPrevHash() {
		return prevHash;
	}
	Block(ArrayList<Transaction> transactions, String prevHash){
		this.transactions = transactions;
		this.prevHash = prevHash;
		if (transactions.isEmpty()){
			merkelRootHash = "";
		}
		else{
			merkelRootHash = constructTransactions(transactions).get(0).getId();
		}
		version = "1";
		timeStamp = new Date().getTime();
		Random random = new Random();
		nonce = Integer.toString(random.nextInt(10000));
		hash = calculateShaHash();
	}
	String getHash() {
		return hash;
	}
	void setHash(String hash) {
		this.hash = hash;
	}
	long getDifficultyTarget() {
		return difficultyTarget;
	}
}
