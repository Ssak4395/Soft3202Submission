package au.edu.sydney.cpa.erp.feaa.reports;

import com.google.common.primitives.ImmutableDoubleArray;
import gnu.trove.map.hash.THashMap;

import java.util.HashMap;


/**
 * This is my flyweight class, the role of this class is keep references that will be reused, if needed.
 */
public class ReportRepository {

    /*I was going to make this a singleton class but decided against, it since, it will harder
    * for the next guy whose replacing me, when it comes to testing*/
   // private static ReportRepository reportRepository = null;
    /*
    * I also experimented with some custom "faster hashmaps" I used Trove collections library and a bit of apache to compare
    * which hashmap gave me the best time.
    * At the end of the day I found that out Apache and Trove, the java hashmap actually performed better*/


    private static HashMap<Integer,ImmutableDoubleArray> cache = new HashMap<>();



    /*
    This retrieves my array from the hashmap.
    * */

   public static ImmutableDoubleArray getArray(int Hash)
   {
       return cache.get(Hash);
   }


/*This is my caching strategy, I compare using hashcode, if an item is not found in the hashcode, I store it
* Next time, if the same item is passed, it is checked, and if found the hash is returned, this is then passed to the
* get array method.*/
    public static int getHash(ImmutableDoubleArray data)
    {
        if(cache.containsKey(data.hashCode()))
        {
            return data.hashCode();
        }else{

            cache.put(data.hashCode(),data);
             return data.hashCode();
        }
    }


    /*

     This was my previously hashing strategy, boy...this was very memory heavy, when combined with my Value Object,
     I kept on running out of Heap Space, and realised, that keeping a list of double[] was counter inuitivie when
     I can simply just do it in one go.


     public static double[]  checkCache(int hash,double[] array)
    {
        List<double[]> test = quickMap.get(hash);
        int Finder = 0;
        if(test == null)
        {
            test = new ArrayList<>();
            test.add(array);
            quickMap.put(hash,test);
            Finder = test.indexOf(array);
        }


        return quickMap.get(hash).get(Finder);

    }*/

}
