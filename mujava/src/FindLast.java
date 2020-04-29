import java.io.*;

public class FindLast
{
   public static int findLast (int[] x, int y)
   { 
      for (int i=x.length-1; i >= 0; i--)
      {
         if (x[i] == y) 
         {
            return i;
         }
      }
      return -1;
   }
}
