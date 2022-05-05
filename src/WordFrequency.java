/* CSCD 427 - Project 2 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.*;
import java.util.*;


class WordFrequency
{
   public static void main(String[] args)
   {
      HashSet<String> ignoreSet = new HashSet<String> (); 
      Document document = null;
      String doc_text = "";
      String[] tokenized_text = null;
      BPlusTree tree;
      Scanner fin;
      Scanner kb;
      
      //Check for valid arguments
      if(args.length < 1 || args.length > 2)
      {
         System.out.println("ERROR: Incorrect format for command line arguments.");
         System.out.println("java WordFrequency <URL> <ignore_file_name.txt>(optional)");
         System.out.println("Exiting.");
         System.exit(-1);
      }
      
      //Program will take time to set up tree; inform user
      System.out.println("WordFrequency is processing HTML. Please wait...");
      
      //Read stopwords ignoreSet (if provided)
      if(args.length == 2)
      {
         try
         {
            fin = new Scanner(new File(args[1]));
            while(fin.hasNextLine())
            {
               ignoreSet.add(fin.nextLine());
            }
            fin.close();
         }
         catch(Exception e)
         {
            System.out.println("File IO Exception; initializing BPlusTree without stopwords.");
         }
      }
       
      //Initialize BPlusTree
      tree = ignoreSet.size() > 0 ? new BPlusTree(ignoreSet) : new BPlusTree(null);
        
      /* Get HTML.
       * Convert HTML to string.
       * Tokenize string. */
      try
      {
         document = Jsoup.connect(args[0]).get();
         doc_text = document.text();
         tokenized_text = doc_text.split("(\\W+)?\\s(\\W+)?");
         tree.setWordCount(tokenized_text.length);
      }
      catch(Exception e)
      {
         System.out.println(e.toString());
         System.exit(-1);
      }
      
      for(String s: tokenized_text)
      {
         tree.insertWord(s);
      }
      
      document = null;
      doc_text = null;
      tokenized_text = null;
      
      //Initialize keyboard scanner
      kb = new Scanner(System.in);
      
      mainMenu(tree);
   }
   
   private static void mainMenu(BPlusTree tree)
   {
      Scanner kb = new Scanner(System.in);
      String userInput;
      int menuSelect = 0;
      Boolean validSelect = true;
      Boolean quit = false;
      
      do
      {
         System.out.println();
         
         //Show menu
         System.out.println("MAIN MENU");
         System.out.println("1) Print all words in order.");
         System.out.println("2) Display tree with Node IDs and keys.");
         System.out.println("3) Insert a word.");
         System.out.println("4) Search a word.");
         System.out.println("5) Search by range.");
         System.out.println("6) Quit.");
         
         do
         {
            System.out.print("Select an option: ");
            
            validSelect = true;
            
            //Get user selection
            userInput = kb.nextLine();
            
            //Validate selection input
            try
            {
               menuSelect = Integer.parseInt(userInput);
               
               if(menuSelect < 1 || menuSelect > 6)
               {
                  System.out.println("Invalid selection. Enter an integer from 1 to 6.");
                  validSelect = false;
               }
            }
            catch(NumberFormatException nfe)
            {
               System.out.println("Invalid selection. Enter an integer from 1 to 6.");
               validSelect = false;
            }
         }while(!validSelect);
         
         System.out.println();
         
         //Execute menu selection
         switch(menuSelect)
         {
            case 1:
               tree.printLeavesInSequence();
               break;
               
            case 2:
               tree.printStructureWKeys();
               break;
               
            case 3:
               tree.userInsertWord(kb);
               break;
            
            case 4:
               tree.searchWord(kb);
               break;
            
            case 5:
               tree.rangeSearch(kb);
               break;
               
            default:
               quit = true;
               break;
         }
         
      }while(!quit);
      
      System.out.println("Quitting.");
   }
}