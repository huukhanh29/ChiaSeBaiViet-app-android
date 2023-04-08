package thud.chiasebaiviet.giaodien;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import thud.chiasebaiviet.MainActivity;
import thud.chiasebaiviet.R;
import thud.chiasebaiviet.xuly.FirebaseHelper;
import thud.chiasebaiviet.xuly.HashPassword;

public class DangNhap extends AppCompatActivity {
    TextInputLayout layoutTaiKhoan, layoutMatKhau;
    TextInputEditText edtTenDangNhap, edtMatKhau;
    String tenDangNhap, matKhau;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangnhap);
        // Ánh xạ các thành phần giao diện
        layoutTaiKhoan = findViewById(R.id.layout_taikhoan);
        layoutMatKhau = findViewById(R.id.layout_matkhau);
        edtTenDangNhap = findViewById(R.id.edt_taikhoan);
        edtMatKhau = findViewById(R.id.edt_matkhau);

        // Lấy thông tin tài khoản đăng nhập từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        tenDangNhap = preferences.getString("tenDangNhap", null);
        matKhau = preferences.getString("matKhau", null);

        // Kiểm tra nếu đã lưu thông tin đăng nhập trong SharedPreferences, tự động đăng nhập
        if (tenDangNhap != null && matKhau != null) {
            //nếu có thông tin đã lưu thì chuyển đến trang chủ
            xacThucDangNhap();
        }
    }

    public void KiemTraDangNhap(View view){
        tenDangNhap = edtTenDangNhap.getText().toString().trim();
        matKhau = edtMatKhau.getText().toString().trim();
        if (tenDangNhap.isEmpty()) {
            layoutTaiKhoan.setError("Vui lòng nhập tên đăng nhập");
            edtTenDangNhap.requestFocus();
            return;
        } else {
            layoutTaiKhoan.setError(null);
        }

        if (matKhau.isEmpty()) {
            layoutMatKhau.setError("Vui lòng nhập mật khẩu");
            edtMatKhau.requestFocus();
            return;
        } else {
            layoutMatKhau.setError(null);
        }
        // Kiểm tra đăng nhập
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.ktDangNhap(tenDangNhap, HashPassword.hash(matKhau), new FirebaseHelper.OnCheckListener() {
            @Override
            public void onCheck(boolean exists) {
                if (exists) {
                    FirebaseHelper firebaseHelper = new FirebaseHelper();
                    firebaseHelper.layKeyNguoiDung(tenDangNhap, new FirebaseHelper.OnGetKeySuccessListener() {
                        @Override
                        public void onGetKeySuccess(String ten) {
                            SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("idNguoiDung", ten);
                            editor.putString("tenDangNhap", tenDangNhap);
                            editor.putString("matKhau", HashPassword.hash(matKhau));
                            editor.apply();
                            xacThucDangNhap();
                        }
                        @Override
                        public void onGetKeyFailure(String errorMessage) {
                            Toast.makeText(DangNhap.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(DangNhap.this,
                            "Tên đăng nhập hoặc mật khẩu không chính xác",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void xacThucDangNhap() {
        // Chuyển sang màn hình chính
        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(DangNhap.this, TrangChu.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TrangChu.class);
        startActivity(intent);
        finish();
    }
}