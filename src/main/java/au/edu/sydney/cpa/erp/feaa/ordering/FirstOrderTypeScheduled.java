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
public class FirstOrderTypeScheduled extends FirstOrderType implements ScheduledOrder {

    private int numQuarters;


    public FirstOrderTypeScheduled(int id, int clientID, LocalDateTime date, double criticalLoading, int maxCountedEmployees, int numQuarters) {
        super(id, clientID, date, criticalLoading, maxCountedEmployees);
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
        return String.format("Your priority business account will be charged: $%,.2f each quarter for %d quarters, with a total overall cost of: $%,.2f" +
                "\nPlease see your internal accounting department for itemised details.", getRecurringCost(), getNumberOfQuarters(), getTotalCommission());
    }

    /**
     *
     * @return a copy of the report.
     */
    @Override
    public Order copy() {
        Map<Report, Integer> products = super.getReports();

        Order copy = new FirstOrderTypeScheduled(getOrderID(), getClient(), getOrderDate(), getCriticalLoading(), getMaxCountedEmployees(), numQuarters);
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
        double totalBaseCost = 0.0;
        double loadedCostPerQuarter = super.getTotalCommission();
        double totalLoadedCost = this.getTotalCommission();
        StringBuilder reportSB = new StringBuilder();

        List<Report> keyList = new ArrayList<>(super.getReports().keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * Math.min(super.getMaxCountedEmployees(), super.getReports().get(report));
            totalBaseCost += subtotal;

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
                        "Critical Loading: $%,.2f\n" +
                        "Recurring cost: $%,.2f\n" +
                        "Total cost: $%,.2f\n",
                super.getOrderID(),
                super.getOrderDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                numQuarters,
                reportSB.toString(),
                totalLoadedCost - (totalBaseCost * numQuarters),
                loadedCostPerQuarter,
                totalLoadedCost

        );
    }
}
