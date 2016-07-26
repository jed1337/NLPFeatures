package Ngram.NgramFilters;

/**
 * An interface contains utility functions for ngrams
 * @author Jed Caychingco
 */
public interface NgramFilters {
   /**
    * Filters s, then returns true if it is present in the ngrams list.
    * @param str The word to be checked
    * @return 
    */
   public boolean contains(String str);
   
   /**
    * Returns the name of the filter used
    * @return 
    */
   public String getName();
}
