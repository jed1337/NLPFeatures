package Socal.Intensifiers;

/**
 * An enum class containing the different intensifier types
 * @author Jed Caychingco
 */
public enum IntensifierType {
   //Hard coded the negator use at the WordBeforeIsANegator class
   NONE(1), COMPARATIVE(1.25f), SUPERLATIVE(1.5f), NEGATOR(1);
   
   /** The multiplier of the intensifier type */
   private final float multiplier;

   /**
    * Creates an instance of this class with the specified multiplier
    * @param multiplier 
    */
   private IntensifierType(float multiplier) {
      this.multiplier = multiplier;
   }

   /**
    * Returns the intensifier type's multiplier
    * @return 
    */
   public float getMultiplier() {
      return multiplier;
   }
   
   @Override
   public String toString(){
      return String.format("%s = %.2f", this.name(), getMultiplier());
   }
}
