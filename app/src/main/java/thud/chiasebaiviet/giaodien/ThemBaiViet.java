package thud.chiasebaiviet.giaodien;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import thud.chiasebaiviet.dulieu.BaiViet;
import thud.chiasebaiviet.xuly.FirebaseHelper;

public class ThemBaiViet extends QuanLyBaiViet {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnXoa.setVisibility(View.GONE);
    }
    public void LuuBaiViet(View view) {
        if (!KiemTraBaiViet()) {
            return;
        }

        // Lưu ảnh lên Firebase Storage
        String tenAnh = "anh_" + System.currentTimeMillis() + ".jpg";
        // Lưu thông tin bài viết vào Firebase Database
        LuuBaiVietVaoFirebase(tenAnh);
        LuuAnhLenFirebaseStorage(tenAnh);
    }
    private boolean KiemTraBaiViet() {
        String nDung = edtNoidung.getText().toString().trim();
        if (nDung.length() < 1) {
            edtNoidung.requestFocus();
            edtNoidung.selectAll();
            layoutNoidung.setError("Nội dung bài viết không được rỗng!");
            return false;
        } else {
            layoutNoidung.setError(null);
        }
        if (bitmap == null) {
            layoutHinh.setError("Chưa chọn hình!");
            return false;
        } else {
            layoutHinh.setError(null);
        }
        return true;
    }
    private void LuuBaiVietVaoFirebase(String tenAnh) {
        SharedPreferences sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String idNguoiDung = sharedPref.getString("idNguoiDung", null);
        // Lưu tên ảnh vào cơ sở dữ liệu
        String maBv = "BV_" +System.currentTimeMillis();
        String nDung = edtNoidung.getText().toString().trim();
        BaiViet baiviet = new BaiViet(maBv, nDung, tenAnh, idNguoiDung);
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.addBaiViet(baiviet);
    }
    private void LuuAnhLenFirebaseStorage(String tenAnh) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference anhRef = storageRef.child(tenAnh);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = anhRef.putBytes(data);
        // Thêm callback để xử lý kết quả upload
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ThemBaiViet.this, "Lưu bài viết thành công!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ThemBaiViet.this, TrangChu.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ThemBaiViet.this, "Lỗi khi lưu ảnh!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ThemBaiViet.this, BaiVietNguoiDung.class);
        startActivity(intent);
        finish();
    }
}