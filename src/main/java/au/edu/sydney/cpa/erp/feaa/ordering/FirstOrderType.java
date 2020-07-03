package au.edu.sydney.cpa.erp.feaa.ordering;

import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Note from Tim: this is a critical order for regular accounting work.
 * Critical work costs extra.
 * Regular accounting work is cheaper in bulk, so once an employee count
 * threshold is reached the cost for that report remains the same.
 */
@SuppressWarnings("Duplicates")
public class FirstOrderType implements Order {
    private Map<Report, Integer> reports = new HashMap<>();
    private LocalDateTime date;
    private double criticalLoading;
    private int clientID;
    private int id;
    private int maxCountedEmployees;
    private boolean finalised = false;


    public FirstOrderType(int id, int clientID, LocalDateTime date, double criticalLoading, int maxCountedEmployees) {
        this.date = date;
        this.criticalLoading = criticalLoading;
        this.clientID = clientID;
        this.id = id;
        this.maxCountedEmployees = maxCountedEmployees;
    }

    /**
     *
     * @return Returns the Date, of when the order was created (Assumption)
     */
    @Override
    public LocalDateTime getOrderDate() {
        return date;
    }

    /**
     *
     *  @param report The report to be set
     *  @param employeeCount Number of employees assigned to the report(Assumption)
     */
    @Override
    public void setReport(Report report, int employeeCount) {
        if (finalised) throw new IllegalStateException("Order was already finalised.");

        // We can't rely on equal reports having the same object identity since they get
        // rebuilt over the network, so we have to check for presence and same values

        for (Report contained: reports.keySet()) {
            if (report.equals(contained)) {
                report = contained;
                break;
            }
        }

        reports.put(report, employeeCount);
    }

    /**
     *
      * @return Returns a unique set of all reports.
     */
    @Override
    public Set<Report> getAllReports() {
        return reports.keySet();
    }


    /**
     *
     * @param report retrieves the employee count assigned to a report.
     * @return
     */
    @Override
    public int getReportEmployeeCount(Report report) {
        // We can't rely on equal reports having the same object identity since they get
        // rebuilt over the network, so we have to check for presence and same values

        for (Report contained: reports.keySet()) {
            if (report.equals(getTotalCommission())) {
                report = contained;
                break;
            }
        }

        Integer result = reports.get(report);
        return null == result ? 0 : result;
    }

    /**
     *
      * @return Retrieves the client id set during instantiation
     */
    @Override
    public int getClient() {
        return clientID;
    }

    /**
     *
     * @return a copy of the report
     */
    @Override
    public Order copy() {
        Order copy = new FirstOrderType(id, clientID, date, criticalLoading, maxCountedEmployees);
        for (Report report : reports.keySet()) {
            copy.setReport(report, reports.get(report));
        }

        if(finalised)
        {
            copy.finalise();
        }

        return copy;
    }

    /**
     *
      * @return the critical loading of the report
     */
    protected double getCriticalLoading() {
        return this.criticalLoading;
    }

    /**
     *
      * @return  a String based representation of the charge applied to the invoice data.
     */
    @Override
    public String generateInvoiceData() {
        return String.format("Your priority business account has been charged: $%,.2f" +
                "\nPlease see your internal accounting department for itemised details.", getTotalCommission());
    }

    /**
     *
      * @return the total commission
     */
    @Override
    public double getTotalCommission() {
        double cost = 0.0;
        for (Report report : reports.keySet()) {
            cost += report.getCommission() * Math.min(maxCountedEmployees, reports.get(report));
        }

        cost += cost * criticalLoading;
        return cost;
    }

    /**
     *
      * @return Map of all the reports
     */
    protected Map<Report, Integer> getReports() {
        return reports;
    }

    /**
     *
      * @return retrieves the order ID
     */
    @Override
    public int getOrderID() {
        return id;
    }

    /**
     * finalizes the report
     */
    @Override
    public void finalise() {
        this.finalised = true;
    }

    /**
     *
      * @return a short description of the report in a string representation
     */
    @Override
    public String shortDesc() {
        return String.format("ID:%s $%,.2f", id, getTotalCommission());
    }

    /**
     *
      * @return long description of the report in string representation
     */
    @Override
    public String longDesc() {
        double baseCommission = 0.0;
        double loadedCommission = getTotalCommission();
        StringBuilder reportSB = new StringBuilder();

        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * Math.min(maxCountedEmployees, reports.get(report));
            baseCommission += subtotal;

            reportSB.append(String.format("\tReport name: %s\tEmployee Count: %d\tCommission per employee: $%,.2f\tSubtotal: $%,.2f",
                    report.getReportName(),
                    reports.get(report),
                    report.getCommission(),
                    subtotal));

            if (reports.get(report) > maxCountedEmployees) {
                reportSB.append(" *CAPPED*\n");
            } else {
                reportSB.append("\n");
            }
        }

        return String.format((finalised ? "" : "*NOT FINALISED*\n") +
                        "Order details (id #%d)\n" +
                        "Date: %s\n" +
                        "Reports:\n" +
                        "%s" +
                        "Critical Loading: $%,.2f\n" +
                        "Total cost: $%,.2f\n",
                id,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                reportSB.toString(),
                loadedCommission - baseCommission,
                loadedCommission
        );
    }

    /**
     *
      * @return true or false if it is finalized
     */
    protected boolean isFinalised() {
        return finalised;
    }

    /**
     *
     * @return maximum count of employees
     */
    protected int getMaxCountedEmployees() {
        return maxCountedEmployees;
    }
}
