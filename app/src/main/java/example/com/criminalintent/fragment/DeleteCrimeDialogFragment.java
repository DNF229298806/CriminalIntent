package example.com.criminalintent.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.UUID;

import example.com.criminalintent.R;
import example.com.criminalintent.mode.Crime;
import example.com.criminalintent.mode.CrimeLab;

/**
 * @author Richard_Y_Wang
 * @version $Rev$
 * @des 2018/5/16
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class DeleteCrimeDialogFragment extends DialogFragment{

    private static final String ARG_CRIME = "crime";
    public static DeleteCrimeDialogFragment newInstance(UUID uuid) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME,uuid);
        DeleteCrimeDialogFragment fragment = new DeleteCrimeDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("您确定要删除该条记录？")
                .setIcon(R.drawable.ic_menu_warning)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UUID crime_id = (UUID) getArguments().getSerializable(ARG_CRIME);
                        Crime crime = CrimeLab.get(getActivity()).getCrime(crime_id);
                        CrimeLab.get(getActivity()).removeCrime(crime);
                        /*Intent intent = new Intent(getActivity(), CrimeListActivity.class);
                        startActivity(intent);*/
                        getActivity().finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .create();
    }
}
