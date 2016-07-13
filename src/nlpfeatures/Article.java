package nlpfeatures;

import java.util.function.Supplier;
import ngram.Ngram;

public class Article {
   private final String fullArticle;
   private final String[] ngrams;
   private final Sentiment sentiment;
   
   public Article(String fullArticle, String sentiment, Supplier<String[]> supplier, int ngCount){
      this.fullArticle = fullArticle;
      this.ngrams      = Ngram.getNgrams(supplier.get(), ngCount);
      this.sentiment   = Sentiment.getSentiment(sentiment);
   }

//   public Article(String[] text, Sentiment sentiment) {
//      this.ngrams = text;
//      this.sentiment = sentiment;
//   }

   public String getFullArticle() {
      return fullArticle;
   }

   public String[] getWords() {
      return ngrams;
   }

   public Sentiment getSentiment() {
      return sentiment;
   }
}
