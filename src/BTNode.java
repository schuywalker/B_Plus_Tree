import java.util.ArrayList;

abstract class BTNode
{
   protected int nodeID;
   protected ArrayList<String> keys;
   protected BTNodeInternal parent;
   
   public BTNodeInternal getParent() { return parent; }
   
   public abstract void insert(String key, BPlusTree tree);
      
   public abstract void printLeavesInSequence();
      
   public abstract void printStructureWKeys();
   
   public abstract Boolean rangeSearch(String startWord, String endWord);
   
   public abstract Boolean searchWord(String word);

   public void setNodeId(int nodeId){
      this.nodeID = nodeId;
   }
}
/* INSERTION
• Find correct leaf page L.
• Put data entry onto L.
   – If L has enough space, done!
   – Else, must split L (into L and a new page L2)
• Redistribute entries evenly, copy up middle key.
• Insert index entry pointing to L2 into parent of L.
• This can happen recursively
   – To split index node, redistribute entries evenly, but
      push up middle key
• Split “grow” tree; root split increases height.
   – Tree grow: gets wider or one level taller at top.
 */