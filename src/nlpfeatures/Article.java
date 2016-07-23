package nlpfeatures;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.function.Supplier;
import ngram.Ngram;

public class Article {
   private final String fullArticle;
   private final String[] ngrams;
   private final Sentiment actualSentiment;
   
   private final HashMap<String, Sentiment> predictedSentiments;
   
   public Article(String fullArticle, String sentiment, Supplier<String[]> supplier, int ngCount){
      this(fullArticle, Sentiment.getSentiment(sentiment), supplier, ngCount);
   }
   
   public Article(String fullArticle, Sentiment sentiment, Supplier<String[]> supplier, int ngCount){
      this.fullArticle     = fullArticle;
      this.ngrams          = Ngram.getNgrams(supplier.get(), ngCount);
      
      this.actualSentiment     = sentiment;
      this.predictedSentiments = new HashMap<>();
   }

   public String getFullArticle() {
      return fullArticle;
   }

   public String[] getWords() {
      return ngrams;
   }

   public Sentiment getActualSentiment() {
      return actualSentiment;
   }

   public HashMap<String, Sentiment> getPredictedSentiments() {
      return predictedSentiments;
   }
   
   public Sentiment getPredictedSentiment(String classifierName) {
      return predictedSentiments.get(classifierName);
   }

   public void addPredictedSentiment(String classifierName, Sentiment sentiment){
      this.predictedSentiments.put(classifierName, sentiment);
   }
}
