import java.util.Collections;
import java.util.ArrayList;

/*
Author: Schuyler Asplin
Project 2 for CSCD 427 with Professor Dan Li
 */

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
      /*
      insert doesn't actually insert a key.
      This method travels down the tree to find the leaf that the key belongs in.
       */
      int position = 0;
      for (int i = indexWords.size()-1; i >= 0; i--) {
         // if key is >= word, insert on right
         if (key.compareTo(indexWords.get(i)) >= 0) {
            position = i + 1; // 4 children for for a node of size 3
            break;
         }
      }

      if (position == children.size()) {
         children.get(position).insert(key, tree);
      }
      children.get(position).insert(key, tree);
   }

   public void receiveUp(String key, BPlusTree tree){
      /*
      This method actually inserts a word into the node (insert does not).
      This is always called from a child, either in a leaf node's copyUp,
      or in an internal node's moveUp.
      In other words, this method travels up the tree.
      After node is inserted, node is checked for an overflow, and if overflow occurs,
      moveUp is called after splitting is handled at this level.
       */

      // insert word
      if (indexWords.size() == 0) {
         indexWords.add(key); // add first word. only happens on first insert. can remove later for efficiency.
      }
      else if(key.compareTo(indexWords.get(indexWords.size()-1)) > 0) {
         indexWords.add(key); // add to end
      }
      else if (key.compareTo(indexWords.get(0)) < 0) {
         indexWords.add(0, key); // add to 0th
      }
      else {
         for (int i = indexWords.size()-1; i >= 1; i--) {
            if (key.compareTo(indexWords.get(i)) < 0 && key.compareTo(indexWords.get(i-1)) > 0) {
               indexWords.add(i, key); // found correct position, insert in middle
               break;
            }
         }
      }


      // check for overflow, if so, move up
      if (indexWords.size() > tree.n){

         this.moveUp(tree);

      }

   }

   public void moveUp(BPlusTree tree) {
      /* this is the 'internal node overflow handler'.
       in case of n=3, means we're removing 3rd word, moving it to parent, and
       splitting the 4th word into a new node with 1 word and 2 pointers (children).
       left child (this) will have 2 words and 3 pointers (children).
       for simpler code, 4 word node is split into 2, and then right node's
       leftmost word is removed and sent up to parent, using receiveUp()
      */


         int splitPoint = (int)Math.ceil(indexWords.size()/2);
         // ^ always 4/2 = 2 for our purposes. would be relevant if n is odd
      if (this.parent == null) {

         // make parent, set IDs
         BTNodeInternal newRoot = new BTNodeInternal(tree.nodeIDAssigner++);
         BTNodeInternal rightSibling = new BTNodeInternal(tree.nodeIDAssigner++);

         // set up parent/ child pointers
         this.parent = newRoot;
         rightSibling.parent = newRoot;
         newRoot.children.add(this);
         newRoot.children.add(rightSibling);

         // split calls receive up
         this.split(splitPoint, rightSibling, tree);
      }
      else {
         //create sibling
         BTNodeInternal rightSibling = new BTNodeInternal(tree.nodeIDAssigner++);

         // set parent and child pointers
         rightSibling.parent = this.parent;

         // find position to insert in parent's children
         boolean success = true;
         for (int i = 0; i < children.size(); i++){
            if (parent.children.get(i) == this) {
               parent.children.add(i+1,rightSibling);
               success = true;
               break;
            }
         }
         if (!success) {
            throw new RuntimeException("Trouble in MoveUp else");
         }

         // split calls receive up
         this.split(splitPoint, rightSibling, tree);

      }
   }

   public void split(int splitPoint, BTNodeInternal rightSibling, BPlusTree tree){
      // move indexWords over (Strings)
      while (this.indexWords.size() > splitPoint) { // with n = 3, node has 4, move right 2 over
         String temp = this.indexWords.remove(splitPoint);
         rightSibling.indexWords.add(temp);
      }

      // move children over
      // in n=3, we should have 5 children when splitting, and the rightmost 2 children should move over
//      int childrenSplitPoint = (int)Math.ceil(children.size()/2); // evald to 2??
      int childrenSplitPoint = (children.size()+1)/2;
      while (children.size() > childrenSplitPoint) {
         BTNode temp = children.remove(childrenSplitPoint);
         rightSibling.children.add(temp);
         temp.parent = rightSibling;
      }

      parent.receiveUp(rightSibling.indexWords.remove(0), tree);
   }
   
   public void printLeavesInSequence()
   {
      children.get(0).printLeavesInSequence();
   }
   
   public void printStructureWKeys()
   {
      System.out.println("Print structure:");
      for (int i = indexWords.size()-1; i >= 0; i--){

         children.get(i+1).printStructureWKeys("");

         if (i == 0) {
            System.out.print(" / "+nodeID+": "+indexWords.get(i)+"\n");
         }
         else if (i == indexWords.size()-1) {
         System.out.print(" \\ "+nodeID+": "+indexWords.get(i)+"\n");
         }
         else {
            System.out.print(" - "+nodeID+": "+indexWords.get(i)+"\n");
         }

      }

         children.get(0).printStructureWKeys("");

      System.out.println("\n");
   }
   public void printStructureWKeys(String tabs){
      tabs += "\t\t";
      for (int i = indexWords.size()-1; i >= 0; i--) {
         children.get(i+1).printStructureWKeys(tabs);

         if (i == indexWords.size()-1){
         System.out.print(tabs+" / "+nodeID+": "+indexWords.get(i)+"\n");
         }
         else if (i == 0) {
            System.out.print(tabs+" \\ "+nodeID+": "+indexWords.get(i)+"\n");
         }
         else {
            System.out.print(tabs+" - "+nodeID+": "+indexWords.get(i)+"\n");
         }
      }
      children.get(0).printStructureWKeys(tabs);
   }
   
   public Boolean rangeSearch(String startWord, String endWord, BPlusTree tree)
   {


      if (startWord.compareTo(indexWords.get(indexWords.size()-1)) >= 0)
      { // if we need to search in the right most child
         children.get(children.size()-1).rangeSearch(startWord, endWord, tree);
         // will return out of function
      }
      for (int i = 0; i < this.indexWords.size(); i++)
      {
         if (startWord.compareTo(indexWords.get(i)) < 0){
            children.get(i).rangeSearch(startWord, endWord, tree);
            break;
         }
      }




      return true;
   }
   
   public Boolean searchWord(String word)
   {

         if (word.compareTo(indexWords.get(indexWords.size()-1)) >= 0){
            return children.get(children.size()-1).searchWord(word);
         }
         else {
            for (int i = 0; i < indexWords.size(); i++) {
               if (word.compareTo(indexWords.get(i)) < 0) {
                  return children.get(i).searchWord(word);
               }
            }
            System.out.println("problem searchWord");
            return false;
         }
   }
}