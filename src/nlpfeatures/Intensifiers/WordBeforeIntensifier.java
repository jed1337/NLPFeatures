package nlpfeatures.Intensifiers;

import nlpfeatures.TaggedWords;
import nlpfeatures.Weight;

public class WordBeforeIntensifier extends IntensifierMethod{

   @Override
   protected void addIntensifiers() {
      intensifiers.add(new Intensifier("tunay na", 1.5f));
      intensifiers.add(new Intensifier("ubod ng", 2.5f));
   }

   @Override
   public float override(TaggedWords[] tws, int curIndex, Weight adjWeight) {
      float total = 0;
      for (Intensifier intPhrase : intensifiers) {
         String[] intPhraseWords = intPhrase.getIntensifier().split("\\s+");
         
         //Used to preven array out of bounds exceptions
         if(curIndex-intPhraseWords.length < 0){
            continue;
         }
         
         //checks if the words preceding tw[curIndex] are found in intPhrases
         final int ipwLen = intPhraseWords.length;
         boolean valid = true;
         for (int i = 0; i < ipwLen; i++) {
            String ipw = intPhraseWords[i];
            
            if(!tws[curIndex+(i-ipwLen)].getWord().equals(ipw)){
               valid = false;
            }
         }

         if(valid){
            total += addWeight(intPhrase, adjWeight, tws[curIndex]);
         }
      }
      return total;
   }
}
