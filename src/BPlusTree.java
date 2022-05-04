import java.util.*;

class BPlusTree
{
   BTNode root;
   private int nextNodeID; //An ID is given to a node; this is only for display purpose; we can easily see all the keys within the same node.
   private final Set ignoreTable;
   public final int n = 3;
   
   public BPlusTree(Set ignoreSet)
   {
      root = new BTNodeLeaf();
      nextNodeID = 1;
      ignoreTable = ignoreSet;
   }
   
   public int assignNodeID()
   {
      //Return current nextNodeID, then increment value
      return nextNodeID++;
   }
   
   public Boolean insertWord(String word)
   {
      word = word.toLowerCase();
      
      if(ignoreTable != null && ignoreTable.contains(word))
      {
         // increase frequency, ret
         return false;
      }
      
      root.insert(word, this);
      
      //Update root if tree grows
      if(root.getParent() != null)
      {
         root = root.getParent();
      }
      
      return true;
   }
   
   public void printLeavesInSequence()
   {
      if(root != null)
      {
         root.printLeavesInSequence();
      }
   }
   
   public void printStructureWKeys()
   {
      if(root != null)
      {
         root.printStructureWKeys();
      }
   }
   
   public void rangeSearch(Scanner kb)
   {
      String startWord;
      String endWord;
      String swap;
      
      System.out.print("Enter 1st word to search: ");
      
      startWord = kb.nextLine();
      startWord = startWord.toLowerCase();
      
      System.out.print("Enter 2nd word to search: ");
      
      endWord = kb.nextLine();
      endWord = endWord.toLowerCase();
      
      //Swap, if needed, to assign "start" and "end" words in correct order
      if(startWord.compareTo(endWord) > 0)
      {
         swap = startWord;
         startWord = endWord;
         endWord = swap;
      }
      
      if(!root.rangeSearch(startWord, endWord))
      {
         System.out.println("No words found in that range.");
      }
   }
   
   public void searchWord(Scanner kb)
   {
      String word;
      
      System.out.print("Enter word to search: ");
      
      word = kb.nextLine();
      
      if(!root.searchWord(word.toLowerCase()))
      {
         System.out.println("Word \"" + word + "\" not found.");
      }
   }
   
   public void userInsertWord(Scanner kb)
   {
      String word;
      
      System.out.print("Enter word to insert: ");
      
      word = kb.nextLine();
      
      if(insertWord(word.toLowerCase()))
      {
         System.out.println("Word \"" + word + "\" inserted.");
      }
      else
      {
         System.out.println("Word \"" + word + "\" found in ignore list, not inserted.");
      }
   }
}