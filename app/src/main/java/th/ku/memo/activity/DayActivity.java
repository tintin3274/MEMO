package th.ku.memo.activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import th.ku.memo.R;
import th.ku.memo.model.DatabaseHelper;
import th.ku.memo.model.Day;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Random;

public class DayActivity extends AppCompatActivity {
    private Day day;
    private int mYear, mMonth, mDay;
    private static final String[] CALCULATE_TYPE = {"D-DAY", "DAYS", "WEEKS"};
    private static final String[] CALCULATE_TYPE_SHOW = {"D-DAY (D-xx)", "DAYS (xx days)", "WEEKS (x weeks)"};
    private String mSelected;

    ImageButton imageButtonBackgroundColor, imageButtonTextColor, imageButtonDelete, imageButtonComplete, imageButtonCancel;
    ConstraintLayout constraintLayoutBackground;
    TextView textViewDay, textViewEventName, textViewDate, textViewCalculate, textViewCountName, textViewCountDetail;
    EditText editTextEventName, editTextEventDetail;
    TextWatcher textWatcherName, textWatcherDetail;
    ColorPicker cpTEXT, cpBG;
    DatabaseHelper databaseHelper;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_day);
        databaseHelper = new DatabaseHelper(this);

        imageButtonBackgroundColor = findViewById(R.id.imageButtonBackgroundColor);
        imageButtonTextColor = findViewById(R.id.imageButtonTextColor);
        imageButtonDelete = findViewById(R.id.imageButtonDelete);
        imageButtonComplete = findViewById(R.id.imageButtonComplete);
        imageButtonCancel = findViewById(R.id.imageButtonCancel);

        constraintLayoutBackground = findViewById(R.id.constraintLayoutBackground);
        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventDetail = findViewById(R.id.editTextEventDetail);
        textViewDay = findViewById(R.id.textViewDay);
        textViewEventName = findViewById(R.id.textViewEventName);
        textViewDate = findViewById(R.id.textViewDate);
        textViewCalculate = findViewById(R.id.textViewCalculate);
        textViewCountName = findViewById(R.id.textViewCountName);
        textViewCountDetail = findViewById(R.id.textViewCountDetail);

        textWatcherName = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textViewCountName.setText(charSequence.length()+" / 20");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                textViewEventName.setText(editable.toString());
                textViewCountName.setText(editable.length()+" / 20");
            }
        };
        textWatcherDetail = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textViewCountDetail.setText(charSequence.length()+" / 40");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                textViewCountDetail.setText(editable.length()+" / 40");
            }
        };
        editTextEventName.addTextChangedListener(textWatcherName);
        editTextEventDetail.addTextChangedListener(textWatcherDetail);

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        Random rnd = new Random();
        cpTEXT = new ColorPicker(DayActivity.this, 255, 0, 0, 0);
        cpBG = new ColorPicker(DayActivity.this, 60, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            day = (Day) bundle.get("day");

            textViewDay.setText(day.getCalculationTime());
            textViewEventName.setText(day.getName());
            editTextEventName.setText(day.getName());
            editTextEventDetail.setText(day.getDetail());
            textViewDate.setText(day.getDate().toString());
            textViewCalculate.setText(day.getCalculate());

            textViewDay.setTextColor(Color.parseColor(day.getColorText()));
            textViewEventName.setTextColor(Color.parseColor(day.getColorText()));
            constraintLayoutBackground.setBackgroundColor(Color.parseColor(day.getColorBackground()));

            cpTEXT.setColor(Color.parseColor(day.getColorText()));
            cpBG.setColor(Color.parseColor(day.getColorBackground()));
        }
        else {
            textViewDay.setTextColor(cpTEXT.getColor());
            textViewEventName.setTextColor(cpTEXT.getColor());
            constraintLayoutBackground.setBackgroundColor(cpBG.getColor());
            day = new Day(0, "", "", String.format("#%08X", (0xFFFFFFFF & cpBG.getColor())),
                    String.format("#%08X", (0xFFFFFFFF & cpTEXT.getColor())), "D-DAY", LocalDate.now(),
                    null, null);


            textViewDate.setText(day.getDate().toString());

            textViewCalculate.setText(CALCULATE_TYPE_SHOW[0]);
        }

    }

    public void clickDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        day.setDate(toLocalDate(year, monthOfYear, dayOfMonth));
                        textViewDate.setText(day.getDate().toString());
                        textViewDay.setText(day.getCalculationTime());
                    }

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void clickCalculate(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calculation Type");
        builder.setItems(CALCULATE_TYPE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected = CALCULATE_TYPE[which];
                day.setCalculate(selected);
                textViewCalculate.setText(CALCULATE_TYPE_SHOW[which]);
                textViewDay.setText(day.getCalculationTime());
                //Toast.makeText(getApplicationContext(), "Calculation Type: " + selected, Toast.LENGTH_SHORT).show();
            }
        });
        builder.create();
        builder.show();
    }


    public void clickImageButtonBackgroundColor(View view) {
        cpBG.setColor(Color.parseColor(day.getColorBackground()));

        /* Show color picker dialog */
        cpBG.show();

        cpBG.enableAutoClose(); // Enable auto-dismiss for the dialog


        /* Set a new Listener called when user click "select" */
        cpBG.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                // Do whatever you want
                // Examples
                Log.d("Alpha", Integer.toString(Color.alpha(color)));
                Log.d("Red", Integer.toString(Color.red(color)));
                Log.d("Green", Integer.toString(Color.green(color)));
                Log.d("Blue", Integer.toString(Color.blue(color)));

                Log.d("Pure Hex", Integer.toHexString(color));
                Log.d("#Hex no alpha", String.format("#%06X", (0xFFFFFF & color)));
                Log.d("#Hex with alpha", String.format("#%08X", (0xFFFFFFFF & color)));

                constraintLayoutBackground.setBackgroundColor(cpBG.getColor());
                day.setColorBackground(String.format("#%08X", (0xFFFFFFFF & color)));
                Toast.makeText(getApplicationContext(), "BACKGROUND COLOR: " + String.format("#%08X", (0xFFFFFFFF & color)), Toast.LENGTH_SHORT).show();


                // If the auto-dismiss option is not enable (disabled as default) you have to manually dismiss the dialog
                // cp.dismiss();
            }
        });
    }

    public void clickImageButtonTextColor(View view) {
        cpTEXT.setColor(Color.parseColor(day.getColorText()));

        /* Show color picker dialog */
        cpTEXT.show();

        cpTEXT.enableAutoClose(); // Enable auto-dismiss for the dialog


        /* Set a new Listener called when user click "select" */
        cpTEXT.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                // Do whatever you want
                // Examples
                Log.d("Alpha", Integer.toString(Color.alpha(color)));
                Log.d("Red", Integer.toString(Color.red(color)));
                Log.d("Green", Integer.toString(Color.green(color)));
                Log.d("Blue", Integer.toString(Color.blue(color)));

                Log.d("Pure Hex", Integer.toHexString(color));
                Log.d("#Hex no alpha", String.format("#%06X", (0xFFFFFF & color)));
                Log.d("#Hex with alpha", String.format("#%08X", (0xFFFFFFFF & color)));

                textViewDay.setTextColor(cpTEXT.getColor());
                textViewEventName.setTextColor(cpTEXT.getColor());
                day.setColorText(String.format("#%08X", (0xFFFFFFFF & color)));
                Toast.makeText(getApplicationContext(), "TEXT COLOR: " + String.format("#%08X", (0xFFFFFFFF & color)), Toast.LENGTH_SHORT).show();

                // If the auto-dismiss option is not enable (disabled as default) you have to manually dismiss the dialog
                // cp.dismiss();
            }
        });
    }

    public void clickImageButtonDelete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                sqLiteDatabase = databaseHelper.getWritableDatabase();
                sqLiteDatabase.delete("day", "id" + " = "+day.getId() , null);
                sqLiteDatabase.close();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void clickImageButtonComplete(View view) {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        if(day.getId() == 0) {
            day.setTimeCreate(LocalDateTime.now());
        }

        day.setName(editTextEventName.getText().toString());
        day.setDetail(editTextEventDetail.getText().toString());
        day.setTimeUpdate(LocalDateTime.now());

        ContentValues cv = new ContentValues();
        cv.put("name", day.getName());
        cv.put("detail", day.getDetail());
        cv.put("color_background", day.getColorBackground());
        cv.put("color_text", day.getColorText());
        cv.put("calculate", day.getCalculate());
        cv.put("date", day.getDate().toString());
        cv.put("time_create", day.getTimeCreate().toString());
        cv.put("time_update", day.getTimeUpdate().toString());

        if(day.getId() == 0) {
            sqLiteDatabase.insert("day", null, cv);
        }
        else {
            sqLiteDatabase.update("day", cv, "id" + " = "+day.getId() , null);
        }
        sqLiteDatabase.close();
        finish();
    }

    public void clickImageButtonCancel(View view) {
        finish();
    }

    private LocalDate toLocalDate(int year, int monthOfYear, int dayOfMonth) {
        String y, m, d, date;
        y = ""+year;
        if((monthOfYear + 1) < 10) m = "0"+(monthOfYear + 1);
        else m = ""+(monthOfYear + 1);
        if(dayOfMonth < 10) d = "0"+dayOfMonth;
        else d = ""+dayOfMonth;
        date = y+"-"+m+"-"+d;
        return LocalDate.parse(date);
    }
}
