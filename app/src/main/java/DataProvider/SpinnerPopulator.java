package DataProvider;

public class SpinnerPopulator {

    public static String[] getCourseStatuses() {
        CourseStatus[] statuses = CourseStatus.values();
        String[] statusString = new String[statuses.length];

        for (int i = 0; i < statuses.length; i++) {
            statusString[i] = statuses[i].getStatus();
        }

        return statusString;
    }

    public static String[] getAssessmentStatuses() {
        AssessmentStatus[] statuses = AssessmentStatus.values();
        String[] statusString = new String[statuses.length];

        for (int i = 0; i < statuses.length; i++) {
            statusString[i] = statuses[i].getStatus();
        }

        return statusString;
    }

}
