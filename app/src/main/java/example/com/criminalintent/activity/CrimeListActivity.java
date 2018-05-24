package example.com.criminalintent.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;

import example.com.criminalintent.R;
import example.com.criminalintent.fragment.CrimeFragment;
import example.com.criminalintent.fragment.CrimeListFragment;
import example.com.criminalintent.mode.Crime;

/**
 * @author Richard_Y_Wang
 * @version $Rev$
 * @des 2018/5/9
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks,CrimeFragment.Callbacks{
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.fl_detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        }else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fl_fragment_container);
        listFragment.updateUI();
    }
}
