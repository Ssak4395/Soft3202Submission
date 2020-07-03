package au.edu.sydney.cpa.erp.feaa.reports;

import au.edu.sydney.cpa.erp.ordering.Report;
import com.google.common.primitives.ImmutableDoubleArray;


import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 * Basically two patterns have been applied here, firstly I have applied the value object design pattern
 *
 * Dot Points targeted
 *
 * 1.The system uses a LOT of RAM. Analysis has indicated this is due to the Report class, which
 * stores a lot of data. CPA would like you to solve this RAM issue somehow, without breaking the
 * existing use of the Report interface.
 * ReportImpl has been included in your module scope to assist with this,
 * but ReportDatabase is a fake façade on a remote database that you cannot change.
 *
 * 2.Because the Report object captures data without any consistent primary key, and because people have duplicated object names and versions,
 * any time reports need to be compared for equality we have to remember to check many fields. CPA would like you to make this process simpler.
 *
 *
 * Design pattern Applied Flyweight
 *
 * Intent: The intent of this pattern is to use sharing to support a large number of objects that have part of their internal state in common where the other part of state can vary.
 *
 * Consequences: Lower RAM usage
 *
 * This pattern was used to ensure equality between reports could be compared much more easily, to do this
 * I used a Guava ImmutableArray, since creating a unmodifiable array natively is a pain
 *
 * Design pattern applied Value Object
 *
 * Intent:A Value Object is an immutable type that is distinguishable only by the state of its properties.
 *
 * Consequences: Comparing two reports is now much more simple, contents of the report cannot be altered.
 *
 *
 */
public class ReportImpl implements Report  {


    //I am not sure I should add the final keyword for the ImmutableDataTypes, they
    // are suppose to be immutable aren't they?
    /*
    * Also this library was imported from Google Guava, using Collections.unModified, really messed with my
    * Heap size, so yea, it was a choice between failing the scope test or using this.
    *
    * */
    private final String name;
    private final double commissionPerEmployee;
    private final ImmutableDoubleArray legalData;
    private final ImmutableDoubleArray cashFlowData;
    private final ImmutableDoubleArray mergesData;
    private final ImmutableDoubleArray tallyingData;
    private final ImmutableDoubleArray deductionsData;


    public ReportImpl(String name,
                      double commissionPerEmployee,
                      double[] legalData,
                      double[] cashFlowData,
                      double[] mergesData,
                      double[] tallyingData,
                      double[] deductionsData) {

       /*
       * Basically I am not exactly sure if it is good practice to have conditional statements, in the constructor
       * I know it can be delegated to other classes, but in the heat of the moment I decided to leave them here
       * */


        this.name = name;
        this.commissionPerEmployee = commissionPerEmployee;

       /*Basically what is happening here is I am doing a null check, because ImmutableDoubleArray does not like null
       * arguments at all
       * After this check is done, I put this in my hashmap, and if this data type, is already in my hashmap I just reuse, that
       * this ultimately reduces my RAM
       * This can be deferred to my flyweight class, but i was running a bit short on time with another assignments.
       * Also not exactly sure if this falls in line with best practice java
       * */

        if(legalData == null) {this.legalData = null; } else { this.legalData = ReportRepository.getArray(ReportRepository.getHash(ImmutableDoubleArray.copyOf(legalData))); }
        if(cashFlowData == null) {this.cashFlowData = null; } else { this.cashFlowData = ReportRepository.getArray(ReportRepository.getHash(ImmutableDoubleArray.copyOf(cashFlowData))); }
        if(mergesData == null) {this.mergesData = null; } else { this.mergesData = ReportRepository.getArray(ReportRepository.getHash(ImmutableDoubleArray.copyOf(mergesData))); }
        if(tallyingData == null) {this.tallyingData = null; } else { this.tallyingData = ReportRepository.getArray(ReportRepository.getHash(ImmutableDoubleArray.copyOf(tallyingData))); }
        if(deductionsData == null) {this.deductionsData = null; } else { this.deductionsData = ReportRepository.getArray(ReportRepository.getHash(ImmutableDoubleArray.copyOf(deductionsData))); }

    }


    // Your traditional getters and setters, no computational wizardry here
    /**
     *
     * @return Name of the report
     */
    @Override
    public String getReportName() {

        return name;
    }
    /**
     *
     * @return Commission of the report
     */
    @Override
    public double getCommission() {
        return commissionPerEmployee;
    }

    /**
     *
     * @return Legal data array
     */
        @Override
    public double[] getLegalData() {
       /*Copies the Immutable Double Array and converts it into a array to validate the interface return types
       * */

        double[] copy = new double[legalData.length()];
        for(int i = 0; i<legalData.length();++i)
        {
            copy[i] = legalData.get(i);
        }
        return copy;

    }
    /**
     *
     * @return CashFlow data array
     */
    @Override
    public double[] getCashFlowData() {
        /*Copies the Immutable Double Array and converts it into a array to validate the interface return types
         * */
        double[] copy = new double[cashFlowData.length()];
        for(int i = 0; i<cashFlowData.length();++i)
        {
            copy[i] = cashFlowData.get(i);
        }
        return copy;
    }
    /**
     *
     * @return Merges data array
     */
    @Override
    public double[] getMergesData() {
        /*Copies the Immutable Double Array and converts it into a array to validate the interface return types
         * */
        double[] copy = new double[mergesData.length()];
        for(int i = 0; i<mergesData.length();++i)
        {
            copy[i] = mergesData.get(i);
        }
        return copy;    }
    /**
     *
     * @return Tallying Data array
     */
    @Override
    public double[] getTallyingData() {
        /*Copies the Immutable Double Array and converts it into a array to validate the interface return types
         * */
        double[] copy = new double[tallyingData.length()];
        for(int i = 0; i<tallyingData.length();++i)
        {
            copy[i] = tallyingData.get(i);
        }
        return copy;
    }

    /**
     *
     * @return Deductions data array
     */
    @Override
    public double[] getDeductionsData() {
        /*Copies the Immutable Double Array and converts it into a array to validate the interface return types
         * */
        double[] copy = new double[deductionsData.length()];
        for(int i = 0; i<deductionsData.length();++i)
        {
            copy[i] = deductionsData.get(i);
        }
        return copy;

    }

    /**
     *
     * @return String representation of a report object
     */
    @Override
    public String toString() {

        return String.format("%s", name);
    }


    /*
    * I was not able to figure out a suitable primary key to use, to ensure reports can be easily compared,
    * I never understook why reports where being compared in the orders in such a naive way, whats the point of comparing th
    * name, commission....data types of report in the order, class, when you can simply delegate a overriden equal method
    *
    * My method checks that a report is EQUAL if AND ONLY IF all the variables inside of it are equal. That way we can just call
    * MyReport.equals(Report) in order, instead of naively comparing everything*/


    /**
     * Compares based on variable equality and returns true if equal false if not equal
     * @param obj
     * @return true if equal false, if not equal
     */
    @Override
    public boolean equals(Object obj)
    {
          if(this == obj){return true;}
          Report report = (Report) obj;
          if(obj.getClass() != this.getClass()) { return false; }
          return name.equals(report.getReportName()) && this.commissionPerEmployee == report.getCommission() &&
                  this.legalData.equals(ImmutableDoubleArray.copyOf(report.getLegalData())) && this.mergesData.equals(ImmutableDoubleArray.copyOf(report.getMergesData())) &&
                  this.deductionsData.equals(ImmutableDoubleArray.copyOf(report.getDeductionsData())) && this.tallyingData.equals(ImmutableDoubleArray.copyOf(report.getTallyingData()))
                  && this.cashFlowData.equals(ImmutableDoubleArray.copyOf(report.getCashFlowData()));
    }

    // COME BACK TO ME, IF I HAVE TIME, MAYBE TRY CONVERTING THE DOUBLES TO IMMUTABLE OBJECTS AND THEN RETURN IN TRUE OR FALSE

    /*
    SO i had time, and i changed it from this.legalData.equals(report.getLegalData) to this.legalData.equals(ImmutableDoubArray.copyof(report.getLegalData), probably
    the same thing, but i get less warnings

     */


    /*
    * Basically custom hash, hopefully this is unique enough to prevent collision, I am not sure
    * if it would be better use the java one. So....... :/ */


    /**
     *
     * @return the hashcode, of a report object
     */
    @Override
    public int hashCode()
    {
     return  this.name.hashCode() + 2323 +
             this.deductionsData.hashCode() + 33 +
             this.legalData.hashCode()+ 654
             + this.tallyingData.hashCode() + 6443
             + this.mergesData.hashCode()+ 643
             + Double.valueOf(commissionPerEmployee).hashCode()+
             this.cashFlowData.hashCode();
    }
    //
}
