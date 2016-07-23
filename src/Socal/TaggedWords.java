package Socal;

public class TaggedWords {
   private final String word;
   private final char tag;

   public TaggedWords(String wordWithTag) {
      String[] temp = wordWithTag.split("/");
      
      if(temp.length==2){
         this.tag  = temp[1].charAt(0);
         this.word = temp[0];
      } else{
         this.word = "___";
         this.tag = '?';
      }
   }

   public String getWord() {
      return word;
   }

   public char getTag() {
      return tag;
   }

   @Override
   public String toString() {
      return String.format("%s: %c: ", word, tag);
   }
}
