package nlpfeatures;

public final class TFIDFCorpus {
   private final String[][] articles;
   private double dIdf;
   private String key;
   
   private static TFIDFCorpus singleton = null;

   private TFIDFCorpus(String[][] articles) {
      this.articles = articles;
      this.key = null;
   }
   
   public static TFIDFCorpus getSingleton(String[][] articles){
      if(singleton == null){
         singleton = new TFIDFCorpus(articles);
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
      
      for (String word : this.articles[articleNum]) {
         if (this.key.equals(word)) {
            result++;
         }
      }
      
      result /= this.articles[articleNum].length;
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
   private double idf(String[][] docs, String key) {
      double n = 0;
      
      for (String[] doc : docs) {
         for (String word : doc) {
            if (key.equals(word)) {
               n++;
               break;
            }
         }
      }
      return Math.log(docs.length / n);
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
