package config;

public class StaticData {
    public static String BLP2_EXP = 
            "E:\\PhD\\BugLocatorP2\\";
  //public static String BRICK_EXP = "C:/My MSc/ThesisWorks/Crowdsource_Knowledge_Base/M4CPBugs/experiment";
    public static String BRICK_EXP="F:/MyWorks/Thesis Works/Crowdsource_Knowledge_Base/M4CPBugs/experiment";

    public static double SIGNIFICANCE_THRESHOLD = 0.0001;
    public final static int WINDOW_SIZE = 2;
    public static int MAX_QUERY_LEN = 1024;
    
    public static double INITIAL_TERM_WEIGHT=0.25;
    public static double KEYWORD_RATIO= 0.33;
    public static int KCORE_SIZE=2;

    public static double alpha =1;// 0.45309403507098156;
    public static double beta = 1;//0.8374424351745824;
    public static double gamma =1;// 0.3753504417175002;
    
    public static double REDUCTION_GAIN_TH=0.50;
}
