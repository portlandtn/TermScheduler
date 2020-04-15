package DataProvider;

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

}
