package nlpfeatures;

import java.io.IOException;

public class NLPFeatures {

   public static void main(String[] args) {
//      Path path = new Path ("src\\Input\\Articles.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
      Path path = new Path ("src\\Input\\Sample.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
      int outputs          = 20;

      Preprocess p = new PreprocessSO_CAL(path, 4);
//      Preprocess p = new PreprocessTF_IDF(path);
      
      try {
         p.excelOutput(outputs);
      } catch (IOException ex) {
         System.err.println(ex.getMessage());
      }
   }
}
