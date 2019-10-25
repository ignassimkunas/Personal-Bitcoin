package block;

import java.util.*;

public class Main {
	
	static ArrayList<Transaction> transactions;
	static ArrayList<Block> blockChain;
	public static Block mineBlock(Block newBlock, ArrayList<Transaction> transactionsForBlock, int tries) {
		long difficulty = blockChain.get(blockChain.size()-1).getDifficultyTarget();
		String target = new String(new char[(int) difficulty]).replace('\0', '0');
		String newestHash = newBlock.getHash();
		Random  random = new Random();
		for (int i = 0; i < tries; i++) {
			newBlock.setNonce(newBlock.getNonce()+1);
			newBlock.setHash(newBlock.calculateShaHash());
			newestHash = newBlock.getHash();
			if (newestHash.substring(0, (int)difficulty).equals(target)) {
				System.out.println("Block mined!!\n  Number of tries: " + i + " Hash of new block:" + newestHash);
				return newBlock;
			}
		}
		System.out.println("Mine unsuccesful");
		return null;
	}
	
	public static void listBlockChain(ArrayList<Block> blockChain) {
		int blockCount = 0;
		for (Block block : blockChain) {
			blockCount++;
			System.out.println("Block number: " + Integer.toString(blockCount));
			System.out.println("Previous block hash: " + block.getPrevHash());
			System.out.println("Hash of block: "+ block.getHash());
			System.out.println("Transaction number " + Integer.toString(block.getTransactions().size()) + "\n");
		}
	}
	//kol kas būtinai user1 perveda user2
	public static Transaction makeTransaction(User user1, User user2) {
		Random rand = new Random();
		int ammount = rand.nextInt(1000) + 1;
		user1.setBalance(user1.getBalance() - ammount);
		user2.setBalance(user2.getBalance() + ammount);
		Transaction transaction = new Transaction(user1, user2, ammount, StringUtil.applySha256(user1.getName()+user2.getName() + ammount));
		return transaction;
	}
	public static void generateTransactions(ArrayList<User> users) {
		Random rand = new Random();
		int index1, index2;
		User user1, user2;
		for (int i = 0; i < 10000; i++) {
			index1 = rand.nextInt(users.size());
			index2 = rand.nextInt(users.size());
			user1 = users.get(index1);
			user2 = users.get(index2);
			while (index1 == index2) {
				index2 = rand.nextInt(users.size());
			}
			transactions.add(makeTransaction(user1, user2));
		}
	}
	public static boolean validateTransaction(Transaction transaction) {
		if (!transaction.getId().equals(StringUtil.applySha256(transaction.getFromUser().getName()+transaction.getToUser().getName() + transaction.getTransferAmount())) || transaction.getFromUser().getBalance() < 0){
			return false;
		}
		else {
			return true;
		}
		
	}
	public static ArrayList<Transaction> get100Transactions(){
		Random rand = new Random();
		ArrayList<Transaction> newTransactions = new ArrayList<Transaction>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		int index;
		if (transactions.isEmpty()) {
			index = 0;
		}
		else {
			index = rand.nextInt(transactions.size());
		}
		indices.add(index);
		if (transactions.size() < 100) {
			for (int i = 0; i < transactions.size(); i++) {
				newTransactions.add(transactions.get(index));
				index = rand.nextInt(transactions.size());
				indices.add(index);
			}
		}
		else {
			for (int i = 0; i < 100; i++) {
				newTransactions.add(transactions.get(index));
				index = rand.nextInt(transactions.size());
				indices.add(index);
			}
		}
		
		return newTransactions;
	}
	public static void removeTransFromPool(Block block) {
		ArrayList<Transaction> tempTrans = new ArrayList<Transaction>();
		for (int i = 0; i < transactions.size(); i++) {
			for (int j = 0; j < block.getTransactions().size(); j++) {
				if (transactions.get(i) == block.getTransactions().get(j)){
					tempTrans.add(transactions.get(i));
				}
			}
		}
		for (Transaction t : tempTrans) {
			transactions.remove(t);
		}
	}
	public static void main(String[] args) {
		transactions = new ArrayList<Transaction>();
		blockChain = new ArrayList<Block>();
		Block genesisBlock = new Block(transactions, "");
		blockChain.add(genesisBlock);
		//Generuoja userius
		ArrayList<User> users = new ArrayList<User>();
		String userName = "";
		for (int i = 0; i < 1000; i++) {
			userName = "John" + Integer.toString(i + 1);
			User user = new User(userName);
			users.add(user);
		}
		//Generuoja transakcijas
		generateTransactions(users);
		//Tikrinama ar yra netinkamų transakcijų
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < transactions.size(); i++) {
			if (!validateTransaction(transactions.get(i))) {
				indices.add(i);
			}
		}
		for (int i : indices) {
			transactions.remove(i);
		}
		//Nauji blokai kandidatai
		ArrayList<Block> newBlocks = new ArrayList<Block>();
		Block newBlock = null;
		int tries = 100000, index;
		Random rand = new Random();
		for (int i = 0; i < 5; i++) {
			newBlocks.add(new Block(get100Transactions(), blockChain.get(blockChain.size() - 1).getHash()));
		}
		index = rand.nextInt(newBlocks.size());
		while(transactions.size() > 0) {
			while (!newBlocks.isEmpty()) {
				newBlock = newBlocks.get(index);
				newBlock = mineBlock(newBlock, newBlock.getTransactions(), tries);
				if (newBlock == null) {
					newBlocks.remove(index);
					if (newBlocks.size() > 0) {
						index = rand.nextInt(newBlocks.size());
					}
					System.out.println("Index changed\n");
				}
				else {
					blockChain.add(newBlock);
					removeTransFromPool(newBlock);
					newBlocks.clear();
				}
			}
			if (newBlock == null) {
				tries *= 2;
				System.out.println("Tries doubled. Number of tries: " + tries);
			}
			for (int i = 0; i < 5; i++) {
				newBlocks.add(new Block(get100Transactions(), blockChain.get(blockChain.size() - 1).getHash()));
			}
		}
		listBlockChain(blockChain);
		System.out.println(transactions.size());
	}
}