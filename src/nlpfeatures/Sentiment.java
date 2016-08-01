package nlpfeatures;

import java.util.Arrays;
import java.util.InputMismatchException;

/**
 * An enum class containing the sentiments used.
 * @author Jed Caychingco
 */
public enum Sentiment {
   /** A positive sentiment */
   POSITIVE, 
   
   /** A negative sentiment */
   NEGATIVE, 
   
   /** A neutral sentiment */
   NEUTRAL, 
   
   /** Used if the word has no sentiment yet. */
   NONE;
   
   /** 
    * A list of words to not be considered when determining a word's sentiment.
    * It is present here since these words are headers in the input Excel file.
    */
   private static final String[] whiteList=new String[]{"article", "sentiment"};

   /**
    * Gets the enum equivalent of the sentiment passed
    * @param sentimentStr
    * @return 
    * @throws InputMismatchException if the {@code sentimentStr}
    * is not a valid Sentiment
    */
   public static Sentiment getSentiment(String sentimentStr) {
      for(Sentiment sentiment: Sentiment.values()){
         if(sentiment.toString().equalsIgnoreCase(sentimentStr)){
            return sentiment;
         }
      }
      
      for (String wl : whiteList) {
         if(wl.equalsIgnoreCase(sentimentStr)){
            return NONE;
         }
      }
      
      throw new InputMismatchException(String.format(
         "'%s' is not a valid sentiment. Valid options are: %s", sentimentStr, Arrays.toString(Sentiment.values())));
   }
   
   @Override
   public String toString(){
      return this.name();
   }
}
