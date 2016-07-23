package Socal.Intensifiers;

import java.util.ArrayList;
import java.util.regex.Pattern;
import Socal.TaggedWords;
import Socal.Weight;

public abstract class IntensifierMethod {
   protected ArrayList<Intensifier> intPhrases;
   protected Pattern validTags;
   
   public IntensifierMethod(){
      intPhrases = new ArrayList<>();
      addIntensifiers();
      setValidTags();
   }

   protected abstract void setValidTags();
   protected abstract void addIntensifiers();
      
   /**
    * Intensifies an adjective
    * @param tws
    * @param curIndex 
    * @param weights 
    * @return  
    */
   public abstract float getIntensifierVal(TaggedWords[] tws, int curIndex, ArrayList<Weight> weights);
   
   public int addIntensification(TaggedWords[] tws, ArrayList<Weight> weights){
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
    * @param intensifier
    * @param weights
    * @param tw
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
    * @return true if curIndex-intPhraseWords.length < 0
    */
   protected boolean validBounds(int curIndex, String... intPhraseWords) {
      return curIndex-intPhraseWords.length > 0;
   }
//</editor-fold>
   
//<editor-fold defaultstate="collapsed" desc="Intensifier class">
   protected class Intensifier {
      private final String intensifier;
      private final IntensifierType intType;
      
      public Intensifier(String intensifier, IntensifierType intType) {
         this.intensifier = intensifier.toLowerCase().trim();
         this.intType     = intType;
      }
      
      public String getIntensifier() {
         return intensifier;
      }
      
      public float getMultiplier() {
         return intType.getMultiplier();
      }
   }
//</editor-fold>
}
