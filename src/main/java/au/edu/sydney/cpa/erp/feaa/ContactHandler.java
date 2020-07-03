package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.*;
import au.edu.sydney.cpa.erp.ordering.Client;
import com.sun.net.httpserver.Filter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Dot Point Targeted: The current method of handling client contact methods is quite bulky – CPA  would like you to streamline this somehow.
 *
 *
 * The Chain of Responsibility Pattern was used over here, previously I thought it would of been better to utilize,
 * the strategy pattern, but this is not the case, if you take a look at the Feaafacade, it is very evident that we are following
 * a chain like heirarchy.
 *
 * Intent: Declutter and streamline this class
 *
 * Consequences: The contact methods are now all intertwined by implementors of chainable interface.
 *
 *
 */
public class ContactHandler {

    /**
     * Sets up the chain and gets ready to send.
     * @param token
     * @param client
     * @param priority
     * @param data
     * @return true or false it can be sent
     */

    public static boolean sendInvoice(AuthToken token, Client client, List<Chainable> priority, String data){

       /*I actually realized this pretty late in the assignment, lol, a CoR is just a linkedlist with a fancy name,
       * This implementation is much better*/

       for(int i = 0; i<priority.size(); ++i)
       {
           for(int j = i+1; j<priority.size(); ++j)
           {
               priority.get(i).setNextChain(priority.get(j));
               //I am setting the CoR such that it follows a linklist, I set the first chain and make the first chain connect to the i+1 element in the list
           }
       }
       return priority.get(0).canSend(client,token,data);
    }


    /**
     * Default methods I believe.
     * @return
     */
    public static List<String> getKnownMethods() {
        return Arrays.asList(
                "Carrier Pigeon",
                "Email",
                "Mail",
                "Internal Accounting",
                "Phone call",
                "SMS"
        );


    }

}

