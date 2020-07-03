package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.SMS;
import au.edu.sydney.cpa.erp.ordering.Client;

public class SMSSend implements Chainable {

    Chainable chainable;
    public String tag = "SMS";


    @Override
    public String getTag() {
        return tag;
    }

    /**
     *
     * @param client
     * @param token
     * @param data
     * @return True or false, based on whether a chain exists
     * This follows a linked list hierarchy that is preset by the user when choosing a custom list
     *
     */
    @Override
    public boolean canSend(Client client, AuthToken token, String data) {
        String smsPhone = client.getPhoneNumber();
        if (null != smsPhone) {
            SMS.sendInvoice(token, client.getFName(), client.getLName(), data, smsPhone);
            return true;
        }
       else if (chainable == null){ return false;}
        return false;
    }
    /**
     *
     * @param chain Sets the next chain in the hierarchy
     */
    @Override
    public void setNextChain(Chainable chain) {

    }
}
