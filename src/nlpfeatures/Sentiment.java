package nlpfeatures;

import java.util.Arrays;
import java.util.InputMismatchException;

public enum Sentiment {
   POSITIVE, NEGATIVE, NEUTRAL;

   /**
    * Gets the enum equivalent of the sentiment passed
    * @param sentimentStr
    * @return 
    * @throws InputMismatchException if the {@code sentimentStr}
    * is not a valid Sentiment
    */
   public static Sentiment getSentiment(String sentimentStr) {
      sentimentStr = sentimentStr.toUpperCase();
      
      for(Sentiment s: Sentiment.values()){
         if(s.toString().equals(sentimentStr)){
            return s;
         }
      }
      throw new InputMismatchException
        (sentimentStr+" is not a valid sentiment. Valid options are "+Arrays.toString(Sentiment.values()));
   }
}
