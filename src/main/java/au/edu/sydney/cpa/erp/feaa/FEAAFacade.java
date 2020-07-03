package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthModule;
import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.Mail;
import au.edu.sydney.cpa.erp.contact.SMS;
import au.edu.sydney.cpa.erp.database.TestDatabase;
import au.edu.sydney.cpa.erp.ordering.Client;
import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;
import au.edu.sydney.cpa.erp.feaa.ordering.*;
import au.edu.sydney.cpa.erp.feaa.reports.ReportDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("Duplicates")

/**
 * Dot Point targeted(6 and 7)
 *The Order creation process involves a lot of slow database operations.
 * CPA would like you to simplify this process (especially the database lag
 * while the employee is entering data) without breaking the Order interface. (Fully Completed)
 *
 * The current system is mostly single-threaded. There has been some work on the database side to allow multithreading, but as yet the FEAA module does not have any threading besides the main one.
 * CPA would like you to use multithreading to allow employees to use the system for other things while slow database processes happen in the background.
 *
 */

public class FEAAFacade {
    private AuthToken token;
    private UoW uoW = new UoW();

    /**
     * Logs the user into the system, returns true or false if they can be looged
     * @param userName
     * @param password
     * @return true or false, if able to be logged in
     */
    public boolean login(String userName, String password) {

        token = AuthModule.login(userName, password);
        return null != token;
    }

    /**
     *
     * @return retrieves a list of all the orders
     */
    public List<Integer> getAllOrders() {
        if (null == token) {
            throw new SecurityException();
        }

        TestDatabase database = TestDatabase.getInstance();

        List<Order> orders = database.getOrders(token);

        List<Integer> result = new ArrayList<>();

        for (Order order : orders) {
            result.add(order.getOrderID());
        }

        return result;
    }

    /**
     * Creates and returns the ID of the order
     * @param clientID
     * @param date
     * @param isCritical
     * @param isScheduled
     * @param orderType
     * @param criticalLoadingRaw
     * @param maxCountedEmployees
     * @param numQuarters
     * @return the ID of the created order
     */
    public Integer createOrder(int clientID, LocalDateTime date, boolean isCritical, boolean isScheduled, int orderType, int criticalLoadingRaw, int maxCountedEmployees, int numQuarters) {
        if (null == token) {
            throw new SecurityException();
        }

        double criticalLoading = criticalLoadingRaw / 100.0;

        Order order;

        if (!TestDatabase.getInstance().getClientIDs(token).contains(clientID)) {
            throw new IllegalArgumentException("Invalid client ID");
        }

        int id = TestDatabase.getInstance().getNextOrderID();

        if (isScheduled) {
            if (1 == orderType) { // 1 is regular accounting
                if (isCritical) {
                    order = new FirstOrderTypeScheduled(id, clientID, date, criticalLoading, maxCountedEmployees, numQuarters);
                } else {
                    order = new Order66Scheduled(id, clientID, date, maxCountedEmployees, numQuarters);
                }
            } else if (2 == orderType) { // 2 is audit
                    if (isCritical) {
                        order = new CriticalAuditOrderScheduled(id, clientID, date, criticalLoading, numQuarters);
                    } else {
                        order = new NewOrderImplScheduled(id, clientID, date, numQuarters);
                    }
            } else {return null;}
        } else {
            if (1 == orderType) {
                if (isCritical) {
                    order = new FirstOrderType(id, clientID, date, criticalLoading, maxCountedEmployees);
                } else {
                    order = new Order66(id, clientID, date, maxCountedEmployees);
                }
            } else if (2 == orderType) {
                if (isCritical) {
                    order = new CriticalAuditOrder(id, clientID, date, criticalLoading);
                } else {
                    order = new NewOrderImpl(id, clientID, date);
                }
            } else {return null;}
        }
        uoW.RegisterClean(order); //registers the created order to the clean Map
        //TestDatabase.getInstance().saveOrder(token, order);

        return order.getOrderID();
    }

    /**
     *
     * @return a list of all the client ids
     */
    public List<Integer> getAllClientIDs() {
        if (null == token) {
            throw new SecurityException();
        }

        TestDatabase database = TestDatabase.getInstance();
        return database.getClientIDs(token);
    }

    /**
     * Returns the client with the given id
     * @param id
     * @return Client with given id
     */
    public Client getClient(int id) {
        if (null == token) {
            throw new SecurityException();
        }

        return new ClientImpl(token, id);
    }

    public boolean removeOrder(int id) {
        if (null == token) {
            throw new SecurityException();
        }

        TestDatabase database = TestDatabase.getInstance();
        return database.removeOrder(token, id);
    }

    public List<Report> getAllReports() {
        if (null == token) {
            throw new SecurityException();
        }

        return new ArrayList<>(ReportDatabase.getTestReports());
    }

    /**
     * Finalizes and prepares the order to be sent
     * @param orderID
     * @param contactPriority
     * @return finalizes and sends the order
     */

    public boolean finaliseOrder(int orderID, List<String> contactPriority) {
        if (null == token) {
            throw new SecurityException();
        }

        List<Chainable> contactPriorityAsMethods = new ArrayList<>();

        if (null != contactPriority) {
            for (String method: contactPriority) {
                switch (method.toLowerCase()) {
                    case "internal accounting":
                        contactPriorityAsMethods.add(new IASend());
                        break;
                    case "email":
                        contactPriorityAsMethods.add(new EmailSend());
                        break;
                    case "carrier pigeon":
                        contactPriorityAsMethods.add(new CarrierSend());
                        break;
                    case "mail":
                        contactPriorityAsMethods.add(new MailSend());
                        break;
                    case "phone call":
                        contactPriorityAsMethods.add(new PhoneSend());
                        break;
                    case "sms":
                        contactPriorityAsMethods.add(new SMSSend());
                        break;
                    default:
                        break;
                }
            }
        }

        if (contactPriorityAsMethods.size() == 0) { // needs setting to default
            contactPriorityAsMethods = Arrays.asList(
                    new IASend(),
                    new EmailSend(),
                    new CarrierSend(),
                    new MailSend(),
                   new PhoneSend()
            );
        }


        Order order = TestDatabase.getInstance().getOrder(token, orderID);

        if(order == null)
        {
            order = uoW.getTemporary(orderID);
        }
        order.finalise();

        return ContactHandler.sendInvoice(token, getClient(order.getClient()), contactPriorityAsMethods, order.generateInvoiceData());
    }

    /**
     * Logs the user out of the system
     */
    public void logout() {
     /* new Thread(()-> uoW.commit(token)).start();
       try{
           Thread.sleep(10);

       }catch (InterruptedException e)
       {

       }
       To save much faster and do other task
       So this basically kinda...works, however when i logout and log back in I get a security error, this only happens if i create two or orders
       I have a feeling the problem is due to security auth being set to null.
       UNCOMMENT ME AND REMOVE uoW.commit(token) to speed me up.
        */
       uoW.commit(token);    // Commits all the changes in one go, this simply means lag is deferred to the log out stage of the program.
       AuthModule.logout(token);
        token = null;

    }

    /**
     * Returns the total commission of the order
     * @param orderID
     * @return
     */
    public double getOrderTotalCommission(int orderID) {
        if (null == token) {
            throw new SecurityException();
        }

        Order order = TestDatabase.getInstance().getOrder(token, orderID);
        if (null == order) {
            order = uoW.getTemporary(orderID);
        }

        return order.getTotalCommission();
    }

    /**
     * Allows the user to edit the order once created
     * @param orderID
     * @param report
     * @param numEmployees
     */
    public void orderLineSet(int orderID, Report report, int numEmployees) {
        if (null == token) {
            throw new SecurityException();
        }

        Order order = TestDatabase.getInstance().getOrder(token, orderID);


        if (null == order)
            {
                order = uoW.getTemporary(orderID);
            }

        order.setReport(report, numEmployees);
        uoW.RegisterClean(order);
        //TestDatabase.getInstance().saveOrder(token, order);
    }

    public String getOrderLongDesc(int orderID) {
        if (null == token) {
            throw new SecurityException();
        }

        Order order = TestDatabase.getInstance().getOrder(token, orderID);

        if (null == order) {
            order = uoW.getTemporary(orderID);
        }

        return order.longDesc();
    }

    public String getOrderShortDesc(int orderID) {
        if (null == token) {
            throw new SecurityException();
        }

        Order order = TestDatabase.getInstance().getOrder(token, orderID);


        if (null == order) {
            order = uoW.getTemporary(orderID);
        }

        return order.shortDesc();
    }

    public List<String> getKnownContactMethods() {
        if (null == token) {
            throw new SecurityException();
        }
        return ContactHandler.getKnownMethods();
    }
}
