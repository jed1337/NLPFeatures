package nlpfeatures.ngram;

import java.io.IOException;
import java.util.Map;

public class Driver {
   public static void printMap(Map<String, MutableInt> map) {
      map.entrySet().stream()
         .sorted(Map.Entry.comparingByValue())
         .forEachOrdered((entry)->{
            System.out.println("[Key] :___" + entry.getKey() + "___[Value] : " + entry.getValue().get());
         });
   }

   public static void main(String[] args) throws IOException {
      String corpusPath  = "src\\Input\\Corpus198.xlsx";
//      String corpusPath  = "src\\Input\\Formatted To tag.xlsx";
//      String articlePath = "src\\Input\\15Articles.xlsx";
      String articlePath = "src\\Input\\Corpus198.xlsx";
//      String articlePath = "src\\Input\\Formatted To tag.xlsx";
      String outputPath  = "src\\Output\\Output";
      
      new Ngram(corpusPath, articlePath, outputPath, 2, 5, 2, 5);
//      new Ngram(corpusPath, articlePath, outputPath, 3, 2);

//      printMap(en.getCorpus());
   }
}
