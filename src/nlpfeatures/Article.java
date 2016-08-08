package nlpfeatures;

import java.util.HashMap;
import java.util.function.Supplier;
import ngram.Ngram;

/**
 * The class containing the article, its ngrams, 
 * sentiment, and its predicted sentiments
 * @author Jed Caychingco
 */
public class Article {
   /** The article without any alterations */
   private final String fullArticle;
   
   /** The article's ngrams */
   private final String[] ngrams;
   
   /** The article's actual sentiment */
   private final Sentiment actualSentiment;
   
   /** The article's predicted sentiments */
   private final HashMap<String, Sentiment> predictedSentiments;
   
   /**
    * Used to create an instance of this class
    * @param fullArticle The article
    * @param sentiment Its actual sentiment
    * @param supplier The custom formatting to be used
    * @param ngCount The amount of ngrams to be used.
    */
   public Article(String fullArticle, String sentiment, Supplier<String[]> supplier, int ngCount){
      this(fullArticle, Sentiment.getSentiment(sentiment), supplier, ngCount);
   }
   
   /**
    * Used to create an instance of this class
    * @param fullArticle The article
    * @param sentiment Its actual sentiment
    * @param supplier The custom formatting to be used
    * @param ngCount The amount of ngrams to be used.
    */
   public Article(String fullArticle, Sentiment sentiment, Supplier<String[]> supplier, int ngCount){
      this.fullArticle     = fullArticle.toLowerCase();
      this.ngrams          = Ngram.getNgrams(supplier.get(), ngCount);
      
      this.actualSentiment     = sentiment;
      this.predictedSentiments = new HashMap<>();
   }

   /**
    * Returns the full article
    * @return 
    */
   public String getFullArticle() {
      return fullArticle;
   }

   /** 
    * Returns the ngrams in the article 
    * @return 
    */
   public String[] getWords() {
      return ngrams;
   }

   /**
    * Returns the actual sentiment
    * @return 
    */
   public Sentiment getActualSentiment() {
      return actualSentiment;
   }

   /**
    * Returns the predicted sentiments
    * @return 
    */
   public HashMap<String, Sentiment> getPredictedSentiments() {
      return predictedSentiments;
   }
   
   /**
    * Returns the predicted sentiment from a specific classifier
    * @param classifierName
    * @return 
    */
   public Sentiment getPredictedSentiment(String classifierName) {
      return predictedSentiments.get(classifierName);
   }

   /**
    * Adds the predicted sentiment of a classifier
    * @param classifierName
    * @param sentiment 
    */
   public void addPredictedSentiment(String classifierName, Sentiment sentiment){
      this.predictedSentiments.put(classifierName, sentiment);
   }
}
