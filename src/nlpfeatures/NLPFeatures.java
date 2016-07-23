package nlpfeatures;

import Socal.PreprocessSO_CAL;
import java.io.IOException;

public class NLPFeatures {

   public static void main(String[] args) {
//      Path path = new Path ("src\\Input\\Cagampan390-Test.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
      Path path = new Path ("src\\Input\\Sample Potch.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
//      Path path = new Path ("src\\Input\\Article Annotation [Sir Potch].xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
//      Path path = new Path (null, "src\\Output\\", "src\\Input\\Stopwords.txt");
      
//      Preprocess p = new PreprocessTF_IDF(path, 1);
      Preprocess p = new PreprocessSO_CAL(path, 4);
      p.addArticle("Allah", "positive");
      p.addArticle("Boko haram", "Negative");
      p.addArticle("Life", "NeutraL");
      
      try {
         p.output(4);
//         for (int i = 60; i <= 90; i+=5) {
//            p.output(i);
//         }
      } catch (IOException ex) {
         System.err.println(ex.getMessage());
      }
   }
}
