package thud.chiasebaiviet.giaodien;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import thud.chiasebaiviet.dulieu.BaiViet;
import thud.chiasebaiviet.xuly.FirebaseHelper;

public class ChinhSuaBaiViet extends QuanLyBaiViet {
    private BaiViet baiViet;
    private String keyBv;
    private FirebaseHelper firebaseHelper;
    String nDung, tenAnh;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnXoa.setVisibility(View.VISIBLE);
        firebaseHelper = new FirebaseHelper();
        //Lấy dữ liệu từ intent
        Intent intent = getIntent();
        String maBaiViet = intent.getStringExtra("maBaiViet");
        //lấy bài viết từ firebase
        LayBaiViet(maBaiViet);
    }
    private void LayBaiViet(String maBaiViet) {
        firebaseHelper.layBaiVietByMaBV(maBaiViet, new FirebaseHelper.OnGetOneBaiViet() {
            @Override
            public void onGetBaiVietSuccess(BaiViet bv) {
                baiViet = bv;
                edtNoidung.setText(baiViet.getNoiDung());
                //lấy ảnh từ FirebaseStorage
                StorageReference imageRef = storageRef.child(baiViet.getImage());
                final long ONE_MEGABYTE = 1024 * 1024;
                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imgHinh.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ChinhSuaBaiViet.this,
                                "Lấy ảnh thất bại!", Toast.LENGTH_LONG).show();
                    }
                });
                firebaseHelper.layKeyBaiViet(baiViet.getMaBv(), new FirebaseHelper.OnGetKeySuccessListener() {
                    @Override
                    public void onGetKeySuccess(String key) {
                        keyBv = key;
                    }
                    @Override
                    public void onGetKeyFailure(String errorMessage) {
                        Toast.makeText(ChinhSuaBaiViet.this, "Lỗi lấy key bài viết!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onGetBaiVietFailure(String errorMessage) {
                Toast.makeText(ChinhSuaBaiViet.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void LuuBaiViet(View view) {
        nDung = edtNoidung.getText().toString().trim();
        if (nDung.isEmpty()) {
            edtNoidung.requestFocus();
            edtNoidung.selectAll();
            layoutNoidung.setError("Nội dung bài viết không được rỗng!");
            return;
        }
        layoutNoidung.setError(null);

        tenAnh = baiViet.getImage();
        if (bitmap != null) {
            // Xóa ảnh cũ
            StorageReference anhRefOld = storageRef.child(baiViet.getImage());
            anhRefOld.delete();
            tenAnh = "anh_" + System.currentTimeMillis() + ".jpg";
            // Lưu ảnh mới
            StorageReference anhRef = storageRef.child(tenAnh);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = anhRef.putBytes(data);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Nếu lưu ảnh mới thành công, tiếp tục lưu bài viết
                        LuuBaiVietSauKhiLuuAnh();
                    } else {
                        Toast.makeText(ChinhSuaBaiViet.this, "Lỗi khi lưu ảnh!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            // Không có ảnh mới, chỉ lưu bài viết
            LuuBaiVietSauKhiLuuAnh();
        }
    }

    private void LuuBaiVietSauKhiLuuAnh() {
        SharedPreferences sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String idNguoiDung = sharedPref.getString("idNguoiDung", null);

        // Lưu tên ảnh vào cơ sở dữ liệu
        BaiViet baiviet = new BaiViet(baiViet.getMaBv(), nDung, tenAnh, idNguoiDung);
        firebaseHelper.updateBaiViet(keyBv, baiviet);

        Intent intent = new Intent(ChinhSuaBaiViet.this, BaiVietNguoiDung.class);
        startActivity(intent);
        Toast.makeText(ChinhSuaBaiViet.this, "Lưu bài viết thành công!", Toast.LENGTH_LONG).show();
        finish();
    }


    public void XoaBaiViet(View view) {
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.deleteBaiViet(keyBv);
        // Xóa ảnh trên Firebase Storage
        StorageReference anhRef = storageRef.child(baiViet.getImage());
        anhRef.delete();
        Intent intent1 = new Intent(ChinhSuaBaiViet.this, BaiVietNguoiDung.class);
        startActivity(intent1);
        Toast.makeText(this, "Xóa bài viết thành công", Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChinhSuaBaiViet.this, BaiVietNguoiDung.class);
        startActivity(intent);
        finish();
    }
}
