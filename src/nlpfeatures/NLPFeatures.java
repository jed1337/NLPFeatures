package nlpfeatures;

import java.io.IOException;

public class NLPFeatures {

   public static void main(String[] args) {
      Path path = new Path ("src\\Input\\Article Annotation [Sir Potch].xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
//      Path path = new Path ("src\\Input\\Article Annotation [Sir Potch].xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
      
      Preprocess p = new PreprocessTF_IDF(path, 1);
//      Preprocess p = new PreprocessSO_CAL(path, 4);
      
      try {
         p.output(90);
//         for(int i=0;i<=10;i++){
//            p.output(i*10);
//         }
      } catch (IOException ex) {
         System.err.println(ex.getMessage());
      }
   }
}
