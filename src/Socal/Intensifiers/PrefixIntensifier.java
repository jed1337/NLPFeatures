package Socal.Intensifiers;

import java.util.ArrayList;
import java.util.regex.Pattern;
import Socal.TaggedWords;
import Socal.Weight;

public class PrefixIntensifier extends IntensifierMethod{

   @Override
   protected void setValidTags() {
      validTags = Pattern.compile("J");
   }
   
   @Override
   protected void addIntensifiers() {
      intPhrases.add(new Intensifier("napaka", IntensifierType.SUPERLATIVE));
      intPhrases.add(new Intensifier("pinaka", IntensifierType.SUPERLATIVE));
   }
   
   @Override
   public float getIntensifierVal(TaggedWords[] tws, int curIndex, ArrayList<Weight> weights) {
      TaggedWords tw = tws[curIndex];
      
      float total = 0;
      for (Intensifier intensifier : intPhrases) {
         if (startsWith(tw, intensifier)) {
            System.out.println(tw);
            total += addWeight(intensifier, weights, tw);
         }
      }
      return total;
   }
      
   private boolean startsWith(TaggedWords tw, Intensifier in){
      return tw.getWord().toLowerCase().startsWith(in.getIntensifier().toLowerCase());
   }
}
