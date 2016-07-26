package Socal.Intensifiers;

import java.util.ArrayList;
import java.util.regex.Pattern;
import Socal.TaggedWords;
import Socal.Weight;

/**
 * Used to intensify a word. It is an abstract class since 
 * there are different kinds of intensifiers
 * @author Jed Caychingco
 */
public abstract class IntensifierMethod {
   /**
    * The phrases to be intensified
    */
   protected ArrayList<Intensifier> intPhrases;
   
   /**
    * The parts of speech tags that intensification can apply to
    */
   protected Pattern validTags;
   
   /**
    * Creates an instance of this class. Invoked by its subclasses
    */
   protected IntensifierMethod(){
      intPhrases = new ArrayList<>();
      addIntPhrases();
      setValidTags();
   }
   
   /**
    * Adds the intensifier phrases to be used
    */
   protected abstract void addIntPhrases();

   /**
    * Sets the valid tags to be used.
    */
   protected abstract void setValidTags();
      
   /**
    * Intensifies a word
    * @param tws The list of tagged words
    * @param curIndex The index of the word to be intensified
    * @param weights The weights to be used in intensification
    * @return The value intensified form it
    */
   public abstract float getIntensifierVal(TaggedWords[] tws, int curIndex, ArrayList<Weight> weights);
   
   /**
    * @param tws A list of tagged words to be intensified
    * @param weights The list of weights used to base the intensification
    * @return The total intensifications done on the tagged words
    */
   public float addIntensification(TaggedWords[] tws, ArrayList<Weight> weights){
      int total = 0;
      for (int i = 0; i < tws.length; i++) {
         TaggedWords tw = tws[i];
         
         if(validTags.matcher(String.valueOf(tw.getTag())).matches()){
            total += getIntensifierVal(tws, i, weights);
         }
      }
      return total;
   }
   
//<editor-fold defaultstate="collapsed" desc="Sub class utility tools">
   
   /**
    * It looks for the weight of the tagged word withe respect to its tag.
    * It is then multiplied by the value of intensifier
    * @param intensifier Used to get the multiplier for the word
    * @param weights Used to get the weight of the tagged word
    * @param tw The tagged word
    * @return The weight of the intensification
    */
   protected float addWeight(Intensifier intensifier, ArrayList<Weight> weights, TaggedWords tw) {
      for (Weight weight : weights) {
         if(weight.getTag()==tw.getTag()){
            return intensifier.getMultiplier() * weight.getWordValue(tw.getWord());
         }
      }
      return 0.0f;
   }
   
   /**
    * Used to prevent an array out of bounds exception
    * @param curIndex index of the article to be checked
    * @param intPhraseWords the words of the intensifier phrase
    * @return true if curIndex-intPhraseWords.length is less than 0
    */
   protected boolean validBounds(int curIndex, String... intPhraseWords) {
      return curIndex-intPhraseWords.length > 0;
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Intensifier class">
   /**
    * The Intensifier class.
    */
   protected class Intensifier {
      /**
       * The intensifier
       */
      private final String intensifier;
      
      /**
       * The intensifier's type
       */
      private final IntensifierType intType;
      
      /**
       * Cretes an instance of this class
       * @param intensifier The intensifier
       * @param intType The intensifier's type
       */
      public Intensifier(String intensifier, IntensifierType intType) {
         this.intensifier = intensifier.toLowerCase().trim();
         this.intType     = intType;
      }
      
      /**
       * Returns the intesifier
       * @return
       */
      public String getIntensifier() {
         return intensifier;
      }
      
      /**
       * Returns the multiplier of the intensifier
       * @return 
       */
      public float getMultiplier() {
         return intType.getMultiplier();
      }
   }
//</editor-fold>
}
