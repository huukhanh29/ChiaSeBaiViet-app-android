package thud.chiasebaiviet.giaodien;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import thud.chiasebaiviet.R;
import thud.chiasebaiviet.dulieu.NguoiDung;
import thud.chiasebaiviet.xuly.FirebaseHelper;
import thud.chiasebaiviet.xuly.HashPassword;


public class DangKy extends AppCompatActivity {
    TextInputLayout layoutTaiKhoan, layoutMatKhau, layoutNhapLaiMatKhau, layoutHoTen;
    TextInputEditText edtTenDangNhap, edtMatKhau, edtNhapLaiMatKhau, edtHoTen;
    private FirebaseHelper firebaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangky);

        // Ánh xạ các thành phần giao diện
        layoutTaiKhoan = findViewById(R.id.layout_taikhoan);
        layoutHoTen = findViewById(R.id.layout_hoten);
        layoutMatKhau = findViewById(R.id.layout_matkhau);
        layoutNhapLaiMatKhau = findViewById(R.id.layout_xacnhanmatkhau);
        edtTenDangNhap = findViewById(R.id.edt_taikhoan);
        edtHoTen = findViewById(R.id.edt_hoten);
        edtMatKhau = findViewById(R.id.edt_matkhau);
        edtNhapLaiMatKhau = findViewById(R.id.edt_xacnhanmatkhau);
    }
    public void KiemTraDangKy(View view){
        firebaseHelper = new FirebaseHelper();
        // Lấy thông tin người dùng nhập vào
        String tenDangNhap = edtTenDangNhap.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();
        String nhapLaiMatKhau = edtNhapLaiMatKhau.getText().toString().trim();
        String hoTen = edtHoTen.getText().toString().trim();
        // Kiểm tra thông tin nhập vào có hợp lệ hay không
        if (tenDangNhap.isEmpty()) {
            layoutTaiKhoan.setError("Vui lòng nhập tên đăng nhập");
            edtTenDangNhap.requestFocus();
            return;
        }else {
            layoutTaiKhoan.setError(null);
        }
        if (hoTen.isEmpty()) {
            layoutHoTen.setError("Vui lòng nhập họ tên");
            edtHoTen.requestFocus();
            return;
        } else {
            layoutHoTen.setError(null);
        }
        if (matKhau.isEmpty()) {
            layoutMatKhau.setError("Vui lòng nhập mật khẩu");
            edtMatKhau.requestFocus();
            return;
        } else {
            layoutMatKhau.setError(null);
        }
        if (nhapLaiMatKhau.isEmpty()) {
            layoutNhapLaiMatKhau.setError("Vui lòng nhập lại mật khẩu");
            edtNhapLaiMatKhau.requestFocus();
            return;
        } else if (!matKhau.equals(nhapLaiMatKhau)) {
            layoutNhapLaiMatKhau.setError("Mật khẩu không khớp");
            edtNhapLaiMatKhau.requestFocus();
            return;
        } else {
            layoutNhapLaiMatKhau.setError(null);
        }
        firebaseHelper.checkTenDangNhapTonTai(tenDangNhap, new FirebaseHelper.OnCheckListener() {

            @Override
            public void onCheck(boolean exists) {
                if (exists) {
                    // tenDangNhap đã tồn tại
                    layoutTaiKhoan.setError("Tên đăng nhập đã tồn tại");
                    edtTenDangNhap.requestFocus();
                } else {
                    // tenDangNhap chưa tồn tại
                    layoutTaiKhoan.setError(null);
                    // Nếu thông tin nhập vào hợp lệ, tiến hành đăng ký tài khoản
                    if (!tenDangNhap.isEmpty() && !matKhau.isEmpty() && !nhapLaiMatKhau.isEmpty()
                            && !hoTen.isEmpty() && matKhau.equals(nhapLaiMatKhau)) {
                        String hashedPassword = HashPassword.hash(matKhau);
                        NguoiDung nguoiDung = new NguoiDung(tenDangNhap, hoTen, hashedPassword);
                        firebaseHelper.addNguoiDung(nguoiDung);
                        Intent intent = new Intent(DangKy.this, DangNhap.class);
                        startActivity(intent);
                        Toast.makeText(DangKy.this, "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TrangChu.class);
        startActivity(intent);
        finish();
    }
}
