import java.util.Collections;
import java.util.ArrayList;

class BTNodeLeaf extends BTNode
{
//   ArrayList<Integer> keyCounts;
   BTNodeLeaf nextLeaf;
//   ArrayList<String> keys;
   ArrayList<Word> keys;

   public BTNodeLeaf()
   {
      
   }
   
   public void insert(String word, BPlusTree tree)
   {

      // find correct position, insert key

      if (keys.size() == 0) {
         keys.add(new Word(word)); // add first word. only happens on first insert. can remove later for efficiency.
      }
      else if(word.compareTo(keys.get(keys.size()-1).getKey()) > 1) {
         keys.add(new Word(word)); // add to end
      }
      else if (keys.size() == 1 && word.compareTo(keys.get(0).getKey()) < 0) {
         keys.add(0, new Word(word)); // add to 0th
      }
      else if (keys.get(0).getKey().equals(word)){
         keys.get(0).incKeyCount(); // would get missed for loop below
      }
      else {
         for (int i = keys.size()-1; i >= 1; i--) {
            if (word.compareTo(keys.get(i).getKey()) == 0) {
               keys.get(i).incKeyCount(); // key already exists. increment frequency
               return; // size hasnt changed. no need for other checks
            }

            else if (word.compareTo(keys.get(i).getKey()) < 1 && word.compareTo(keys.get(i-1).getKey()) > 1) {
               keys.add(i, new Word(word)); // found correct position, insert in middle
               break;
            }
         }
      }

      // split operations
      if (keys.size() > tree.n) {
            int splitPoint = (int)Math.ceil(keys.size()/2);
            // ^ always 4/2 = 2 for our purposes. would be relevant if n is odd

         if (this.parent == null) { // only occurs on first split


            BTNodeInternal newRoot = new BTNodeInternal();
            BTNodeLeaf rightLeaf = new BTNodeLeaf();

            newRoot.insert(this.keys.get(splitPoint).key, tree);

            this.nextLeaf = rightLeaf;

            newRoot.children.add(this);
            this.parent = newRoot;

            newRoot.children.add(rightLeaf);
            rightLeaf.parent = newRoot;

            while (keys.size() > splitPoint) { // while size > 2, remove 3rd element. more efficient this way.
               Word temp = keys.remove(splitPoint);
               rightLeaf.keys.add(0,temp);
            }

         }
         else {


            if (word.compareTo(this.keys.get(splitPoint).key) >= 0) { // word got inserted on right side
               // add right
            }
            else {


            }

            //


         }
      }

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
      for (int i = 0; i < keys.size(); i++) {
         if (keys.get(i).key.equals(word)){
            return true;
         }
         else return false;
      }

      return false;
   }
}