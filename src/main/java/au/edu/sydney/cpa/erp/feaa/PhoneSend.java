package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.PhoneCall;
import au.edu.sydney.cpa.erp.ordering.Client;

public class PhoneSend implements Chainable {
   Chainable chainable;
    public String tag = "PhoneCall";



    /**
     *
     * @return a String tag, for reference, to see which stage of the heiarchy we are currently
     * in.
     */
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
     */    @Override
    public boolean canSend(Client client, AuthToken token, String data) {
        if (client.getPhoneNumber() != null) {
            PhoneCall.sendInvoice(token, client.getFName(), client.getLName(), data,client.getPhoneNumber());
            return true;
        }


        else if(chainable != null){
            return chainable.canSend(client,token,data);
        }

        return false;

    }
    /**
     *
     * @param chain Sets the next chain in the hierarchy
     */
    @Override
    public void setNextChain(Chainable chain) {
        this.chainable = chain;
    }
}
