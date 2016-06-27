package nlpfeatures.Intensifiers;

import nlpfeatures.TaggedWords;
import nlpfeatures.Weight;

public class PrefixIntensifier extends IntensifierMethod{
   @Override
   protected void addIntensifiers() {
      intensifiers.add(new Intensifier("napaka", IntensifierType.SUPERLATIVE));
      intensifiers.add(new Intensifier("pinaka", IntensifierType.SUPERLATIVE));
   }
   
   private boolean startsWith(TaggedWords tw, Intensifier in){
      return tw.getWord().toLowerCase().startsWith(in.getIntensifier().toLowerCase());
   }
   
   @Override
   public float getAdjectiveValue(TaggedWords[] tws, int curIndex, Weight adjWeight) {
      TaggedWords tw = tws[curIndex];
      
      float total = 0;
      for (Intensifier intensifier : intensifiers) {
         if (startsWith(tw, intensifier)) {
            System.out.println(tw);
            total += addWeight(intensifier, adjWeight, tw);
         }
      }
      return total;
   }
}
