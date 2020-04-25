package com.jedmay.termscheduler.dataProvider;

public class Validator {

    public static boolean objectHasDependencies(long[] ids, long idToSearch)
    {
        for (long id : ids)
        {
            if (id == idToSearch)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean stringsAreNotEmpty(String[] things) {

        for (String item:things) {
            if (item.isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
