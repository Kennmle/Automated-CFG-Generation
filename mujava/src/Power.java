public class Power
{
   public static int power (int left, int right)
   {
      int rslt;
      rslt = left;
      if (right == 0)
      {
         rslt = 1;
      }
      else
      {
         for (int i = 2; i <= right; i++) {
            rslt = rslt * left;
         }
      }
      return (rslt);
   }
}
