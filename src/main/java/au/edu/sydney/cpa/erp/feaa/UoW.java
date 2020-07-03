package au.edu.sydney.cpa.erp.feaa;
import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.database.TestDatabase;
import au.edu.sydney.cpa.erp.ordering.Order;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UoW implements IUoW<Order> {
     Map<Integer,Order> memory; //One map for unedited objects
     Map<Integer,Order> dirtyMemory; //Another for edited objects.



   public UoW()
   {
       memory = new HashMap<>();
       dirtyMemory = new HashMap<>();
   }


    /**
     *
     * @return Returns the map of the unedited objects
     */
    @Override
    public Map getMap() {
        return this.memory;
    }


    /**
     *
     * @param order Registers a new clean order object.
     */
    @Override
    public void RegisterClean(Order order) {

       if(order!= null)
        {
            memory.put(order.getOrderID(),order);
        }
   }

    /**
     *
     * @param order Checks
     */
   @Override
    public void RegisterDirty(Order order) {
       if(memory.containsKey(order.getOrderID()))
       {
           dirtyMemory.put(order.getOrderID(),memory.get(order.getOrderID()));
       }
       else
           dirtyMemory.put(order.getOrderID(),order);
    }

    /**
     * Retrieves an order that is stored in the MAP
     * @param id
     * @return
     */
    @Override
    public Order getTemporary(int id) {

        return memory.get(id);
    }

    /**
     * Commits everything to the database when user logs out.
     * @param authToken
     */
    @Override
    public void commit(AuthToken authToken){



          TestDatabase tdb = TestDatabase.getInstance();
          memory.forEach((k, v) -> tdb.saveOrder(authToken, v));
          dirtyMemory.forEach((k, v) -> tdb.saveOrder(authToken, v));
          memory.clear();
          dirtyMemory.clear();


    }

}
