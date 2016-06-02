package nlpfeatures;

import java.util.ArrayList;

public final class TFIDFCalculator {
   private final ArrayList<Article> articles;
   private double dIdf;
   private String key;
   
   private static TFIDFCalculator singleton = null;

   private TFIDFCalculator(ArrayList<Article> articles) {
      this.articles = articles;
      this.key = null;
   }
   
   /**
    * Is a singleton since only 1 set of articles is used.
    * Doing this avoids unnecessary initializations
    * @param articles
    * @return 
    */
   public static TFIDFCalculator getSingleton(ArrayList<Article> articles){
      if(singleton == null){
         singleton = new TFIDFCalculator(articles);
      }
      singleton.key = null;
      return singleton;
   }

   public void setKey(String key) {
      this.key = key;
      this.dIdf = idf(articles, key);
   }
   
   /**
    * @param articleNum The article number
    * @return term frequency of term in document
    */
   private double tf(int articleNum) throws IllegalStateException, ArithmeticException{
      double result = 0;
      
      if(this.key == null){
         throw new IllegalStateException("The key has not been set.");
      }
      
      for (String word : this.articles.get(articleNum).getWords()) {
         if (this.key.equals(word)) {
            result++;
         }
      }
      
      result /= this.articles.get(articleNum).getWords().length;
      if(result == Double.NaN){
         throw new ArithmeticException("Result is NaN");
      }
      return result;
   }

   /**
    * @param docs list of list of strings represents the dataset
    * @param key String represents a term
    * @return the inverse term frequency of term in documents
    */
   private double idf(ArrayList<Article> articles, String key) {
      double n = 0;
      
      for (Article article : articles) {
         for (String word : article.getWords()) {
            if (key.equals(word)) {
               n++;
               break;
            }
         }
      }
      return Math.log(articles.size() / n);
   }

   /**
    * @param articleNum Article number
    * @throws IllegalStateException when the key is not set
    * @throws ArithmeticException when the tf function is NaN
    * @return the TF-IDF of term
    */
   public double tfIdf(int articleNum) throws IllegalStateException, ArithmeticException{
      return tf(articleNum) * this.dIdf;
   }
}