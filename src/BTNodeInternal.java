import java.util.Collections;
import java.util.ArrayList;

class BTNodeInternal extends BTNode
{
   ArrayList<BTNode> children;
   
   public BTNodeInternal() 
   {

   }
   
   public void insert(String key, BPlusTree tree)
   {
      
   }
   
   public void printLeavesInSequence()
   {
      
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

//      for (int i = 0; i < children.size(); i++) {
//
//         if (children.get(i).getClass() == BTNodeInternal.class) {
//            // child is internal. compare will not return 0.
//            BTNodeInternal child = (BTNodeInternal)children.get(i);
//            if   child.children.size() == 0  && wont happen
//
//         }
//
//
//
//
//                ((BTNodeInternal) child).children.get(0).toString()) < 0) {
//            return searchWord(((BTNodeInternal) child).children.get(i).toString());
//         }
//         else if ()
//      }


   }
}