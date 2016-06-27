package nlpfeatures.Intensifiers;

import java.util.ArrayList;
import nlpfeatures.TaggedWords;
import nlpfeatures.Weight;

public abstract class IntensifierMethod {
   protected ArrayList<Intensifier> intensifiers;
   
   public IntensifierMethod(){
      intensifiers = new ArrayList<>();
      addIntensifiers();
   }

   protected abstract void addIntensifiers();
   
   public int ifAdjective(TaggedWords[] tws, Weight adjWeight){
      int total = 0;
      for (int i = 0; i < tws.length; i++) {
         TaggedWords tw = tws[i];
         if(tw.getTag()=='J'){   //Adjective
            total += getAdjectiveValue(tws, i, adjWeight);
         }
      }
      return total;
   }
   
   /**
    * Todo Think of a better name
    * Intensifies an adjective
    * @param tws
    * @param curIndex 
    * @param adjWeight 
    * @return  
    */
   public abstract float getAdjectiveValue(TaggedWords[] tws, int curIndex, Weight adjWeight);
   
   //Add weight
   protected float addWeight(Intensifier intensifier, Weight adjWeight, TaggedWords tw) {
      return intensifier.getMultiplier() * adjWeight.getWordValue(tw.getWord());
   }
   
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
