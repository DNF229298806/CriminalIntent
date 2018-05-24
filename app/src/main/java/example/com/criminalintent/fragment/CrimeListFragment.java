package example.com.criminalintent.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import example.com.criminalintent.ItemTouchHelperAdapter;
import example.com.criminalintent.R;
import example.com.criminalintent.SimpleItemTouchHelperCallback;
import example.com.criminalintent.mode.Crime;
import example.com.criminalintent.mode.CrimeLab;

import static android.support.v7.widget.helper.ItemTouchHelper.*;

/**
 * @author Richard_Y_Wang
 * @version $Rev$
 * @des 2018/5/9
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class CrimeListFragment extends Fragment {
    private static final String SACED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView crimeRecyclerView;
    private CrimeAdapter adapter;
    private int position;
    private boolean subtitleVisible;
    private TextView tv_No_Info;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeSelected(Crime crime);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        crimeRecyclerView = view.findViewById(R.id.rcv_crime_list);
        //如果没有LayoutManager的支持 RecyclerView无法工作 会导致程序崩溃
        crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //这里有警告说是 getActivity() 这里有可能会报空 null  加入分割线
        crimeRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(SACED_SUBTITLE_VISIBLE);
        }
        tv_No_Info = getActivity().findViewById(R.id.tv_no_info);
        updateUI();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SACED_SUBTITLE_VISIBLE, subtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //实例化选项菜单
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        System.out.println("onCreateOptionsMenu:  " + subtitleVisible);
        if (subtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
               /*Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);*/
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            case R.id.show_subtitle:
                System.out.println("onOptionsItemSelected:  " + subtitleVisible);
                subtitleVisible = !subtitleVisible;
                System.out.println("onOptionsItemSelected:  " + subtitleVisible);
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        //String subtitle = getString(R.string.subtitle_format, crimeCount);
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        System.out.println("crimeCount:" + crimeCount);
        System.out.println("subtitle:" + subtitle);
        System.out.println("updateSubtitle:  " + !subtitleVisible);
        if (!subtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        //如果是空的话 就new adapter 并设置adapter
        //否则通知adapter 数据发生的改变
        if (adapter == null) {
            adapter = new CrimeAdapter(crimes);
            crimeRecyclerView.setAdapter(adapter);
            //先实例化Callback
            Callback callback = new SimpleItemTouchHelperCallback(adapter);
            //用Callback构造ItemtouchHelper
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            //调用ItemTouchHelper的attachToRecyclerView方法建立联系
            touchHelper.attachToRecyclerView(crimeRecyclerView);
        } else {
            adapter.setCrimes(crimes);
            //adapter.notifyItemChanged(position);
            adapter.notifyDataSetChanged();
        }
        if (crimes.size() == 0) {
            //tv_No_Info.setVisibility(View.VISIBLE);
        } else {
            //tv_No_Info.setVisibility(View.GONE);
        }
        updateSubtitle();
    }

    public class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Crime crime;
        private TextView tv_crime_title;
        private TextView tv_crime_date;
        private ImageView iv_crime_solved;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            //实例化crime_recycle_item 并传入ViewHolder
            super(inflater.inflate(R.layout.crime_recycle_item, parent, false));
            tv_crime_title = itemView.findViewById(R.id.tv_crime_title);
            tv_crime_date = itemView.findViewById(R.id.tv_crime_date);
            iv_crime_solved = itemView.findViewById(R.id.iv_crime_solved);
            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            this.crime = crime;
            tv_crime_title.setText(this.crime.getTitle());
            //日期格式转化
            String myString = DateFormat.format("yyyy年MM月dd日 HH:mm:ss EEEE", this.crime.getDate()).toString();
            System.out.println(myString);
            tv_crime_date.setText(myString);
            iv_crime_solved.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), crime.getTitle() + "clicked!", Toast.LENGTH_SHORT).show();
            /*Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
            startActivity(intent);*/
            mCallbacks.onCrimeSelected(crime);
        }
    }

    public class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> implements ItemTouchHelperAdapter {
        private List<Crime> crimes;

        public CrimeAdapter(List<Crime> crimes) {
            this.crimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = crimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return crimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            this.crimes = crimes;
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            //交换位置
            Collections.swap(crimes, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(int position) {
            //移除数据
            crimes.remove(position);
            Crime crime = CrimeLab.get(getActivity()).getCrimes().get(position);
            CrimeLab.get(getActivity()).removeCrime(crime);
            notifyItemRemoved(position);
        }
    }
}

