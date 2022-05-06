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

   // Done
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

   // Done
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


//      this.printLeavesInSequence();
//      for (BTNode c : children){
//         c.printLeavesInSequence();
//      }

      if (this.children.size() > 5){
         System.out.println("children bug on "+key);
      }

      // check for overflow, if so, move up
      if (indexWords.size() > tree.n){

         this.moveUp(tree);

      }

   }

   // Not done, moving children over ~ 112
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
         BTNodeInternal newRoot = new BTNodeInternal(this.nodeID);
         this.setNodeId((this.nodeID/2)-1);
         BTNodeInternal rightSibling = new BTNodeInternal((this.nodeID*2)+1);

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
         BTNodeInternal rightSibling = new BTNodeInternal((this.nodeID/2)+1);

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
      System.out.print("\ninternal "+this.nodeID+": ");
      for (String s : indexWords) {
      System.out.print(s + "  ");
      }
      System.out.println();

   }
   
   public void printStructureWKeys()
   {
      System.out.println("Print structure:");
      for (int i = indexWords.size()-1; i >= 0; i--){

         children.get(i+1).printStructureWKeys("");

         System.out.print(" - "+nodeID+": "+indexWords.get(i)+"\n");

      }

         children.get(0).printStructureWKeys("");

      System.out.println("\n");
   }
   public void printStructureWKeys(String tabs){
      tabs += "\t\t\t";
      for (int i = indexWords.size()-1; i >= 0; i--) {
         children.get(i+1).printStructureWKeys(tabs);

         System.out.print(tabs+" / "+nodeID+": "+indexWords.get(i)+"\n");
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

      if (children.get(0).getClass() == BTNodeInternal.class) { // child is internal. compare will not return 0.
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