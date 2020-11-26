package th.ku.memo.activity;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import th.ku.memo.R;
import th.ku.memo.model.DatabaseHelper;
import th.ku.memo.model.Memo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;


public class MemoActivity extends AppCompatActivity {
    private Memo memo;
    private static final String[] TEXT_ALIGNMENT = {"Top-Left-aligned", "Top-Centered", "Top-Right-aligned",
            "Center-Left-aligned", "Center-Centered", "Center-Right-aligned"};
    private String mSelected;

    ImageButton imageButtonBackgroundColor, imageButtonTextSize, imageButtonTextAlignment,
            imageButtonTextColor, imageButtonDelete, imageButtonInformation,
            imageButtonNotification, imageButtonShare, imageButtonComplete, imageButtonCancel;
    ConstraintLayout constraintLayoutBackground;
    EditText editTextTextMultiLine;
    ColorPicker cpTEXT, cpBG;
    DatabaseHelper databaseHelper;
    SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_memo);
        databaseHelper = new DatabaseHelper(this);

        imageButtonBackgroundColor = findViewById(R.id.imageButtonBackgroundColor);
        imageButtonTextSize = findViewById(R.id.imageButtonTextSize);
        imageButtonTextAlignment = findViewById(R.id.imageButtonTextAlignment);
        imageButtonTextColor = findViewById(R.id.imageButtonTextColor);
        imageButtonDelete = findViewById(R.id.imageButtonDelete);
        imageButtonInformation = findViewById(R.id.imageButtonInformation);
        imageButtonNotification = findViewById(R.id.imageButtonNotification);
        imageButtonShare = findViewById(R.id.imageButtonShare);
        imageButtonComplete = findViewById(R.id.imageButtonComplete);
        imageButtonCancel = findViewById(R.id.imageButtonCancel);

        constraintLayoutBackground = findViewById(R.id.constraintLayoutBackground);
        editTextTextMultiLine = findViewById(R.id.editTextTextMultiLine);

        Random rnd = new Random();
        cpTEXT = new ColorPicker(MemoActivity.this, 255, 0, 0, 0);
        cpBG = new ColorPicker(MemoActivity.this, 60, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            memo = (Memo) bundle.get("memo");

            sqLiteDatabase = databaseHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + "memo"
                    + " WHERE " + "id="+memo.getId(), null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                memo = new Memo(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                        LocalDateTime.parse(cursor.getString(6)), LocalDateTime.parse(cursor.getString(7)));
            }
            else {
                memo.setId(0);
            }
            sqLiteDatabase.close();

            editTextTextMultiLine.setText(memo.getText());
            editTextTextMultiLine.setTextSize(memo.getTextSize());
            editTextTextMultiLineSetAlignment();
            editTextTextMultiLine.setTextColor(Color.parseColor(memo.getColorText()));
            constraintLayoutBackground.setBackgroundColor(Color.parseColor(memo.getColorBackground()));

            cpTEXT.setColor(Color.parseColor(memo.getColorText()));
            cpBG.setColor(Color.parseColor(memo.getColorBackground()));
        }
        else {
            editTextTextMultiLine.setTextSize(18);
            editTextTextMultiLine.setTextColor(cpTEXT.getColor());
            constraintLayoutBackground.setBackgroundColor(cpBG.getColor());
            memo = new Memo(0, "", String.format("#%08X", (0xFFFFFFFF & cpBG.getColor())),
                    String.format("#%08X", (0xFFFFFFFF & cpTEXT.getColor())), "Center-Centered",
                    18, null, null);
        }
    }

    public void clickImageButtonBackgroundColor(View view) {
        cpBG.setColor(Color.parseColor(memo.getColorBackground()));

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
                memo.setColorBackground(String.format("#%08X", (0xFFFFFFFF & color)));
                Toast.makeText(getApplicationContext(), "BACKGROUND COLOR: " + String.format("#%08X", (0xFFFFFFFF & color)), Toast.LENGTH_SHORT).show();


                // If the auto-dismiss option is not enable (disabled as default) you have to manually dismiss the dialog
                // cp.dismiss();
            }
        });
    }

    public void clickImageButtonTextSize(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select TEXT SIZE");
        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);
        numberPicker.setValue(memo.getTextSize());
        builder.setView(numberPicker);
        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int size = numberPicker.getValue();
                Toast.makeText(getApplicationContext(), "TEXT SIZE: " + size, Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                memo.setTextSize(size);
                editTextTextMultiLine.setTextSize(size);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create();

        // Showing Alert Message
        builder.show();
    }

    public void clickImageButtonTextAlignment(View view) {
        int check = 4;

        switch (memo.getAlignment()){
            case "Top-Left-aligned": check = 0; break;
            case "Top-Centered": check = 1; break;
            case "Top-Right-aligned": check = 2; break;
            case "Center-Left-aligned": check = 3; break;
            case "Center-Centered": check = 4; break;
            case "Center-Right-aligned": check = 5; break;
            default:
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select TEXT GRAVITY-ALIGNMENT");
        builder.setSingleChoiceItems(TEXT_ALIGNMENT, check, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSelected = TEXT_ALIGNMENT[which];
            }
        });
        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "TEXT GRAVITY-ALIGNMENT: " + mSelected, Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                memo.setAlignment(mSelected);
                editTextTextMultiLineSetAlignment();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create();
        builder.show();
    }

    public void editTextTextMultiLineSetAlignment() {
        switch (memo.getAlignment()){
            case "Top-Left-aligned":
                editTextTextMultiLine.setGravity(Gravity.TOP | Gravity.LEFT);
                break;
            case "Top-Centered":
                editTextTextMultiLine.setGravity(Gravity.CENTER | Gravity.TOP);
                break;
            case "Top-Right-aligned":
                editTextTextMultiLine.setGravity(Gravity.RIGHT | Gravity.TOP);
                break;
            case "Center-Left-aligned":
                editTextTextMultiLine.setGravity(Gravity.CENTER | Gravity.LEFT);
                break;
            case "Center-Centered":
                editTextTextMultiLine.setGravity(Gravity.CENTER);
                break;
            case "Center-Right-aligned":
                editTextTextMultiLine.setGravity(Gravity.CENTER | Gravity.RIGHT);
                break;
            default:
        }
    }

    public void clickImageButtonTextColor(View view) {
        cpTEXT.setColor(Color.parseColor(memo.getColorText()));

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

                editTextTextMultiLine.setTextColor(cpTEXT.getColor());
                memo.setColorText(String.format("#%08X", (0xFFFFFFFF & color)));
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
                sqLiteDatabase.delete("memo", "id" + " = "+memo.getId() , null);
                sqLiteDatabase.close();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void clickImageButtonInformation(View view) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String information;
        if(memo.getTimeCreate() == null) {
            information = "Create Date Time: "+"\n"+"Update Date Time: ";
        }
        else {
            information = "Create Date Time: "+memo.getTimeCreate().format(formatter)+"\n"
                    +"Update Date Time: "+memo.getTimeUpdate().format(formatter);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Memo Information");
        builder.setMessage(information);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void clickImageButtonNotification(View view) {
        Intent notificationIntent = new Intent(MemoActivity.this, MemoActivity.class);
        notificationIntent.putExtra("memo", memo);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(MemoActivity.this, memo.getId(), notificationIntent, 0);

        String title = "MEMO";
        String body = editTextTextMultiLine.getText().toString();

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MemoActivity.this, "CHANNEL_MEMO")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(Color.parseColor("#ffdaa3"))
                .setContentIntent(intent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(memo.getId(), builder.build());
    }


    public void clickImageButtonShare(View view) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share memo");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, editTextTextMultiLine.getText().toString());
        startActivity(Intent.createChooser(sharingIntent, "Sharing Option"));
    }

    public void clickImageButtonComplete(View view) {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        if(memo.getId() == 0) {
            memo.setTimeCreate(LocalDateTime.now());
        }

        memo.setText(editTextTextMultiLine.getText().toString());
        memo.setTimeUpdate(LocalDateTime.now());

        ContentValues cv = new ContentValues();
        cv.put("text", memo.getText());
        cv.put("color_background", memo.getColorBackground());
        cv.put("color_text", memo.getColorText());
        cv.put("alignment", memo.getAlignment());
        cv.put("text_size", memo.getTextSize());
        cv.put("time_create", memo.getTimeCreate().toString());
        cv.put("time_update", memo.getTimeUpdate().toString());

        if(memo.getId() == 0) {
            sqLiteDatabase.insert("memo", null, cv);
        }
        else {
            sqLiteDatabase.update("memo", cv, "id" + " = "+memo.getId() , null);
        }
        sqLiteDatabase.close();
        finish();
    }

    public void clickImageButtonCancel(View view) {
        finish();
    }
}
