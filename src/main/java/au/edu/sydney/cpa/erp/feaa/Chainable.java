package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.ordering.Client;

public interface Chainable {
     boolean  canSend(Client client, AuthToken token,String data);
     void  setNextChain(Chainable chain);
     String getTag();
}
