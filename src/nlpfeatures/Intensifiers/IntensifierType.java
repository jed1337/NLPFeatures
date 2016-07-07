package nlpfeatures.Intensifiers;

public enum IntensifierType {
   //Hard coded the negator use at the WordBeforeIsANegator class
   NONE(1), COMPARATIVE(1.5f), SUPERLATIVE(2.0f), NEGATOR(1);
   
   private final float multiplier;

   private IntensifierType(float multiplier) {
      this.multiplier = multiplier;
   }

   public float getMultiplier() {
      return multiplier;
   }
}
