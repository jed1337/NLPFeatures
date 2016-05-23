package nlpfeatures;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PreprocessTF_IDF extends Preprocess {
   private HashMap<String, Float> corpusWords;

   public PreprocessTF_IDF(Path path) {
      super(path, true);
      
      setCorpusWords();
      removeStopWords(corpusWords.keySet());
      removeInvalidWords(corpusWords);
      
      corpusWords.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(System.out::println);
   }

//<editor-fold defaultstate="collapsed" desc="TF IDF Outputs">
   @Override
   public void output(float outputs) throws IOException {
      for (float i = 0; i < outputs; i++) {
         float percentage = (float) Math.floor(1.0 * (i / outputs) * 100);
         
         try{
            FileOutputStream fos   = new FileOutputStream(outputPath+"BagOfWords "+percentage);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            ArrayList<String> temp = new ArrayList<>();
            temp.addAll(removeLowPercentageWords(percentage).keySet());
            oos.writeObject(temp);
            
            closeSafely(oos);
            closeSafely(fos);
         } catch(IOException ie){
            printErrors(ie);
         }
      }
   }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Removers">
   private void removeInvalidWords(HashMap<String, Float> corpusWords) {
      remove1LetterWords(corpusWords);
      removeDash(corpusWords);
   }

   /**
    * Remove the "-" from words Ex: "-anyos" -> "anyos", "--frj" -> "frj"
    * Remove the words which begin in "-"
    * @param corpusWords 
    */
   private void removeDash(HashMap<String, Float> corpusWords){
      Map<String, Float> temp = corpusWords.entrySet().stream()
         .filter(entry->entry.getKey().startsWith("-"))
         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      
      corpusWords.entrySet().removeAll(temp.entrySet());

      //Place the words without their "-"
      temp.entrySet().forEach((entry)->{
         String key = entry.getKey();
         while (key.startsWith("-")) {
            key = key.substring(1);
         }
         corpusWords.put(key, entry.getValue());
      });
   }
   
   /**
    * Remove words of length 1 except for "I"
    * @param corpusWords 
    */
   private void remove1LetterWords(HashMap<String, Float> corpusWords){
      corpusWords.keySet().removeIf(s->s.length() <= 1 && !s.equalsIgnoreCase("i"));
   }

   private HashMap<String, Float> removeLowPercentageWords(float percentage) {
      int cutoff = (int) Math.floor(corpusWords.size() * percentage / 100);
      
      HashMap<String, Float> tempMap = new HashMap<>(corpusWords);
      
      int[] idx = {0};
      corpusWords.entrySet().stream()
         .sorted(Map.Entry.comparingByValue())
         .forEach(e ->{
            if(idx[0] == cutoff){
               tempMap.values().removeIf(v->v < e.getValue());  //Remove low percentage entries
            }
            idx[0]++;
         }
      );
      
      return tempMap;
   }
//</editor-fold>

   /**
    * Sets the corpus words
    * Uses the TFIDF Calculator
    */
   private void setCorpusWords() {
      this.corpusWords = new HashMap<>();
      int articleCount = this.articles.size();

      TFIDFCalculator calculator = TFIDFCalculator.getSingleton(this.articles);
      for (String key : getUniqueWords()) {
         float value  = 0;

         calculator.setKey(key);
         for (int j = 0; j < articleCount; j++) {
            value += calculator.tfIdf(j);
         }
         this.corpusWords.put(key, value);
      }
   }
   
   /**
    * Returns the words in the article
    * @param article
    * @return 
    */
   @Override
   protected String[] format(String article){
      return article.toLowerCase()
         .replaceAll(REGEX_WHITE_LIST, " ")
         .split("\\s+");
   }
}
