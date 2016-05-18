package nlpfeatures;

public class Article {
   private final String text;
   private final Sentiment sentiment;

   public Article(String text, Sentiment sentiment) {
      this.text = text;
      this.sentiment = sentiment;
   }

   public String getText() {
      return text;
   }

   public Sentiment getSentiment() {
      return sentiment;
   }
}
