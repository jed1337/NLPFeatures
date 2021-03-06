package Socal.Intensifiers;

import java.util.ArrayList;
import java.util.regex.Pattern;
import Socal.TaggedWords;
import Socal.Weight;

/**
 * Applicable to adjectives whose intensifiers are the other words before it
 * @author Jed Caychingco
 */
public class WordBeforeIsIntensifier extends IntensifierMethod{
   
   /**
    * {@inheritDoc }
    */
   @Override
   protected void setValidTags() {
      validTags = Pattern.compile("J");
   }

   /**
    * {@inheritDoc } <br>
    * Contains Filipino phrases
    */
   @Override
   protected void addIntPhrases() {
      intPhrases.add(new Intensifier("ubod ng", IntensifierType.SUPERLATIVE));
      intPhrases.add(new Intensifier("sukdulang", IntensifierType.SUPERLATIVE));
      intPhrases.add(new Intensifier("sukdulan ng", IntensifierType.SUPERLATIVE));
      
      intPhrases.add(new Intensifier("mas", IntensifierType.COMPARATIVE));
      intPhrases.add(new Intensifier("tunay na", IntensifierType.COMPARATIVE));
      intPhrases.add(new Intensifier("masyadong", IntensifierType.COMPARATIVE));
      intPhrases.add(new Intensifier("masyado na", IntensifierType.COMPARATIVE));
      intPhrases.add(new Intensifier("totoong", IntensifierType.COMPARATIVE));
      intPhrases.add(new Intensifier("totoo na", IntensifierType.COMPARATIVE));
   }

   /**
    * {@inheritDoc }
    * @return 
    */
   @Override
   public float getIntensifierVal(TaggedWords[] tws, int curIndex, ArrayList<Weight> weights) {
      float total = 0;
      for (Intensifier intPhrase : intPhrases) {
         String[] intPhraseWords = intPhrase.getIntensifier().split("\\s+");
         
         if(!validBounds(curIndex, intPhraseWords)){
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
            total += addWeight(intPhrase, weights, tws[curIndex]);
         }
      }
      return total;
   }
}
