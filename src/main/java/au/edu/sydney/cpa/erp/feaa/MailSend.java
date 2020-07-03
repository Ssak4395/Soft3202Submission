package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.Mail;
import au.edu.sydney.cpa.erp.ordering.Client;

public class MailSend implements  Chainable {

    //  Chainable next = new PhoneSend(); So..before I decided it would of been a smart idea to hard code, I thought the order was static.
    /* But this is loads better*/

    public String tag = "Mail";

    /**
     *
     * @return a String tag, for reference, to see which stage of the heiarchy we are currently
     * in.
     */
    @Override
    public String getTag() {
        return tag;
    }
    Chainable chainable;

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
        if (null != client.getAddress() && null != client.getSuburb() &&
                null != client.getState() && null != client.getPostCode())
        {
            Mail.sendInvoice(token, client.getFName(), client.getLName(), data, client.getAddress(), client.getSuburb(), client.getState(), client.getPostCode());
            return true;
        } else if(chainable!= null)
        {
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
