package au.edu.sydney.cpa.erp.feaa.ordering;

import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Note from Tim: this is a normal order for regular accounting work.
 *
 * Also note from Tim: Yes, there are another 60+ Order types cut from this release that differ in other ways.
 * There are a bunch of other client types and different sorts of accounting types out there.
 *
 * It would be really good if the new design could easily extend to support those as well.
 */
@SuppressWarnings("Duplicates")
public class Order66 implements Order {
    private Map<Report, Integer> reports = new HashMap<>();
    private final int id;
    private LocalDateTime date;
    private int maxCountedEmployees;
    private int client;
    private boolean finalised = false;

        public Order66(int id, int client, LocalDateTime date, int maxCountedEmployees) {
        this.id = id;
        this.client = client;
        this.date = date;
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
            if (contained.getCommission() == report.getCommission() &&
                    contained.getReportName().equals(report.getReportName()) &&
                    Arrays.equals(contained.getLegalData(), report.getLegalData()) &&
                    Arrays.equals(contained.getCashFlowData(), report.getCashFlowData()) &&
                    Arrays.equals(contained.getMergesData(), report.getMergesData()) &&
                    Arrays.equals(contained.getTallyingData(), report.getTallyingData()) &&
                    Arrays.equals(contained.getDeductionsData(), report.getDeductionsData())) {
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
     * @return returns a copy of the order.
     */
    @Override
    public Order copy() {
        Order copy = new NewOrderImpl(id, client, date);
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
        StringBuilder reportSB = new StringBuilder();

        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * Math.min(maxCountedEmployees, reports.get(report));

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

        return String.format(finalised ? "" : "*NOT FINALISED*\n" +
                        "Order details (id #%d)\n" +
                        "Date: %s\n" +
                        "Reports:\n" +
                        "%s" +
                        "Total cost: $%,.2f\n",
                id,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                reportSB.toString(),
                getTotalCommission()
        );
    }
    /**
     *
     * @return a String based representation of the charge applied to the invoice data.
     */
    @Override
    public String generateInvoiceData() {
        StringBuilder sb = new StringBuilder();

        sb.append("Thank you for your Crimson Permanent Assurance accounting order!\n");
        sb.append("The cost to provide these services: $");
        sb.append(String.format("%,.2f", getTotalCommission()));
        sb.append("\nPlease see below for details:\n");
        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * Math.min(maxCountedEmployees, reports.get(report));

            sb.append("\tReport name: ");
            sb.append(report.getReportName());
            sb.append("\tEmployee Count: ");
            sb.append(reports.get(report));
            sb.append("\tCost per employee: ");
            sb.append(String.format("$%,.2f", report.getCommission()));
            if (reports.get(report) > maxCountedEmployees) {
                sb.append("\tThis report cost has been capped.");
            }
            sb.append("\tSubtotal: ");
            sb.append(String.format("$%,.2f\n", subtotal));
        }
        return sb.toString();
    }
    /**
     *
     * @return the Total Commission
     */
    @Override
    public double getTotalCommission() {
        double cost = 0.0;
        for (Report report : reports.keySet()) {
            cost += report.getCommission() * Math.min(maxCountedEmployees, reports.get(report));
        }
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
    /**
     *
     * @return  maximum counted employees in the report.
     */
    protected int getMaxCountedEmployees() {
        return maxCountedEmployees;
    }
}












