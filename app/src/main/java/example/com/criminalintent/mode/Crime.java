package example.com.criminalintent.mode;

import java.util.Date;
import java.util.UUID;

/**
 * @author Richard_Y_Wang
 * @version $Rev$
 * @des 2018/5/9
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class Crime {
    private UUID id;
    private String title;
    private Date date;
    private boolean solved;
    private String suspect;
    private String mNumber;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        this.id = id;
        date = new Date();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getSuspect() {
        return suspect;
    }

    public void setSuspect(String suspect) {
        this.suspect = suspect;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

}
