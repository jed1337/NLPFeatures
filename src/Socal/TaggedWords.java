package Socal;

/**
 * The tagged words
 * @author Jed Caychingco
 */
public class TaggedWords {
   /**
    * The WORD
    */
   private final String WORD;
   
   /**
    * The TAG of its part of speech
    */
   private final char TAG;

   /**
    * Parses the String input that's in the following format:
    * <i>WORD/TAG</i>
    * @param wordWithTag 
    */
   public TaggedWords(String wordWithTag) {
      String[] temp = wordWithTag.split("/");
      
      if(temp.length==2){
         this.WORD = temp[0];
         this.TAG  = temp[1].charAt(0);
      } else{
         this.WORD = "___";
         this.TAG = '?';
      }
   }

   /**
    * Returns the WORD
    * @return 
    */
   public String getWord() {
      return WORD;
   }

   /**
    * Returns the TAG
    * @return 
    */
   public char getTag() {
      return TAG;
   }

   
   /**
    * @return {@code WORD: TAG} 
    */
   @Override
   public String toString() {
      return String.format("%s: %c: ", WORD, TAG);
   }
}
