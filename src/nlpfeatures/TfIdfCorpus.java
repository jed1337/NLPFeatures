package nlpfeatures;

public final class TfIdfCorpus {
   
   /**
    * The Inverse Document Frequency
    */
   private final double dIdf;
   
   public TfIdfCorpus(){
      dIdf = 0;
   }
   public TfIdfCorpus(String[][] docs, String key){
      dIdf = idf(docs, key);
   }
   
   /**
    * @param doc list of strings
    * @param term String represents a term
    * @return term frequency of term in document
    */
   public double tf(String[] doc, String term) {
      double result = 0;
      for (String word : doc) {
         if (term.equalsIgnoreCase(word)) {
            result++;
         }
      }
      //Throw exception for NaN
      return result / doc.length;
   }

   /**
    * @param docs list of list of strings represents the dataset
    * @param key String represents a term
    * @return the inverse term frequency of term in documents
    */
   public double idf(String[][] docs, String key) {
      double n = 0;

      for (String[] doc : docs) {
         for (String word : doc) {
            if (key.equalsIgnoreCase(word)) {
               n++;
               break;
            }
         }
      }
      return Math.log(docs.length / n);
   }

   /**
    * @param doc a text document
    * @param docs all documents
    * @param term term
    * @return the TF-IDF of term
    */
   public double tfIdf(String[] doc, String[][] docs, String term) {
      return tf(doc, term) * idf(docs, term);
   }
   
   public double tfIdf(String[] doc, String term) {
      return tf(doc, term) * dIdf;
   }
}
