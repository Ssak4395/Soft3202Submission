package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.database.TestDatabase;
import au.edu.sydney.cpa.erp.ordering.Client;

/**
 *
 * Dot point Targeted:
 *
 * Any time Clients are loaded from the database, the system lags for a long time.
 * The database issues themselves have been deemed too expensive to fix,
 * but perhaps you can partially mitigate this with the software somehow?
 *
 *
 * This class focuses on the Client loading problem, this was fixed
 * using the lazy load(Lazy Initialization)
 *
 * Intent: Reduce database times, by intializing a variable once, and then calling the variable by its reference
 *
 * Consequences: Lag has been eliminated.
 *
 */
public class ClientImpl implements Client {

    private final int id;
    private String fName;
    private String lName;
    private String phoneNumber;
    private String emailAddress;
    private String address;
    private String suburb;
    private String state;
    private String postCode;
    private String internalAccounting;
    private String businessName;
    private String pigeonCoopID;
    private AuthToken authToken;

    /**
     *
     * @param token Authorization Token
     * @param id Client ID
     */
    public ClientImpl(AuthToken token, int id) {

        this.id = id;
        this.authToken = token;
    }

    /**
     *
     * @return The Client ID that has been set
     */
    public int getId() {
        return id;
    }

    /**
     * This uses lazy loading, first if the client is being initialized for the first time
     * It will retrieve its data from the database,set it as a reference
     * the next time we access a variable, we will simply return the reference rather than searching for it
     * in the database.
     * @return First Name
     */
    @Override
    public String getFName() {
        if(fName == null)
        {
            fName =  TestDatabase.getInstance().getClientField(authToken, id, "fName");
        }
        return fName;
    }
    /**
     * This uses lazy loading, first if the client is being initialized for the first time
     * It will retrieve its data from the database,set it as a reference
     * the next time we access a variable, we will simply return the reference rather than searching for it
     * in the database.
     * @return Last Name Passed
     */
    @Override
    public String getLName() {
        if(lName == null)
        {
            lName =  TestDatabase.getInstance().getClientField(authToken, id, "lName");
        }
        return lName;
    }

    /**
     * This uses lazy loading, first if the client is being initialized for the first time
     * It will retrieve its data from the database,set it as a reference
     * the next time we access a variable, we will simply return the reference rather than searching for it
     * in the database.
     * @return Phone Number Passed
     */
    @Override
    public String getPhoneNumber() {
        if(phoneNumber == null)
        {
            phoneNumber =  TestDatabase.getInstance().getClientField(authToken, id, "phoneNumber");
        }
        return phoneNumber;
    }

    /**
     * This uses lazy loading, first if the client is being initialized for the first time
     * It will retrieve its data from the database,set it as a reference
     * the next time we access a variable, we will simply return the reference rather than searching for it
     * in the database.
     * @return Email Address Passed
     */
    @Override
    public String getEmailAddress() {
        if(emailAddress == null)
        {
            emailAddress =  TestDatabase.getInstance().getClientField(authToken, id, "emailAddress");
        }
        return emailAddress;
    }
    /**
     * This uses lazy loading, first if the client is being initialized for the first time
     * It will retrieve its data from the database,set it as a reference
     * the next time we access a variable, we will simply return the reference rather than searching for it
     * in the database.
     * @return Address Passed
     */
    @Override
    public String getAddress() {
        if(address == null)
        {
            address =  TestDatabase.getInstance().getClientField(authToken, id, "address");
        }
        return address;
    }

    /**
     * This uses lazy loading, first if the client is being initialized for the first time
     * It will retrieve its data from the database,set it as a reference
     * the next time we access a variable, we will simply return the reference rather than searching for it
     * in the database.
     * @return Suburb passed
     */
    @Override
    public String getSuburb() {
        if(suburb == null)
        {
            suburb =  TestDatabase.getInstance().getClientField(authToken, id, "suburb");
        }
        return suburb;
    }

    /**
     * This uses lazy loading, first if the client is being initialized for the first time
     * It will retrieve its data from the database,set it as a reference
     * the next time we access a variable, we will simply return the reference rather than searching for it
     * in the database.
     * @return The state passed
     */
    @Override
    public String getState() {
        if(state == null)
        {
            state =  TestDatabase.getInstance().getClientField(authToken, id, "state");
        }
        return state;
    }

    /**
     * This uses lazy loading, first if the client is being initialized for the first time
     * It will retrieve its data from the database,set it as a reference
     * the next time we access a variable, we will simply return the reference rather than searching for it
     * in the database.
     * @return The Post Code passed
     */
    @Override
    public String getPostCode() {
        if(postCode == null)
        {
            postCode =  TestDatabase.getInstance().getClientField(authToken, id, "postCode");
        }
        return postCode;
    }

    /**
     * This uses lazy loading, first if the client is being initialized for the first time
     * It will retrieve its data from the database,set it as a reference
     * the next time we access a variable, we will simply return the reference rather than searching for it
     * in the database.
     * @return Internal Accounting passed
     */
    @Override
    public String getInternalAccounting() {
        if(internalAccounting == null)
        {
            internalAccounting =  TestDatabase.getInstance().getClientField(authToken, id, "internal accounting");
        }
        return internalAccounting;
    }

    /**
     * This uses lazy loading, first if the client is being initialized for the first time
     * It will retrieve its data from the database,set it as a reference
     * the next time we access a variable, we will simply return the reference rather than searching for it
     * in the database.
     * @return Business Name passed
     */
    @Override
    public String getBusinessName() {
        if(businessName == null)
        {
            businessName =  TestDatabase.getInstance().getClientField(authToken, id, "businessName");
        }
        return businessName;
    }

    /**
     * This uses lazy loading, first if the client is being initialized for the first time
     * It will retrieve its data from the database,set it as a reference
     * the next time we access a variable, we will simply return the reference rather than searching for it
     * in the database.
     * @return Pigeon Coop ID passed
     */
    @Override
    public String getPigeonCoopID() {
        if(pigeonCoopID == null)
        {
            pigeonCoopID =  TestDatabase.getInstance().getClientField(authToken, id, "pigeonCoopID");
        }
        return pigeonCoopID;
    }
}

