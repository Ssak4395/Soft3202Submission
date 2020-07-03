package au.edu.sydney.cpa.erp.feaa.ordering;

import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Note from Tim: this is a normal order for audit accounting work.
 */
@SuppressWarnings("Duplicates")
public class NewOrderImpl implements Order {
    private Map<Report, Integer> reports = new HashMap<>();
    private final int id;
    private LocalDateTime date;
    private int client;
    private boolean finalised = false;
    private boolean isDirty;


    public NewOrderImpl(int id, int client, LocalDateTime date) {
        this.id = id;
        this.client = client;
        this.date = date;
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
      * @return a copy of the order
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
     * @return a short string based description of the order
     */
    @Override
    public String shortDesc() {
        return String.format("ID:%s $%,.2f", id, getTotalCommission());
    }

    /**
     *
     * @return a long description of the order
     */
    @Override
    public String longDesc() {
        StringBuilder reportSB = new StringBuilder();

        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * reports.get(report);

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
                        "Total cost: $%,.2f\n",
                id,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                reportSB.toString(),
                getTotalCommission()
        );
    }

    /**
     *
     * @return the invoice data which is a string based representation of the details set in the report.
     */
    @Override
    public String generateInvoiceData() {
        double baseCommission = 0.0;
        double loadedCommission = getTotalCommission();

        StringBuilder sb = new StringBuilder();

        sb.append("Thank you for your Crimson Permanent Assurance accounting order!\n");
        sb.append("The cost to provide these services: $");
        sb.append(String.format("%,.2f", getTotalCommission()));
        sb.append("\nPlease see below for details:\n");
        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * reports.get(report);
            baseCommission += subtotal;

            sb.append("\tReport name: ");
            sb.append(report.getReportName());
            sb.append("\tEmployee Count: ");
            sb.append(reports.get(report));
            sb.append("\tCost per employee: ");
            sb.append(String.format("$%,.2f", report.getCommission()));
            sb.append("\tSubtotal: ");
            sb.append(String.format("$%,.2f\n", subtotal));
        }
        return sb.toString();
    }

    /**
     *
     * @return the total commission
     */
    @Override
    public double getTotalCommission() {
        double cost = 0.0;
        for (Report report : reports.keySet()) {
            cost += reports.get(report) * report.getCommission();
        }
        return cost;
    }

    /**
     *
     * @return a Map of the reports
     */
    protected Map<Report, Integer> getReports() {
        return reports;
    }

    /**
     *
     * @return the order ID
     */
    @Override
    public int getOrderID() {
        return id;
    }

    /**
     *
     * @return checks if report is finalized or not.
     */
    protected boolean isFinalised() {
        return finalised;
    }
}
