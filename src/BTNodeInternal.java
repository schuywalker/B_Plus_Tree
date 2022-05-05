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
   public void insert(String key, BPlusTree tree, int count)
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
      count++;
      if (count > 5){
         System.out.println("woah");
         throw new StackOverflowError("parent calling child or dup nodes?");
      }

      children.get(position).insert(key, tree, count);
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
      else if(key.compareTo(indexWords.get(indexWords.size()-1)) > 1) {
         indexWords.add(key); // add to end
      }
      else if (key.compareTo(indexWords.get(0)) < 0) {
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
      for (BTNode c : children){
         c.printLeavesInSequence();
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
         BTNodeInternal rightSibling = new BTNodeInternal((this.nodeID/2)+1);

         // set up parent/ child pointers
         this.parent = newRoot;
         rightSibling.parent = newRoot;
         newRoot.children.add(this);
         newRoot.children.add(newRoot);

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
      while (indexWords.size() > splitPoint) { // with n = 3, node has 4, move right 2 over
         String temp = indexWords.remove(splitPoint);
         rightSibling.indexWords.add(temp);
      }

      // move children over
      // in n=3, we should have 5 children when splitting, and the rightmost 2 children should move over
      int childrenSplitPoint = (int)Math.ceil(children.size()/2);
      while (children.size() > childrenSplitPoint) {
         BTNode temp = children.remove(childrenSplitPoint);
         rightSibling.children.add(temp);
      }

      parent.receiveUp(rightSibling.indexWords.remove(0), tree);
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