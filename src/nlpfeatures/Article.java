package nlpfeatures;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.function.Supplier;
import ngram.Ngram;

public class Article {
   private final String fullArticle;
   private final String[] ngrams;
   private final Sentiment actualSentiment;
   
   private final EnumMap<ClassifierNames, Sentiment> predictedSentiments;
   
   public Article(String fullArticle, String sentiment, Supplier<String[]> supplier, int ngCount){
      this.fullArticle     = fullArticle;
      this.ngrams          = Ngram.getNgrams(supplier.get(), ngCount);
      
      this.actualSentiment     = Sentiment.getSentiment(sentiment);
      this.predictedSentiments = new EnumMap<>(ClassifierNames.class);
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

   public EnumMap<ClassifierNames, Sentiment> getPredictedSentiments() {
      return predictedSentiments;
   }
   
   public Sentiment getPredictedSentiment(ClassifierNames classifierName) {
      return predictedSentiments.get(classifierName);
   }

   public void addPredictedSentiment(ClassifierNames classifierName, Sentiment sentiment){
      this.predictedSentiments.put(classifierName, sentiment);
   }
}
