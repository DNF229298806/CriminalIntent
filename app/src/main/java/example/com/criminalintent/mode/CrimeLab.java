package example.com.criminalintent.mode;

/**
 * @author Richard_Y_Wang
 * @version $Rev$
 * @des 2018/5/9
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import example.com.criminalintent.database.CrimeBaseHelper;
import example.com.criminalintent.database.CrimeCursorWrapper;

import static example.com.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * 使用单例设计模式
 * 首先是使用private的构造方法 只能在类里面调用
 * 然后是把唯一一个成员变量设置成static的 这样就会放在静态储存区里了
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        //如果不适用getApplicationContext();这个方法而是直接使用context方法的话 会造成
        //当context对应的activity应该被销毁的时候无法被垃圾回收器回收造成内存的浪费
        mContext = context.getApplicationContext();
        //调用getWritableDatabase();方法时
        //(1) 打开/data/data/example.com.criminalintent/databases/crimeBase.db数据库；如果
        //不存在，就先创建crimeBase.db数据库文件。
        //(2) 如果是首次创建数据库，就调用onCreate(SQLiteDatabase)方法，然后保存最新的版本号。
        //(3) 如果已创建过数据库，首先检查它的版本号。如果CrimeBaseHelper中的版本号更高，
        //就调用onUpgrade(SQLiteDatabase, int, int)方法升级。
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            //一定要关闭cursor
            cursor.close();
        }
        return crimes;
    }

    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void removeCrime(Crime c) {
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + "=?", new String[]{c.getId().toString()});
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + "=?",
                new String[]{id.toString()});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.Cols.NUMBER, crime.getNumber());
        return values;
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + "=?", new String[]{uuidString});
    }

    public File getPhotoFile(Crime crime) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFilename());
    }

    public CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new CrimeCursorWrapper(cursor);
    }
}
