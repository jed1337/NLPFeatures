package nlpfeatures;

import java.io.IOException;

public class NLPFeatures {

   public static void main(String[] args) {
//      Path path = new Path ("src\\Input\\Cagampan390-Test.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
//      Path path = new Path ("src\\Input\\Carmen330-Divided.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
      Path path = new Path ("src\\Input\\Article-Annotation-Lineth.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
//      Path path = new Path ("src\\Input\\Lineth400.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
//      Path path = new Path ("src\\Input\\Positive.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
      
//      Preprocess p = new PreprocessTF_IDF(path);
      Preprocess p = new PreprocessSO_CAL(path, 4);
      
      try {
         p.output(10);
      } catch (IOException ex) {
         System.err.println(ex.getMessage());
      }
   }
}
