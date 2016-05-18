package nlpfeatures;

public class Article {
   private final String[] words;
   private final Sentiment sentiment;

   public Article(String[] text, Sentiment sentiment) {
      this.words = text;
      this.sentiment = sentiment;
   }

   public String[] getWords() {
      return words;
   }

   public Sentiment getSentiment() {
      return sentiment;
   }
}
