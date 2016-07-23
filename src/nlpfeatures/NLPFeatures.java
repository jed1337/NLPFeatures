package nlpfeatures;

import Socal.PreprocessSO_CAL;
import java.io.IOException;

public class NLPFeatures {

   public static void main(String[] args) {
//      Path path = new Path ("src\\Input\\Cagampan390-Test.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
      Path path = new Path ("src\\Input\\Sample Potch.xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
//      Path path = new Path ("src\\Input\\Article Annotation [Sir Potch].xlsx", "src\\Output\\", "src\\Input\\Stopwords.txt");
      
//      Preprocess p = new PreprocessTF_IDF(path, 1);
      PreprocessSO_CAL p = new PreprocessSO_CAL(path);
      
//      p.addArticle("Nice", "positive");
//      p.addArticle("Boko haram", "NegatiVe");
//      p.addArticle("Life", Sentiment.NEUTRAL);
//      p.addArticle("Wala", "None");
//      
//      try {
//         p.output(4);
//      } catch (IOException ex) {
//         System.err.println(ex.getMessage());
//      }
      
      int aWeight = p.getArticleWeight("Insert article here");
      System.out.println(p.getSentimentFromWeight(aWeight));
   }
}
