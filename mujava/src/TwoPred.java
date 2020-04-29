public class TwoPred
{
   
   public static String twoPred (int x, int y)
   {  
      boolean z;
      
      if (x < y) {
         z = true;
      }
      else {
         z = false;
      }
   
      if (z && x+y == 10) {
         return "A";
      }
      return "B";
   }
}
