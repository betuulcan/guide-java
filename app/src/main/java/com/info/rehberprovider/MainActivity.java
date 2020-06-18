package com.info.rehberprovider;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button buttonKaydet, buttonSil, buttonGuncelle, buttonGetir;
    private TextView textViewCikti;
    private EditText editTextid, editTextAd, editTextTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewCikti = findViewById(R.id.textviewCikti);
        buttonGetir = findViewById(R.id.buttonGetir);
        buttonGuncelle = findViewById(R.id.buttonGuncelle);
        buttonKaydet = findViewById(R.id.buttonKaydet);
        buttonSil = findViewById(R.id.buttonSil);
        editTextid = findViewById(R.id.editTextId);
        editTextAd = findViewById(R.id.editTextAd);
        editTextTel = findViewById(R.id.editTextTel);

        buttonKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("ad", editTextAd.getText().toString());
                values.put("tel", editTextTel.getText().toString());

                Uri uri = getContentResolver().insert(MyProvider.CONTENT_URI, values);
                Toast.makeText(getApplicationContext(), "Rehber: " + uri.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        buttonGetir.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Cursor c = getContentResolver().query(MyProvider.CONTENT_URI, null, null, null);
                String result = "Rehber Sonuç: ";

                while (c.moveToNext()) {
                    result = result + "\n" + c.getInt(c.getColumnIndex("id"))
                            + "---" + c.getString(c.getColumnIndex("ad"))
                            + "---" + c.getString(c.getColumnIndex("tel"));
                }

                textViewCikti.setText(result);

            }
        });

        buttonSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://com.info.rehberprovider.MyProvider/rehber/" + editTextid.getText().toString());
                int count = getContentResolver().delete(uri, null, null);
            }
        });

        buttonGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("ad", editTextAd.getText().toString());
                values.put("tel", editTextTel.getText().toString());

                Uri uri = Uri.parse("content://com.info.rehberprovider.MyProvider/rehber/" + editTextid.getText().toString());
                int count = getContentResolver().update(uri, values, null, null);


            }
        });

    }
}
