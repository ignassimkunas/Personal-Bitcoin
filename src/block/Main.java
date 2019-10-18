package block;

import java.util.*;

public class Main {
	
	static ArrayList<String> transactions;
	public static Block mineBlock(ArrayList<Block> blockchain, ArrayList<String> transactionsForBlock) {
		long difficulty = blockchain.get(blockchain.size()-1).getDifficultyTarget();
		String target = new String(new char[(int) difficulty]).replace('\0', '0');
		Block newBlock = new Block(transactionsForBlock, blockchain.get(blockchain.size()-1).getHash());
		String newestHash = newBlock.getHash();
		Random  random = new Random();
		while (!newestHash.substring(0, (int)difficulty).equals(target)) {
			newBlock.setNonce(newBlock.getNonce()+1);
			newBlock.setHash(newBlock.calculateShaHash());
			newestHash = newBlock.getHash();
		}
		System.out.println("Block mined!!\n Hash of new block:" + newestHash);
		return newBlock;
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
	public static String makeTransaction(User user1, User user2) {
		String transaction = "";
		Random rand = new Random();
		int ammount = rand.nextInt(1000) + 1;
		transaction = user1.getName() + " to: " + user2.getName() + " ammount: " + Integer.toString(ammount);
		user1.setBalance(user1.getBalance() - ammount);
		user2.setBalance(user2.getBalance() + ammount);
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
			if (user1.getBalance() >= user2.getBalance()) {
				transactions.add(makeTransaction(user1, user2));
			}
			else {
				transactions.add(makeTransaction(user2, user1));
			}
		}
	}
	/*TODO:
	 * Padaryt, kad random transakcijas imtų, o ne iš eilės
	 */
	public static void main(String[] args) {
		transactions = new ArrayList<String>();
		ArrayList<Block> blockChain = new ArrayList<Block>();
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
		ArrayList<String> transactionsToChain;
		//Minina blokus, kol nebelieka transakcijų.
		int count = 0;
		while(count < transactions.size()) {
			transactionsToChain = new ArrayList<String>();
			for (int i = count; i < count + 100; i++) {
				transactionsToChain.add(transactions.get(i));
			}
			blockChain.add(mineBlock(blockChain, transactionsToChain));
			count += 100;
		}
		listBlockChain(blockChain);
	}
}