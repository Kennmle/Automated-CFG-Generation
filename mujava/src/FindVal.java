import java.io.*;

public class FindVal
{
   public static int findVal(int numbers[], int val)       
   {                                                       
      int findVal = -1;                                    
                                                        
      for (int i=0; i<numbers.length; i++)  {          
         if (numbers [i] == val)
         {                      
            findVal = i;         
         }           
      }                                              
      return (findVal);    
   }
}                                                
