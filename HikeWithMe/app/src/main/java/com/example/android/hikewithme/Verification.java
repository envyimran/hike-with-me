package com.example.android.hikewithme;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Verification extends AppCompatActivity {

    EditText etFirstName, etLastName, etEmail;
    Button btnUploadID;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification);

        etFirstName = findViewById(R.id.editTextFirstName);
        etLastName = findViewById(R.id.editTextLastName);
        etEmail = findViewById(R.id.editTextEmail);
        btnUploadID = findViewById(R.id.btnUploadID);

        btnUploadID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(Verification.this, btnUploadID);
                popupMenu.getMenuInflater().inflate(R.menu.upload_options_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_camera:
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                return true;
                            case R.id.menu_gallery:
                                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
                                popupMenu.dismiss();
                                return true;
                        }
                        return false;
                    }
                });

// Show the popup menu
                popupMenu.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload_options_menu, menu);
        return true;
    }

    public void submitForm(View view) {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (firstName.isEmpty()) {
            etFirstName.setError("Please enter your first name");
            etFirstName.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            etLastName.setError("Please enter your last name");
            etLastName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Please enter your email address");
            etEmail.requestFocus();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*"); // Set the MIME type to any format
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"imranrbusiness@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Verification Form Submission");
        intent.putExtra(Intent.EXTRA_TEXT, "First Name: " + firstName + "\nLast Name: " + lastName + "\nEmail: " + email);
        ImageView imageView = findViewById(R.id.imageViewID);
        if (imageView.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Verification Photo", null);
            Uri bitmapUri = Uri.parse(bitmapPath);
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        }

        new AlertDialog.Builder(this)
                .setTitle("Submit Form")
                .setMessage("Are you sure you want to submit the form?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    startActivity(Intent.createChooser(intent, "Send email"));
                    Toast.makeText(this, "Form submitted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ImageView imageView = findViewById(R.id.imageViewID);
                imageView.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImage = data.getData();
                ImageView imageView = findViewById(R.id.imageViewID);
                imageView.setImageURI(selectedImage);
            }
        }
    }
}