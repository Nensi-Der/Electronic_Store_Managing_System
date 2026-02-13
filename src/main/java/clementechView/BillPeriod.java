package clementechView;

public enum BillPeriod {

    TODAY("Today"),
    THIS_WEEK("This week"),
    THIS_MONTH("This month"),
    THIS_YEAR("This year"),
    ALL("All time");

    private final String label;

    BillPeriod(String label) { this.label = label; }

    @Override public String toString() { return label; }
}