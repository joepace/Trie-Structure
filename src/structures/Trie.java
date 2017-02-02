package structures;

import java.util.ArrayList;

/**
 * This class implements a compressed trie. Each node of the tree is a CompressedTrieNode, with fields for
 * indexes, first child and sibling.
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	/**
	 * Words indexed by this trie.
	 */
	ArrayList<String> words;
	
	/**
	 * Root node of this trie.
	 */
	TrieNode root;
	
	/**
	 * Initializes a compressed trie with words to be indexed, and root node set to
	 * null fields.
	 * 
	 * @param words
	 */
	public Trie() {
		root = new TrieNode(null, null, null);
		words = new ArrayList<String>();
	}
	
	/**
	 * Inserts a word into this trie. Converts to lower case before adding.
	 * The word is first added to the words array list, then inserted into the trie.
	 * 
	 * @param word Word to be inserted.
	 */
	public void insertWord(String word) 
	{
		/** COMPLETE THIS METHOD **/
		word = word.toLowerCase(); //all words in array list must be lower case
		words.add(word);
		
		int wordIndex = words.indexOf(word);
		Indexes newIndexes;
		//base case - empty trie to start
		if (root.firstChild == null)
		{
			newIndexes = new Indexes(wordIndex, (short)0, (short)(word.length()-1));
			root.firstChild = new TrieNode(newIndexes, null, null);
			return;
		}
		//////////////////////////EVERYTHING ABOVE THIS IS FINE///////////////////////////////////////////		
		TrieNode curNode = root.firstChild;
		TrieNode bestPrefixNode = curNode;
		
		int prefixLength = 0; 
		int bestPrefixLength = 0;
		
		while (curNode != null)
		{
			
			prefixLength = findPrefixLength(word, curNode); 
			
			System.out.println("prefixLength = " + prefixLength);
			
			if (prefixLength > bestPrefixLength)
			{
				bestPrefixLength = prefixLength;
				System.out.println("bestPrefixLength is :" + bestPrefixLength);
		
				bestPrefixNode = curNode;
				System.out.println("bestPrefixNode is :" + bestPrefixNode.substr.toString());
			}
			
			if (prefixLength > 0 && curNode.firstChild != null)
			{
				curNode = curNode.firstChild;
			}
			
			else
			{
				curNode = curNode.sibling;
			}
		}
		
		if (bestPrefixLength > 0)
		{
			if (bestPrefixNode.firstChild == null)
			{
				System.out.println("bestPrefixNode indexes are: " + bestPrefixNode.substr.toString());
				bestPrefixNode.substr.endIndex = (short)(bestPrefixLength - 1);	
				newIndexes = new Indexes(bestPrefixNode.substr.wordIndex, (short)bestPrefixLength, (short)(words.get(bestPrefixNode.substr.wordIndex).length()-1));
				bestPrefixNode.firstChild = new TrieNode(newIndexes, null, null);

				newIndexes = new Indexes(wordIndex, (short)bestPrefixLength, (short)(words.get(wordIndex).length() -1));
				bestPrefixNode.firstChild.sibling = new TrieNode(newIndexes, null, null);
			}
			
			else
			{
				bestPrefixNode = getToBottom(bestPrefixNode);
				
				System.out.println("bestPrefixNode indexes are: " + bestPrefixNode.substr.toString());
				bestPrefixNode.substr.endIndex = (short)(bestPrefixLength - 1);	
				newIndexes = new Indexes(bestPrefixNode.substr.wordIndex, (short)bestPrefixLength, (short)(words.get(bestPrefixNode.substr.wordIndex).length()-1));
				TrieNode temp = bestPrefixNode.firstChild;
				bestPrefixNode.firstChild = new TrieNode (newIndexes, null, temp);
				
				newIndexes = new Indexes(wordIndex, (short)bestPrefixLength, (short)(words.get(wordIndex).length() -1));
				
				TrieNode temp2 = bestPrefixNode.firstChild;
				while (temp2.sibling != null)
				{
					temp2 = temp2.sibling;
				}
				temp2.sibling = new TrieNode(newIndexes, null, null);
			}
		}
			
		if (bestPrefixLength == 0)
		{
			while (bestPrefixNode.sibling != null)
			{
				bestPrefixNode = bestPrefixNode.sibling;
			}
			newIndexes = new Indexes(wordIndex, (short)bestPrefixLength, (short)(words.get(wordIndex).length() -1));
			bestPrefixNode.sibling = new TrieNode(newIndexes, null, null);
		}
	}
	
	private TrieNode getToBottom (TrieNode curNode)
	{
		while (curNode.firstChild != null)
		{
			curNode = curNode.firstChild;
		}
		
		return curNode;
	}
	
	private int findPrefixLength (String word, TrieNode curNode)
	{
		int numMatch = 0;
		String curNodeWord = words.get(curNode.substr.wordIndex);
		String newPrefix = "";
		
		for (int i = 0; i < curNodeWord.length(); i++)
		{
			System.out.println("test3");
			System.out.println("curNodeWord is " + curNodeWord);
			newPrefix = curNodeWord.substring(0, (i + 1)); //adds next char to current prefix to check vs word
			
			System.out.println("newPrefix is: " + newPrefix);
			
			System.out.println("word is " + word);
			
			if (word.substring(0, (i + 1)).equals(newPrefix))
			{
				numMatch++; //length of prefix increases
				System.out.println("numMatch = " + numMatch);
			}
			
			else
			{
				break;
			}
		}
		
		return numMatch;
	}
	
	private int findCompPrefixLength (String word, TrieNode curNode)
	{
		short startIndex = curNode.substr.startIndex;
		short endIndex = curNode.substr.endIndex;
		
		String string = words.get(curNode.substr.wordIndex);
		String subString = string.substring(startIndex, endIndex + 1);
		
		int bestPrefixLength = Math.min(string.length(), word.length());
		int prefixLength = 0;
		
		boolean equalsWordPrefix = true; //checks to see if substring equals word prefix
		
		if(endIndex < word.length())
		{
			if(subString.equals(word.substring(startIndex, endIndex)))
			{
				prefixLength = subString.length();
				equalsWordPrefix = false;
			}
		}
		
		if(equalsWordPrefix)
		{
			for(int i = 0; i < bestPrefixLength; i++)
			{
				if(string.charAt(i) == word.charAt(i))
				{
					prefixLength++;
				}
				
				else
				{
					break;
				}
			}
		}
		
		return prefixLength;
	}
	
	/**
	 * Given a string prefix, returns its "completion list", i.e. all the words in the trie
	 * that start with this prefix. For instance, if the tree had the words bear, bull, stock, and bell,
	 * the completion list for prefix "b" would be bear, bull, and bell; for prefix "be" would be
	 * bear and bell; and for prefix "bell" would be bell. (The last example shows that a prefix can be
	 * an entire word.) The order of returned words DOES NOT MATTER. So, if the list contains bear and
	 * bell, the returned list can be either [bear,bell] or [bell,bear]
	 * 
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all words in tree that start with the prefix, order of words in list does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public ArrayList<String> completionList(String prefix) 
	{
		/** COMPLETE THIS METHOD **/
		
		ArrayList<String> completed = new ArrayList<String>();
		
		TrieNode curNode = root.firstChild;
		TrieNode tempNode = null;
		
		int bestPrefixLength = 0;
		boolean match = false;
		
		System.out.println("Given prefix length: " + prefix.length());
		
		while(match != true)
		{
			int prefixLength = findCompPrefixLength(prefix, curNode);
			//System.out.println("Current substr: " + curNode.substr);
			//System.out.println("Current prefixlength: " + prefixLength);
			if((prefixLength > bestPrefixLength) && (prefixLength < prefix.length()))
			{
				bestPrefixLength = prefixLength;
				curNode = curNode.firstChild;
				continue;
			}
			
			if((prefixLength > bestPrefixLength) && (prefixLength == prefix.length())){
				bestPrefixLength = prefixLength;
				tempNode = curNode;
			}
			
			curNode = curNode.sibling;
			
			if(curNode == null)
			{
				match = true;
			}
		}
		
		if(bestPrefixLength > 0)
		{
			findCompletionList(tempNode, prefix, completed);
		}
		
		return completed;
	}	
	
	private void findCompletionList(TrieNode newRoot, String prefix, ArrayList<String> completed)
	{
		if(newRoot == null)
		{
			return;
		}
		
		int curPrefixLength = findCompPrefixLength(prefix, newRoot); 
		
		if(curPrefixLength == prefix.length())
		{
			if(newRoot.firstChild == null)
			{
				if(!(completed.contains(words.get(newRoot.substr.wordIndex))))
				{
					completed.add(words.get(newRoot.substr.wordIndex));
				}
			}
			
			if(newRoot.sibling != null)
			{
				findCompletionList(newRoot.sibling, prefix, completed);
			}
			
			if(newRoot.firstChild != null)
			{
				findCompletionList(newRoot.firstChild, prefix, completed);
			}
		}
	}
		
	
	public void print() {
		print(root, 1, words);
	}
	
	private static void print(TrieNode root, int indent, ArrayList<String> words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			System.out.println("      " + words.get(root.substr.wordIndex));
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		System.out.println("(" + root.substr + ")");
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
