import java.util.Collections;
import java.util.ArrayList;

class BTNodeInternal extends BTNode
{
   ArrayList<BTNode> children;
   ArrayList<String> indexWords;
   
   public BTNodeInternal() 
   {
      children = new ArrayList<BTNode>();
      indexWords = new ArrayList<String>();
   }
   public BTNodeInternal(int nodeID)
   {
      children = new ArrayList<BTNode>();
      indexWords = new ArrayList<String>();
      this.nodeID = nodeID;
   }
   
   public void insert(String key, BPlusTree tree)
   {
      if (indexWords.size() == 0) {
         indexWords.add(key); // add first word. only happens on first insert. can remove later for efficiency.
      }
      else if (indexWords.contains(key)){
         // indexWords.indexOf(key)
         // increment below? probably not since this will likely only be called from leaf nodes which increment it themselves
      }
      else if(key.compareTo(indexWords.get(indexWords.size()-1)) > 1) {
         indexWords.add(key); // add to end
      }
      else if (indexWords.size() == 1 && key.compareTo(indexWords.get(0)) < 0) {
         indexWords.add(0, key); // add to 0th
      }
      else {
         for (int i = indexWords.size()-1; i >= 1; i--) {

             if (key.compareTo(indexWords.get(i)) < 1 && key.compareTo(indexWords.get(i-1)) > 1) {
               indexWords.add(key); // found correct position, insert in middle
               break;
             }
         }
      }
      this.printLeavesInSequence();
   }
   
   public void printLeavesInSequence()
   {
      System.out.print("internal "+this.nodeID+": ");
      for (String s : indexWords) {
      System.out.print(s + "  ");
      }
      System.out.println();

   }
   
   public void printStructureWKeys()
   {
      
   }
   
   public Boolean rangeSearch(String startWord, String endWord)
   {
      return true;
   }
   
   public Boolean searchWord(String word)
   {
//      if (children.size() == 0) {
//         return false; // string
//      } wont happen. node won't exist if it doesn't have a data record to point to below.
//

      if (children.get(0).getClass() == BTNodeInternal.class) {
//            // child is internal. compare will not return 0.
         if (children.size() > 1) {
            for (int i = children.size() - 1; i >= 0; i--) {
               BTNodeInternal child = (BTNodeInternal) children.get(i);
               int compare = word.compareTo(child.children.get(0).toString());
               if (compare > 0) { // if words exists, it's to the right of this node
                  return child.searchWord(word);
               }
            }
         }
         BTNodeInternal child = (BTNodeInternal) children.get(0);
         return child.searchWord(word); // word is < all other children, must be in 0th child if it exists
      }

      else { // child is leaf node
         if (children.size() > 1) {
            for (int i = children.size() - 1; i >= 0; i--) {
               BTNodeLeaf child = (BTNodeLeaf)children.get(i);
               int compare = word.compareTo(child.keys.get(0).key);
               if (compare >= 0) { // if words exists, it's IN this node
                  return child.searchWord(word);
               }
            }
         }
         // if this line is reached, it must be in the left-most leaf node
         return children.get(0).searchWord(word);

      }


   }
}