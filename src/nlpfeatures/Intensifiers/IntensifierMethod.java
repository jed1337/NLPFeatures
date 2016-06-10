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
   
   public int doSomething(TaggedWords[] tws, Weight adjWeight){
      int total = 0;
      for (int i = 0; i < tws.length; i++) {
         TaggedWords tw = tws[i];
         if(tw.getTag()=='J'){   //Adjective
            total += override(tws, i, adjWeight);
         }
      }
      return total;
   }
   
   /**
    * Todo Think of a better name
    * Intensifies an adjective
    * @param tws
    * @param curIndex 
    */
   public abstract float override(TaggedWords[] tws, int curIndex, Weight adjWeight);
   
   //Add weight
   protected float addWeight(Intensifier intensifier, Weight adjWeight, TaggedWords tw) {
      return intensifier.getMultiplier() * adjWeight.getWordValue(tw.getWord());
   }
   
   protected class Intensifier {
      private final String intensifier;
      private final float multiplier;

      public Intensifier(String intensifier, float multiplier) {
         this.intensifier = intensifier.toLowerCase().trim();
         this.multiplier  = multiplier;
      }
      
      public String getIntensifier() {
         return intensifier;
      }

      public float getMultiplier() {
         return multiplier;
      }
   }
}
