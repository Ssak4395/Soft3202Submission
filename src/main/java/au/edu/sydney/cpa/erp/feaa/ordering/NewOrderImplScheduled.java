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
public class NewOrderImplScheduled extends NewOrderImpl implements ScheduledOrder {
    private int numQuarters;

    public NewOrderImplScheduled(int id, int customerID, LocalDateTime date, int numQuarters) {
        super(id, customerID, date);
        this.numQuarters = numQuarters;
    }


    /**
     *
     * @return recurring cost of the report
     */
    @Override
    public double getRecurringCost() {
        return super.getTotalCommission();
    }

    /**
     *
     * @return number of quarters
     */
    @Override
    public int getNumberOfQuarters() {
        return numQuarters;
    }

    /**
     *
     * @return the total commission assigned to the report
     */
    @Override
    public double getTotalCommission() {
        return super.getTotalCommission() * numQuarters;
    }

    /**
     *
     * @return String of the Invoice data of the report.
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
            sb.append("\tSubtotal: ");
            sb.append(String.format("$%,.2f\n", report.getCommission() * reports.get(report)));
        }
        return sb.toString();
    }

    /**
     *
     * @return a copy of the current report
     */
    @Override
    public Order copy() {
        Map<Report, Integer> products = super.getReports();

        Order copy = new NewOrderImplScheduled(getOrderID(), getClient(), getOrderDate(), getNumberOfQuarters());
        for (Report report : products.keySet()) {
            copy.setReport(report, products.get(report));
        }

        return copy;
    }

    /**
     *
     * @return a short description of the report, in a string representation
     */
    @Override
    public String shortDesc() {
        return String.format("ID:%s $%,.2f per quarter, $%,.2f total", super.getOrderID(), getRecurringCost(), getTotalCommission());
    }
    /**
     *
     * @return a long description of the report, in a string representation
     */
    @Override
    public String longDesc() {
        double totalLoadedCost = this.getTotalCommission();
        StringBuilder reportSB = new StringBuilder();

        List<Report> keyList = new ArrayList<>(super.getReports().keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * super.getReports().get(report);

            reportSB.append(String.format("\tReport name: %s\tEmployee Count: %d\tCommission per employee: $%,.2f\tSubtotal: $%,.2f\n",
                    report.getReportName(),
                    super.getReports().get(report),
                    report.getCommission(),
                    subtotal));
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
                totalLoadedCost
        );
    }
}
