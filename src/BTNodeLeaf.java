import java.util.Collections;
import java.util.ArrayList;

/*
Author: Schuyler Asplin
Project 2 for CSCD 427 with Professor Dan Li
 */

class BTNodeLeaf extends BTNode
{
//   ArrayList<Integer> keyCounts;
   BTNodeLeaf nextLeaf;
//   ArrayList<String> keys;
   ArrayList<Word> keys;

   public BTNodeLeaf()
   {
      keys = new ArrayList<Word>();
   }
   public BTNodeLeaf(int nodeID)
   {
      keys = new ArrayList<Word>();
      this.nodeID = nodeID;
   }

   public void insert(String word, BPlusTree tree)
   {

      // used later to find position in parent's children array
      String originalLeftMostKey = "";
      if (keys.size() > 0) {
         originalLeftMostKey = keys.get(0).key;
      }

      if (keys.size() == 0) {
         keys.add(new Word(word)); // add first word. only happens on first insert. can remove later for efficiency.
      }
      else if(word.compareTo(keys.get(keys.size()-1).getKey()) > 0) {
         keys.add(new Word(word)); // add to end
      }
      else if (word.compareTo(keys.get(0).getKey()) < 0) {
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

            else if (word.compareTo(keys.get(i).getKey()) < 0 && word.compareTo(keys.get(i-1).getKey()) > 0) {
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

            // make parent and sibling
            BTNodeInternal newRoot = new BTNodeInternal(tree.nodeIDAssigner++);

            BTNodeLeaf rightLeaf = new BTNodeLeaf(tree.nodeIDAssigner++);


            // parent / child pointers
            newRoot.children.add(this);
            newRoot.children.add(rightLeaf);
            this.parent = newRoot;
            rightLeaf.parent = newRoot;

            // split calls copy up
            this.split(splitPoint, rightLeaf, tree);

         }
         else { // parent is not null - called every time after first

            // create right node to split to
            BTNodeLeaf rightLeaf = new BTNodeLeaf(tree.nodeIDAssigner++);
            rightLeaf.parent = this.parent;

            /*
            figure out where to insert in parent's children list.
            left most word will be the one also in parent's children list, unless this is the left most child.
            Thus we initialize position to 0, and only change if this is not the left most child.
             */
            int positionInParentsChildrenList = 0;

            // insert right sibling into parents children. must find correct position to do so.
            // cant use contains because it considers 'window' to contain 'wind'
            for (int i = parent.indexWords.size()-1; i >= 0; i--) {
               if (parent.indexWords.get(i).equals(originalLeftMostKey)) {
                  positionInParentsChildrenList = i + 1; // correspond children index will be 1 greater than indexWords index
                  break;
               }
            }
            if (positionInParentsChildrenList + 1 >= parent.children.size()){
               parent.children.add(rightLeaf); // avoid index out of bounds exception
            }
            else {
            parent.children.add(positionInParentsChildrenList + 1, rightLeaf);
            // + 1 again to add after
            }


            // split calls copy up
            this.split(splitPoint, rightLeaf, tree);

         }
      }

   }
   public void split(int splitPoint, BTNodeLeaf rightLeaf, BPlusTree tree){
      // set nextLeaf pointers
      if (this.nextLeaf != null) {
         rightLeaf.nextLeaf = this.nextLeaf;
      }
      this.nextLeaf = rightLeaf;

      // move Word half of word objects over to rightLeaf
      while (keys.size() > splitPoint) { // while size > 2, remove 3rd element. more efficient this way.
         Word temp = keys.remove(splitPoint);
         rightLeaf.keys.add(temp);
      }

      // COPY UP
      this.copyUp(rightLeaf.keys.get(0).key, tree);
   }

   public void copyUp(String word, BPlusTree tree){
      this.parent.receiveUp(word, tree);
   }
   
   public void printLeavesInSequence()
   {
      BTNodeLeaf cursor = this;
      while (cursor != null) {
         for (Word w : cursor.keys){
            System.out.print(w.key + " ");
         }
         System.out.println();
         cursor = cursor.nextLeaf;
      }
   }
   
   public void printStructureWKeys()
   {

   }
   public void printStructureWKeys(String tabs)
   {

      tabs += "\t\t";
      for (int i = keys.size()-1; i >= 0; i--){
         System.out.println(tabs+"- "+keys.get(i).key+": "+keys.get(i).keyCount);
      }
   }
   
   public Boolean rangeSearch(String startWord, String endWord, BPlusTree tree)
   {
      int i = 0;
      String keyCursor = null;
      BTNodeLeaf leafCursor = this;

      for (i = 0; i < keys.size(); i++){
         if (keys.get(i).key.compareTo(startWord) >= 0){
//            keyCursor = keys.get(i).key;
            break;
         }
      }
      // print rest of this node
      for (i = i; i < keys.size(); i++) {
         keyCursor = keys.get(i).key;
         if (keyCursor.compareTo(endWord) <= 0) {
            System.out.println(keyCursor);
         }
         else {
            return true;
         }
      }
      // print all sibling nodes till keyCursor > endword
      if (keyCursor == null){
         keyCursor = this.nextLeaf.keys.get(0).key;
         // occurs if break between nodes.
         // i.e. this ends with computer. nextLeaf begins with create. startWord is crazy.
      }

      while (keyCursor.compareTo(endWord) <= 0) {
//         System.out.println(keyCursor);

        leafCursor = leafCursor.nextLeaf;
        if (leafCursor == null) {
           return true; // reached end of tree
        }
        for (int j = 0; j < leafCursor.keys.size(); j++) {
           keyCursor = leafCursor.keys.get(j).key;
           if (keyCursor.compareTo(endWord) <= 0) {
              System.out.println(keyCursor);
           }
           else { // early stop w/in node
              break;
           }
        }
      }
      return true;
   }
   
   public Boolean searchWord(String word)
   {
      for (int i = 0; i < keys.size(); i++) {
         if (keys.get(i).key.compareTo(word) == 0){
            return true;
         }
      }

      return false;
   }
}