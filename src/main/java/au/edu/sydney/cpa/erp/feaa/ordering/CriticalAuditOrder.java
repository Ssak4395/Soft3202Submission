package au.edu.sydney.cpa.erp.feaa.ordering;

import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Note from Tim: this is a critical order for audit accounting work.
 * Critical work costs extra.
 * Audits go into detail and so charge for all employees
 */
@SuppressWarnings("Duplicates")
public class CriticalAuditOrder implements Order {
    private Map<Report, Integer> reports = new HashMap<>();
    private final int id;
    private LocalDateTime date;
    private int client;
    private double criticalLoading;
    private boolean finalised = false;


    /**
     *
     * @param id  Order id
     * @param client Client ID to be passed
     * @param date Date of Creation(I think)
     * @param criticalLoading
     */
    public CriticalAuditOrder(int id, int client, LocalDateTime date, double criticalLoading) {
        this.id = id;
        this.client = client;
        this.date = date;
        this.criticalLoading = criticalLoading;
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
     * @param report The report to be set
     * @param employeeCount Number of employees assigned to the report(Assumption)
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

            //(my Comment) So basically the value object, has led to me reducing this code to one line.
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
            if (report.equals(contained)) {
                report = contained;
                break;
            }
        }
        Integer result = reports.get(report);
        return null == result ? 0 : result;
    }


    /**
     * Retrieves the client id set during instantiation
     * @return
     */
    @Override
    public int getClient() {
        return client;
    }

    /**
     * Finalises the report, sets finalised variable to true, it is now ready to be sent.
     */
    @Override
    public void finalise() {
        this.finalised = true;
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
     * @return returns a copy of the order.
     */
    @Override
    public Order copy() {
        Order copy = new CriticalAuditOrder(id, client, date, criticalLoading);
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
     * @return a string based description of the order that is short
     */
    @Override
    public String shortDesc() {
        return String.format("ID:%s $%,.2f", id, getTotalCommission());
    }
    /**
     *
     * @return a string based description of the order that is long
     */
    @Override
    public String longDesc() {
        double baseCommission = 0.0;
        double loadedCommission = getTotalCommission();
        StringBuilder reportSB = new StringBuilder();

        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * reports.get(report);
            baseCommission += subtotal;

            reportSB.append(String.format("\tReport name: %s\tEmployee Count: %d\tCommission per employee: $%,.2f\tSubtotal: $%,.2f\n",
                    report.getReportName(),
                    reports.get(report),
                    report.getCommission(),
                    subtotal));
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
     * @return a String based representation of the charge applied to the invoice data.
     */
    @Override
    public String generateInvoiceData() {
        return String.format("Your priority business account has been charged: $%,.2f" +
                "\nPlease see your internal accounting department for itemised details.", getTotalCommission());
    }

    /**
     *
     * @return the Total Commission
     */
    @Override
    public double getTotalCommission() {
        double cost = 0.0;
        for (Report report : reports.keySet()) {
            cost += reports.get(report) * report.getCommission();
        }
        cost += cost * criticalLoading;
        return cost;
    }

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
     *
     * @return  true or false if report is finalized
     */
    protected boolean isFinalised() {
        return finalised;
    }
}
