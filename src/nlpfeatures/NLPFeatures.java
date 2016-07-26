package nlpfeatures;

import Socal.PreprocessSO_CAL;
import Tfidf.PreprocessTF_IDF;
import java.io.IOException;

public class NLPFeatures {

   /**
    * The main class
    * @param args
    * @throws IOException 
    */
   public static void main(String[] args) throws IOException {
//      Path path = new Path ("src\\Input\\Cagampan390-Test.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
//      Path path = new Path ("src\\Input\\Article Annotation [Sir Potch].xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
//      Path path = new Path ("src\\Input\\Sample Potch.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
      Path path = new Path (null, "src\\Output\\", "src\\Input\\Stopwords.txt");
      
      PreprocessSO_CAL p = new PreprocessSO_CAL(path);
   }
}
