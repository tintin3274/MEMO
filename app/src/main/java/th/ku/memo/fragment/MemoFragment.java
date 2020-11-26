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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import th.ku.memo.R;
import th.ku.memo.activity.MemoActivity;
import th.ku.memo.adapter.MemoItemAdapter;
import th.ku.memo.model.DatabaseHelper;
import th.ku.memo.model.Memo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class MemoFragment extends Fragment {
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

        view = inflater.inflate(R.layout.fragment_memo, container, false);


        Button buttonCreate = view.findViewById(R.id.buttonCreateDay);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, MemoActivity.class);
                context.startActivity(intent);
            }
        });

        loadMemo();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMemo();

    }

    public void loadMemo(){
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        RecyclerView recyclerView = view.findViewById(R.id.RecyclerView);
        ArrayList<Memo> memos = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query("memo", null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        while(!cursor.isAfterLast()) {
            Memo memo = new Memo(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                    LocalDateTime.parse(cursor.getString(6)), LocalDateTime.parse(cursor.getString(7)));
            memos.add(memo);
            cursor.moveToNext();
        }

        memos.sort(new Comparator<Memo>() {
            @Override
            public int compare(Memo memo, Memo t1) {
                switch (sharedPreferences.getString("MemoSortBy", "Update Date Time - New")) {
                    case "Update Date Time - New":
                        return t1.getTimeUpdate().compareTo(memo.getTimeUpdate());
                    case "Update Date Time - Old":
                        return memo.getTimeUpdate().compareTo(t1.getTimeUpdate());
                    case "Create Date Time - New":
                        return t1.getTimeCreate().compareTo(memo.getTimeCreate());
                    case "Create Date Time - Old":
                        return memo.getTimeCreate().compareTo(t1.getTimeCreate());
                    default:
                }

                return 0;
            }
        });

        MemoItemAdapter memoItemAdapter = new MemoItemAdapter(memos);
        recyclerView.setAdapter(memoItemAdapter);
        RecyclerView.LayoutManager layoutManager;

        if(sharedPreferences.getBoolean("MemoLayoutGrid", true)) {
            layoutManager = new GridLayoutManager(getContext(), 2);
        }
        else {
            layoutManager = new LinearLayoutManager(getContext());
        }

        recyclerView.setLayoutManager(layoutManager);
        sqLiteDatabase.close();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.top_nav_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuButtonSort: clickSort(); break;
            case R.id.menuButtonLayout:
                if(sharedPreferences.getBoolean("MemoLayoutGrid", true)) {
                    item.setIcon(R.drawable.row);
                    editor.putBoolean("MemoLayoutGrid", false);
                }
                else {
                    item.setIcon(R.drawable.blocks);
                    editor.putBoolean("MemoLayoutGrid", true);
                }
                editor.commit();
                loadMemo();
                break;
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
                editor.putString("MemoSortBy", selected);
                editor.commit();
                Toast.makeText(getContext(), "Memo Sort By: " + selected, Toast.LENGTH_SHORT).show();
                loadMemo();
            }
        });
        builder.create();
        builder.show();
    }
}