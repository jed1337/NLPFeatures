package nlpfeatures.Intensifiers;

import java.util.ArrayList;
import java.util.regex.Pattern;
import nlpfeatures.TaggedWords;
import nlpfeatures.Weight;

public class WordBeforeIsANegator extends IntensifierMethod{
   @Override
   protected void setValidTags() {
      validTags = Pattern.compile(".");
   }

   @Override
   protected void addIntensifiers() {
      intPhrases.add(new Intensifier("hindi", IntensifierType.NEGATOR));
      intPhrases.add(new Intensifier("di", IntensifierType.NEGATOR));
      intPhrases.add(new Intensifier("huwag", IntensifierType.NEGATOR));
      intPhrases.add(new Intensifier("wala", IntensifierType.NEGATOR));
      intPhrases.add(new Intensifier("walang", IntensifierType.NEGATOR));
      intPhrases.add(new Intensifier("ayaw", IntensifierType.NEGATOR));
      intPhrases.add(new Intensifier("aywan", IntensifierType.NEGATOR));
      intPhrases.add(new Intensifier("dili", IntensifierType.NEGATOR));
   }

      
      /*
       * If negator is found, check the weight of the following word then subtract or add 4 to it.
       */
   @Override
   public float getIntensifierVal(TaggedWords[] tws, int curIndex, ArrayList<Weight> weights) {
      float total = 0;
      for (Intensifier intPhrase : intPhrases) {
         String intPhraseWord = intPhrase.getIntensifier();
         
         if(!validBounds(curIndex, intPhraseWord)){
            continue;
         }

         //curIndex-1 is used since all the intPhrases here are only
         //made up of one word each
         if(tws[curIndex-1].getWord().equals(intPhraseWord)){
            float awValue = addWeight(intPhrase, weights, tws[curIndex]);
            //Hardcoded
            //Since negators -4 for positive, and +4 for positive
            //IntensifierType.NEGATOR has a multiplier value of 1
            if(awValue>0){
               total-=4;   //tws[...] is positive, therefore negate it by 
            }              //adding -4 to total
            else if (awValue<0){
               total+=4;   //tws[...] is negative here, negat it by
            }              //adding 4 instead to the total
            //If awValue == 0, total is not changed since it means tws[...] has a weight of 0
         }
      }
      return total;
   }
}
