package Tfidf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import nlpfeatures.Path;
import nlpfeatures.Preprocess;

/**
 * Used to get the most relevant words in the corpus. This is determined by 
 * their term frequency - inverse document frequency (TF-IDF) value.
 * @author Jed Caychingco
 */
public class PreprocessTF_IDF extends Preprocess {
   /** Contains the words in the article along with their TF-IDF value */
   private HashMap<String, Float> corpusWords;
   

   /**
    * Creates an instance of this class from the 
    * specified path using unigrams
    * @param path The path to the excel file containing article. This serves as the input.
    */
   public PreprocessTF_IDF(Path path) {
      this(path, 1);
   }
   
   /**
    * Creates an instance of this class from the 
    * specified path using the specified ngram count
    * @param path The path to the excel file containing article. This serves as the input.
    * @param ngCount The number of ngrams to use
    */
   public PreprocessTF_IDF(Path path, int ngCount) {
      super(path, ngCount);
      
      setCorpusWords();
      removeInvalidWords(corpusWords, hashMap->removeStopWords(hashMap.keySet()));
   }
   
   
   /**
    * Sets the corpus words
    * Uses the TFIDF Calculator
    */
   private void setCorpusWords() {
      this.corpusWords = new HashMap<>();
      int articleCount = this.articles.size();

      TFIDFCalculator calculator = new TFIDFCalculator(this.articles);
      for (String key : super.getUniqueWords()) {
         float value = 0;

         calculator.setKey(key);
         for (int j = 0; j < articleCount; j++) {
            value += calculator.tfIdf(j);
         }
         this.corpusWords.put(key, value);
      }
   }
   
   /**
    * Returns the words in the article split by its symbols
    * @param article
    * @return 
    */
   @Override
   protected String[] format(String article){
      return article.toLowerCase()
         .replaceAll(REGEX_WHITE_LIST, " ")
         .split("\\s+");
   }

//<editor-fold defaultstate="collapsed" desc="Outputs">
   /**
    * Creates a .ser file containing the most relevant words in the corpus
    * @param percentage
    * @throws IOException 
    */
   @Override
   public void output(int percentage) throws IOException {
      try{
         FileOutputStream fos   = new FileOutputStream(
            String.format("%sBagOfWords %d_%d gram.ser", outputPath, percentage, ngCount));
         ObjectOutputStream oos = new ObjectOutputStream(fos);
         
         ArrayList<String> temp = new ArrayList<>(removeLowPercentageWords(percentage).keySet());
         System.out.println("Size = " + temp.size());
         
         oos.writeObject(temp);
         
         closeSafely(oos, fos);
      } catch(IOException ie){
         printErrors(ie);
      }
   }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Removers">
   /**
    * Removes the stopwords from the given collection
    * @param collection The collection without stopwords
    */
   private void removeStopWords(Collection collection) {
      collection.removeAll(stopwords);
   }

   /**
    * Removes the low words
    * @param percentage The cut off percentage
    * @return A new HashMap without the low percentage words
    */
   private HashMap<String, Float> removeLowPercentageWords(float percentage) {
      int cutoff = (int) Math.floor(corpusWords.size() * percentage / 100);
      
      HashMap<String, Float> tempMap = new HashMap<>(corpusWords);
      
      int[] idx = {0};
      corpusWords.entrySet().stream()
         .sorted(Map.Entry.comparingByValue())
         .forEach(e ->{
            if(idx[0] == cutoff){
               System.out.println(e.getValue());
               tempMap.values().removeIf(v->v < e.getValue());  //Remove low percentage entries
            }
            idx[0]++;
         }
      );
      
      return tempMap;
   }
//</editor-fold>
}
