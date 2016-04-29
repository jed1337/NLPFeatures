package nlpfeatures;

import java.io.IOException;

public class NLPFeatures {

   public static void main(String[] args) {
//      String inputPath     = "src\\Input\\Articles.xlsx";
      String inputPath     = "src\\Input\\Sample.xlsx";
      String outputPath    = "src\\Output\\";
      String stopwordsPath = "src\\Input\\Stopwords.txt";
      int outputs          = 1;

//      PreprocessSO_CAL p = new PreprocessSO_CAL(inputPath, outputPath, stopwordsPath);
      Preprocess p = new PreprocessTF_IDF(inputPath, outputPath, stopwordsPath);
      
      try {
         p.excelOutput(outputs);
      } catch (IOException ex) {
         System.err.println(ex.getMessage());
      }
   }
}
