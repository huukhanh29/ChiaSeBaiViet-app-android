package thud.chiasebaiviet.giaodien;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
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
        // Tạo logo cho action bar
        ActionBar myActionBar = getSupportActionBar();
        myActionBar.setDisplayShowHomeEnabled(true);
        myActionBar.setIcon(R.drawable.ic_gallery);
        firebaseHelper = new FirebaseHelper();
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
    public void KiemTraDangKy(View view) {
        String tenDangNhap = edtTenDangNhap.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();
        String nhapLaiMatKhau = edtNhapLaiMatKhau.getText().toString().trim();
        String hoTen = edtHoTen.getText().toString().trim();

        if (!KiemTraInput(tenDangNhap, matKhau, nhapLaiMatKhau, hoTen)) {
            return;
        }

        KiemTraTenDangNhapTonTai(tenDangNhap);
    }

    private boolean KiemTraInput(String tenDangNhap, String matKhau,
                                    String nhapLaiMatKhau, String hoTen) {
        if (tenDangNhap.isEmpty() || tenDangNhap.length() < 6 || tenDangNhap.length() > 10) {
            layoutTaiKhoan.setError(tenDangNhap.isEmpty() ?
                    "Vui lòng nhập tên đăng nhập" : "Tên đăng nhập phải từ 6 đến 10 ký tự");
            edtTenDangNhap.requestFocus();
            return false;
        } else {
            layoutTaiKhoan.setError(null);
        }
        if (hoTen.isEmpty() || !hoTen.matches("[a-zA-Zà-ỹÀ-Ỹ ]+")) {
            layoutHoTen.setError(hoTen.isEmpty() ?
                    "Vui lòng nhập họ tên" : "Họ tên chỉ được chứa chữ cái và dấu cách");
            edtHoTen.requestFocus();
            return false;
        } else {
            layoutHoTen.setError(null);
        }

        if (matKhau.isEmpty() ||
                !matKhau.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{5,}$")) {
            layoutMatKhau.setError(matKhau.isEmpty() ?
                    "Vui lòng nhập mật khẩu" :
                    "Mật khẩu phải có ít nhất 5 kí tự và phải có chữ và số");
            edtMatKhau.requestFocus();
            return false;
        } else {
            layoutMatKhau.setError(null);
        }
        if (nhapLaiMatKhau.isEmpty() || !matKhau.equals(nhapLaiMatKhau)) {
            layoutNhapLaiMatKhau.setError(nhapLaiMatKhau.isEmpty() ?
                    "Vui lòng nhập lại mật khẩu" : "Mật khẩu không khớp");
            edtNhapLaiMatKhau.requestFocus();
            return false;
        } else {
            layoutNhapLaiMatKhau.setError(null);
        }
        return true;
    }

    private void KiemTraTenDangNhapTonTai(String tenDangNhap) {
        firebaseHelper.checkTenDangNhapTonTai(tenDangNhap, new FirebaseHelper.OnCheckListener() {
            @Override
            public void onCheck(boolean exists) {
                if (exists) {
                    layoutTaiKhoan.setError("Tên đăng nhập đã tồn tại");
                    edtTenDangNhap.requestFocus();
                } else {
                    layoutTaiKhoan.setError(null);
                    TienHanhDangKy();
                }
            }
        });
    }
    private void TienHanhDangKy() {
        String tenDangNhap = edtTenDangNhap.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();
        String hoTen = edtHoTen.getText().toString().trim();
        String hashedPassword = HashPassword.hash(matKhau);
        NguoiDung nguoiDung = new NguoiDung(tenDangNhap, hoTen, hashedPassword);
        firebaseHelper.addNguoiDung(nguoiDung);
        Intent intent = new Intent(DangKy.this, DangNhap.class);
        startActivity(intent);
        Toast.makeText(DangKy.this, "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TrangChu.class);
        startActivity(intent);
        finish();
    }
}
