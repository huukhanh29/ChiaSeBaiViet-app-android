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
    FirebaseHelper firebaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangnhap);
        firebaseHelper = new FirebaseHelper();
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
            XacThucDangNhap();
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
        // Kiểm tra thông tin đăng nhập
        KiemTraThongTin(tenDangNhap, matKhau);
    }

    private void KiemTraThongTin(String tenDangNhap, String matKhau) {
        firebaseHelper.ktDangNhap(tenDangNhap, HashPassword.hash(matKhau), new FirebaseHelper.OnCheckListener() {
            @Override
            public void onCheck(boolean exists) {
                if (exists) {
                    //Nếu đúng thông tin sẽ lưu thông tin vào SharedPreferences
                    LuuThongTin(tenDangNhap, matKhau);
                } else {
                    Toast.makeText(DangNhap.this,
                            "Tên đăng nhập hoặc mật khẩu không chính xác",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void LuuThongTin(String tenDangNhap, String matKhau) {
        //lấy ra key của người dùng
        firebaseHelper.layKeyNguoiDung(tenDangNhap, new FirebaseHelper.OnGetKeySuccessListener() {
            @Override
            public void onGetKeySuccess(String key) {
                //lưu vào SharedPreferences
                SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("idNguoiDung", key);
                editor.putString("tenDangNhap", tenDangNhap);
                editor.putString("matKhau", HashPassword.hash(matKhau));
                editor.apply();
                //đăng nhập thành công
                XacThucDangNhap();
            }
            @Override
            public void onGetKeyFailure(String errorMessage) {
                Toast.makeText(DangNhap.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void XacThucDangNhap() {
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