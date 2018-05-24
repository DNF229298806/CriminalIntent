package example.com.criminalintent.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import example.com.criminalintent.PermissionUtil;
import example.com.criminalintent.PictureUtils;
import example.com.criminalintent.R;
import example.com.criminalintent.mode.Crime;
import example.com.criminalintent.mode.CrimeLab;

/**
 * @author Richard_Y_Wang
 * @version $Rev$
 * @des 2018/5/9
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_DELETE_CRIME = "DialogDeleteCrime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private Crime crime;
    private EditText et_TitleField;
    private Button bt_Date;
    private CheckBox cb_Solved;
    private Button bt_To_First;
    private Button bt_To_Last;
    private ViewPager viewPager;
    private Button bt_Crime_Time;
    private Button bt_Choose_Suspect;
    private Button bt_Send_Crime_Report;
    private Button bt_Call;
    private ImageButton ib_Crime_Camera;
    private ImageView iv_Crime_Photo;
    private File photoFile;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        if (bundle != null) {
            UUID crimeId = (UUID) bundle.getSerializable(CrimeFragment.ARG_CRIME_ID);
            crime = CrimeLab.get(getActivity()).getCrime(crimeId);
        }
        photoFile = CrimeLab.get(getActivity()).getPhotoFile(crime);
        String[] arr_permission = {Manifest.permission.READ_CONTACTS};
        PermissionUtil.addPermission(getActivity(), arr_permission);
        if (!PermissionUtil.checkPermission(getActivity())) {
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //第一个参数是布局文件，第二个参数是视图的父视图，第三个参数告诉布局生成器是否将生成的视图添加给父视图
        //这里传入false参数 因为我们将以代码的方式添加视图
        final View v = inflater.inflate(R.layout.fragment_crime, container, false);
        et_TitleField = v.findViewById(R.id.et_crime_title);
        bt_Date = v.findViewById(R.id.bt_crime_date);
        cb_Solved = v.findViewById(R.id.cb_crime_solved);
        bt_To_First = v.findViewById(R.id.bt_to_first);
        bt_To_Last = v.findViewById(R.id.bt_to_last);
        viewPager = getActivity().findViewById(R.id.vp_activity_crime_pager);
        bt_Crime_Time = v.findViewById(R.id.bt_crime_time);
        bt_Choose_Suspect = v.findViewById(R.id.bt_choose_suspect);
        bt_Send_Crime_Report = v.findViewById(R.id.bt_send_crime_report);
        ib_Crime_Camera = v.findViewById(R.id.ib_crime_camera);
        iv_Crime_Photo = v.findViewById(R.id.iv_crime_photo);
        bt_Call = v.findViewById(R.id.bt_call);
        et_TitleField.setText(crime.getTitle());
        et_TitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                crime.setTitle(charSequence.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        updateDate();
        bt_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
                //设置目标fragment
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        bt_Crime_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = new TimePickerFragment();
                dialog.show(manager, DIALOG_TIME);
            }
        });
        cb_Solved.setChecked(crime.isSolved());
        cb_Solved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
                updateCrime();
            }
        });
        bt_To_First.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });
        bt_To_Last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(CrimeLab.get(getActivity()).getCrimes().size() - 1);
                System.out.println(CrimeLab.get(getActivity()).getCrimes().size());
            }
        });
        bt_Send_Crime_Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent = intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });
        //打开联系人
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        bt_Choose_Suspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (crime.getSuspect() != null) {
            bt_Choose_Suspect.setText(crime.getSuspect());
        }
        //结束
        //检查是否存在联系人应用
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            bt_Choose_Suspect.setEnabled(false);
        }
        //打开联系人 打电话
        if (crime.getNumber() == null) {
            bt_Call.setEnabled(false);
        } else {
            bt_Call.setEnabled(true);
            bt_Call.setText(crime.getNumber());
        }
        bt_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri number = Uri.parse("tel:" + crime.getNumber());
                System.out.println(crime.getNumber());
                intent.setData(number);
                startActivity(intent);
            }
        });

        //相机部分
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //检查有没有安装相机程序
        boolean canTakePhoto = photoFile != null && captureImage.resolveActivity(packageManager) != null;
        ib_Crime_Camera.setEnabled(canTakePhoto);
        ib_Crime_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "example.com.criminalintent.fileprovider", photoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        //缩略图部分
        iv_Crime_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                PictureDialogFragment pictureDialogFragment = PictureDialogFragment.newInstance(photoFile.getPath());
                //pictureDialogFragment.setTargetFragment(CrimeFragment.this, 0);
                pictureDialogFragment.show(manager, "a");
            }
        });
        int size = CrimeLab.get(getActivity()).getCrimes().size() - 1;
        if (crime.getId().equals(CrimeLab.get(getActivity()).getCrimes().get(0).getId()))
            bt_To_First.setEnabled(false);
        if (crime.getId().equals(CrimeLab.get(getActivity()).getCrimes().get(size).getId()))
            bt_To_Last.setEnabled(false);
        /*ViewTreeObserver mPhotoObserver = iv_Crime_Photo.getViewTreeObserver();
        mPhotoObserver.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        updatePhotoView(iv_Crime_Photo.getWidth(), iv_Crime_Photo.getHeight());
                    }
                });*/
        updatePhotoView();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            updateCrime();
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID
            };
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            String SuspectContactId = null;
            try {
                if (c.getCount() == 0) {
                    c.close();
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                System.out.println(suspect);
                crime.setSuspect(suspect);
                updateCrime();
                bt_Choose_Suspect.setText(suspect);

                String _id = c.getString(1);
                SuspectContactId = _id;
            } finally {
                c.close();
            }
            Cursor cursor = getActivity().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{SuspectContactId},
                            null);
            try {
                if (cursor.getCount() == 0) {
                    return;
                }

                cursor.moveToFirst();
                int index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(index);
                crime.setNumber(number);
                bt_Call.setEnabled(true);
                bt_Call.setText(number);
            } finally {
                cursor.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "example.com.criminalintent.fileprovider", photoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhotoView();
        }
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(crime);
        mCallbacks.onCrimeUpdated(crime);
    }

    private void updateDate() {
        bt_Date.setText(crime.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, crime.getDate()).toString();
        String suspect = crime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report, crime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void updatePhotoView() {
        if (photoFile == null || !photoFile.exists()) {
            iv_Crime_Photo.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
            iv_Crime_Photo.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                FragmentManager manager = getFragmentManager();
                DeleteCrimeDialogFragment deleteCrimeDialogFragment = DeleteCrimeDialogFragment.newInstance(crime.getId());
                deleteCrimeDialogFragment.show(manager, DIALOG_DELETE_CRIME);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(crime);
    }
}
