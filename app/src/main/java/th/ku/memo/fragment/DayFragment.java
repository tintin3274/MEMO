package th.ku.memo.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import th.ku.memo.R;
import th.ku.memo.activity.DayActivity;
import th.ku.memo.adapter.DayItemAdapter;
import th.ku.memo.model.DatabaseHelper;
import th.ku.memo.model.Day;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class DayFragment extends Fragment {
    private static final String[] MEMO_SORT_BY = {"Update Date Time - New", "Update Date Time - Old",
            "Create Date Time - New", "Create Date Time - Old"};

    SQLiteDatabase sqLiteDatabase;
    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        databaseHelper = new DatabaseHelper(this.getContext());

        view = inflater.inflate(R.layout.fragment_day, container, false);


        Button buttonCreate = view.findViewById(R.id.buttonCreateDay);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, DayActivity.class);
                context.startActivity(intent);
            }
        });

        loadDay();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDay();

    }

    public void loadDay() {
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        RecyclerView recyclerView = view.findViewById(R.id.RecyclerView);
        ArrayList<Day> days = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query("day", null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        while(!cursor.isAfterLast()) {
            Day day = new Day(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5),
                    LocalDate.parse(cursor.getString(6)), LocalDateTime.parse(cursor.getString(7)),
                    LocalDateTime.parse(cursor.getString(8)));
            days.add(day);
            cursor.moveToNext();
        }

        days.sort(new Comparator<Day>() {
            @Override
            public int compare(Day day, Day t1) {
                switch (sharedPreferences.getString("DaySortBy", "Update Date Time - New")) {
                    case "Update Date Time - New":
                        return t1.getTimeUpdate().compareTo(day.getTimeUpdate());
                    case "Update Date Time - Old":
                        return day.getTimeUpdate().compareTo(t1.getTimeUpdate());
                    case "Create Date Time - New":
                        return t1.getTimeCreate().compareTo(day.getTimeCreate());
                    case "Create Date Time - Old":
                        return day.getTimeCreate().compareTo(t1.getTimeCreate());
                    default:
                }

                return 0;
            }
        });

        DayItemAdapter dayItemAdapter = new DayItemAdapter(days);
        recyclerView.setAdapter(dayItemAdapter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        sqLiteDatabase.close();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.top_nav_menu_day, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuButtonSort: clickSort(); break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickSort() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sort By");
        builder.setItems(MEMO_SORT_BY, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected = MEMO_SORT_BY[which];
                editor.putString("DaySortBy", selected);
                editor.commit();
                Toast.makeText(getContext(), "D-DAY Sort By: " + selected, Toast.LENGTH_SHORT).show();
                loadDay();
            }
        });
        builder.create();
        builder.show();
    }
}