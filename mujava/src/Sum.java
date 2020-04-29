public class Sum
{
   public static int sum(int[] x)
   {
      int s = 0;
      for (int i=0; i < x.length; i++)
      {
         s = s + x[i];
      }
      return s;
   }
}
