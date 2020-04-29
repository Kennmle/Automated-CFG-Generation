public class TriangleType
{
   public static Triangle triangle (int s1, int s2, int s3)
   {
      if (s1 <= 0 || s2 <= 0 || s3 <= 0) {
         return (Triangle.INVALID);
        }
      if (s1+s2 <= s3 || s2+s3 <= s1 || s1+s3 <= s2)
         {return (Triangle.INVALID);}

      if ((s1 == s2) && (s2 == s3))
         {return Triangle.EQUILATERAL;}

      if ((s1 == s2) || (s2 == s3) || (s1 == s3))
         {return Triangle.ISOSCELES;}

      return (Triangle.SCALENE);
   }
}
