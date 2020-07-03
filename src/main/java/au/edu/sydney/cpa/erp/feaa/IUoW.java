package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;

import java.util.Map;

public interface IUoW<T> {

    void  RegisterClean(Order order);
    void RegisterDirty(Order order);
    void commit(AuthToken authToken);
    Order getTemporary(int id);
    Map getMap();

}
