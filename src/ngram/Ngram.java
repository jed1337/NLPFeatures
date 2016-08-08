package ngram;

import java.util.Arrays;
import java.util.StringJoiner;

public class Ngram {
   
   /**
    * Formats an article section (1 sentence, etc.)
    * @param articleSection
    * @return A string array. Each String in the array contains 1 word
    */
   private static String[] formatString(String articleSection) {
      return Arrays.stream(articleSection.split("\\s+"))
         .map(String::toLowerCase)  //lowercase all array elements
         .map(String::trim)         //Trim all array elements
         .filter(s -> !s.isEmpty()) //Remove ""s
         .toArray(String[]::new);   //Convert back to an array
   }
   
   /**
    * Concatenates the words in the given array from the start index to
    * before the end index "[start, end)"
    * 
    * @param words Array of words to concatenate
    * @param start Start index
    * @param end End index
    * @return 
    */
   public static String concatWords(String[] words, int start, int end) {
      StringJoiner sj = new StringJoiner(" ");
      for (int i = start; i < end; i++) {
         sj.add(words[i].toLowerCase());
      }
      return sj.toString();
   }
   
   /**
    * Returns the words with their appropriate ngram count
    * @param words An array containing the input words
    * @param ngramCount How many ngrams are needed
    * @return 
    */
   public static String[] getNgrams(String[] words, int ngramCount) {
      final int size  = words.length - ngramCount + 1;
      
      if(size<0){
         return new String[]{};
      }
      
      String[] ngrams = new String[size];
      
      for (int i = 0; i < size; i++) {
         ngrams[i] = concatWords(words, i, i+ngramCount);
      }
      return ngrams;
   }
}
