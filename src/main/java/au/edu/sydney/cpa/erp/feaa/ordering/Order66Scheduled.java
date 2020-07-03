package au.edu.sydney.cpa.erp.feaa.ordering;

import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;
import au.edu.sydney.cpa.erp.ordering.ScheduledOrder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class Order66Scheduled extends Order66 implements ScheduledOrder {

    private int numQuarters;



    public Order66Scheduled(int id, int clientID, LocalDateTime date, int maxCountedEmployees, int numQuarters) {
        super(id, clientID, date, maxCountedEmployees);
        this.numQuarters = numQuarters;
    }


    /**
     *
     * @return recurring cost of the report.
     */
    @Override
    public double getRecurringCost() {
        return super.getTotalCommission();
    }
    /**
     *
     * @return the number of quarters
     */
    @Override
    public int getNumberOfQuarters() {
        return numQuarters;
    }
    /**
     *
     * @return the total commission of the report
     */
    @Override
    public double getTotalCommission() {
        return super.getTotalCommission() * numQuarters;
    }
    /**
     *
     * @return a string representation of the invoice data.
     */
    @Override
    public String generateInvoiceData() {
        StringBuilder sb = new StringBuilder();

        sb.append("Thank you for your Crimson Permanent Assurance accounting order!\n");
        sb.append("The cost to provide these services: $");
        sb.append(String.format("%,.2f", getRecurringCost()));
        sb.append(" each quarter, with a total overall cost of: $");
        sb.append(String.format("%,.2f", getTotalCommission()));
        sb.append("\nPlease see below for details:\n");

        Map<Report, Integer> reports = getReports();
        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            sb.append("\tReport name: ");
            sb.append(report.getReportName());
            sb.append("\tEmployee Count: ");
            sb.append(reports.get(report));
            sb.append("\tCost per employee: ");
            sb.append(String.format("$%,.2f", report.getCommission()));
            if (reports.get(report) > super.getMaxCountedEmployees()) {
                sb.append("\tThis report cost has been capped.");
            }
            sb.append("\tSubtotal: ");
            sb.append(String.format("$%,.2f\n", report.getCommission() * reports.get(report)));
        }

        return sb.toString();
    }
    /**
     *
     * @return a copy of the report.
     */
    @Override
    public Order copy() {
        Map<Report, Integer> products = super.getReports();

        Order copy = new Order66Scheduled(getOrderID(), getClient(), getOrderDate(), getMaxCountedEmployees(), numQuarters);
        for (Report report : products.keySet()) {
            copy.setReport(report, products.get(report));
        }

        return copy;
    }
    /**
     *
     * @return a string representation of a short description of the report
     */
    @Override
    public String shortDesc() {
        return String.format("ID:%s $%,.2f per quarter, $%,.2f total", super.getOrderID(), getRecurringCost(), getTotalCommission());
    }
    /**
     *
     * @return a long representation of report, represented as a string
     */
    @Override
    public String longDesc() {
        StringBuilder reportSB = new StringBuilder();

        List<Report> keyList = new ArrayList<>(super.getReports().keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * Math.min(super.getMaxCountedEmployees(), super.getReports().get(report));

            reportSB.append(String.format("\tReport name: %s\tEmployee Count: %d\tCommission per employee: $%,.2f\tSubtotal: $%,.2f",
                    report.getReportName(),
                    super.getReports().get(report),
                    report.getCommission(),
                    subtotal));

            if (super.getReports().get(report) > super.getMaxCountedEmployees()) {
                reportSB.append(" *CAPPED*\n");
            } else {
                reportSB.append("\n");
            }
        }

        return String.format((super.isFinalised() ? "" : "*NOT FINALISED*\n") +
                        "Order details (id #%d)\n" +
                        "Date: %s\n" +
                        "Number of quarters: %d\n" +
                        "Reports:\n" +
                        "%s" +
                        "Recurring cost: $%,.2f\n" +
                        "Total cost: $%,.2f\n",
                super.getOrderID(),
                super.getOrderDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                numQuarters,
                reportSB.toString(),
                super.getTotalCommission(),
                this.getTotalCommission()

        );
    }


}
