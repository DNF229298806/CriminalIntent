package example.com.criminalintent.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import example.com.criminalintent.R;

/**
 * @author Richard_Y_Wang
 * @version $Rev$
 * @des 2018/5/15
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_timepicker,null);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.timepicker_title)
                .setPositiveButton(android.R.string.ok,null)
                .create();
    }
}
