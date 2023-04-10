package thud.chiasebaiviet.giaodien;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import thud.chiasebaiviet.R;

public abstract class QuanLyBaiViet extends AppCompatActivity {
    protected EditText edtNoidung;
    protected TextInputLayout layoutNoidung, layoutHinh;
    protected ImageView imgHinh;
    protected static Bitmap bitmap = null;
    protected Button btnXoa;
    protected ActivityResultLauncher<Intent> runCamera;
    protected ActivityResultLauncher<Intent> runGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baiviet);
        // Tạo logo cho action bar
        ActionBar myActionBar = getSupportActionBar();
        myActionBar.setDisplayShowHomeEnabled(true);
        myActionBar.setIcon(R.drawable.ic_gallery);
        btnXoa = findViewById(R.id.btn_xoa);
        edtNoidung = findViewById(R.id.edt_ndbaiviet);
        imgHinh = findViewById(R.id.img_hinhanh);
        layoutNoidung = findViewById(R.id.layout_ndbaiviet);
        layoutHinh = findViewById(R.id.layout_hinhanh);

        runCamera = registerForActivityResult(new
                        ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            bitmap = (Bitmap)
                                    result.getData().getExtras().get("data");
                            imgHinh.setImageBitmap(bitmap);
                        } else
                            Toast.makeText(QuanLyBaiViet.this,
                                    "Lỗi chụp hình!", Toast.LENGTH_LONG).show();
                    }
                });

        runGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            if (result.getData() != null) {
                                Uri contentURI = result.getData().getData();
                                try {
                                    bitmap = MediaStore.Images.Media.
                                            getBitmap(getContentResolver(),
                                                    contentURI);
                                    imgHinh.setImageBitmap(bitmap);
                                } catch (IOException e) {
                                }
                            }
                        } else
                            Toast.makeText(QuanLyBaiViet.this, "Lỗi chọn hình!",
                                    Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void HinhAnh(View view) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Thực hiện");
        String[] pictureDialogItems = {
                " Chọn hình có sẵn",
                " Chụp hình mới"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent galleryIntent = new Intent(
                                        Intent.ACTION_PICK,
                                        MediaStore.Images.
                                                Media.EXTERNAL_CONTENT_URI);
                                runGallery.launch(galleryIntent);
                                break;
                            case 1:
                                Intent intent = new Intent(
                                        MediaStore.
                                                ACTION_IMAGE_CAPTURE);
                                runCamera.launch(intent);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
}