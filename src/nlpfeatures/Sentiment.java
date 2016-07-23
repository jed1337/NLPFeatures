package nlpfeatures;

import java.util.Arrays;
import java.util.InputMismatchException;

public enum Sentiment {
   POSITIVE, NEGATIVE, NEUTRAL, NONE;
   
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
}
