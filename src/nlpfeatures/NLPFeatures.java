package nlpfeatures;

import java.io.IOException;

public class NLPFeatures {

   public static void main(String[] args) {
      String inputPath     = "src\\Input\\Articles.xlsx";
//      String inputPath     = "src\\Input\\Sample.xlsx";
      String outputPath    = "src\\Output\\";
      String stopwordsPath = "src\\Input\\Stopwords.txt";
      int outputs          = 20;

      Preprocess p = new PreprocessSO_CAL(inputPath, outputPath, stopwordsPath, 4);
//      Preprocess p = new PreprocessTF_IDF(inputPath, outputPath, stopwordsPath);
      
      try {
         p.excelOutput(outputs);
      } catch (IOException ex) {
         System.err.println(ex.getMessage());
      }
   }
}
