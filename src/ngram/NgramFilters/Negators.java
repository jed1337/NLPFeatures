package Ngram.NgramFilters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A list of Filipino Negators
 * @author Jed Caychingco
 */
public class Negators implements NgramFilters{
   /** A list of Filipino negators */
   private final List<String> negators = newList("hindi di huwag wala ayaw aywan dili");
   
//<editor-fold defaultstate="collapsed" desc="Utility functions">
   
   /**
    * Creates a new formatted List<String> from the words in its String input
    * @param input
    * @return
    */
   private List<String> newList(String input) {
      return Collections.unmodifiableList(Arrays.asList(format(input)));
   }
   
   /**
    * Lowercases the String and returns the words in it
    * @param input
    * @return
    */
   private String[] format(String input) {
      return input.toLowerCase().split("\\s+");
   }
   
   /**
    * Returns the negators
    * @return
    */
   public List<String> getNegators() {
      return negators;
   }
//</editor-fold>

   /**
    * Returns true if at least 1 negator appears in the words
    * @param words
    *
    * @return
    */
   @Override
   public boolean contains(String words) {
      for (String word : format(words)) {
         for (String negator : negators) {
            if (word.equals(negator)) {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * @return "Negators"
    */
   @Override
   public String getName(){
      return "Negators";
   }
}
