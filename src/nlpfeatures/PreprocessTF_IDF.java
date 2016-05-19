package nlpfeatures;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PreprocessTF_IDF extends Preprocess {
   private HashMap<String, Float> corpusWords;

   public PreprocessTF_IDF(Path path) {
      super(path, true);
      
      setCorpusWords();
      removeStopWords(corpusWords.keySet());
      removeInvalidWords(corpusWords);
   }

//<editor-fold defaultstate="collapsed" desc="TF IDF Outputs">
   @Override
   public void excelOutput(float outputs) throws IOException {
      output(outputs, true);
   }
   
   public void csvOutput(float outputs) throws IOException {
      output(outputs, false);
   }
   
   @Override
   public void output(float outputs, boolean isExcel) throws IOException {
      for (float i = 0; i < outputs; i++) {
         float percentage = (float) Math.floor(1.0 * (i / outputs) * 100);
         Set<String> keys = removeLowPercentageWords(percentage);
         
         if (isExcel) {
            ExcelOutput.output(articles, keys, outputPath + percentage + "%.xlsx");
         } else {
            CSVOutput.output(articles, keys, outputPath + percentage + "%.csv");
         }
      }
   }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Removers">
   private void removeInvalidWords(HashMap<String, Float> corpusWords) {
      //Remove words of length 1 except for "I"
      corpusWords.keySet().removeIf(s->s.length() <= 1 && !s.equalsIgnoreCase("i"));

      //Remove the "-" from words Ex: "-anyos" -> "anyos", "--frj" -> "frj"
      //Remove the words which begin in "-"
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

   private Set<String> removeLowPercentageWords(float percentage) {
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
      
      return tempMap.keySet();
   }
//</editor-fold>

   public HashMap<String, Float> getCorpusWords() {
      return corpusWords;
   }

   private void setCorpusWords() {
      this.corpusWords = new HashMap<>();
      int articleCount = this.articles.size();

      TFIDFCorpus calculator = TFIDFCorpus.getSingleton(this.articles);
      for (String key : getUniqueWords()) {
         float value  = 0;

         calculator.setKey(key);
         for (int j = 0; j < articleCount; j++) {
            value += calculator.tfIdf(j);
         }
         this.corpusWords.put(key, value);
      }
   }
}
