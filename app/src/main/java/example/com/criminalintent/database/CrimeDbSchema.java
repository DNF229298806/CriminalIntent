package example.com.criminalintent.database;

/**
 * @author Richard_Y_Wang
 * @version $Rev$
 * @des 2018/5/21
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class CrimeDbSchema {
    //CrimeTable的唯一用途是描述表元素的String常量
    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String NUMBER = "number";
        }
    }
}
