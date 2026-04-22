package report;

public enum ReportType {
   SIMPLE_MONTHLY,
   COLLECTION_AGGREGATION;

   public static ReportType getDefault() {
      return SIMPLE_MONTHLY;
   }

   public static ReportType getOrDefault(String type) {
      try {
         return valueOf(type.toUpperCase());
      } catch (Exception var2) {
         return getDefault();
      }
   }
}
