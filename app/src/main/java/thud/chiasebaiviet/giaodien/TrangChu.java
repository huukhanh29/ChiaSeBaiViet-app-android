package thud.chiasebaiviet.giaodien;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import thud.chiasebaiviet.R;
import thud.chiasebaiviet.dulieu.BaiViet;
import thud.chiasebaiviet.xuly.CustomGridViewAdapter;
import thud.chiasebaiviet.xuly.FirebaseHelper;
public class TrangChu extends AppCompatActivity {

    private GridView gridView;
    private CustomGridViewAdapter adapter;
    private List<BaiViet> baiVietList = new ArrayList<>();
    private boolean isLoggedIn = false;
    static final int MA_BNNGOAI = 1;
    static final int MA_CAMERA = 2;
    static String[] DSQUYEN_BNNGOAI =
            {Manifest.permission.READ_EXTERNAL_STORAGE};
    static String[] DSQUYEN_CAMERA = {Manifest.permission.CAMERA};
    boolean QUYEN_BNNGOAI = false, QUYEN_CAMERA = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trangchu);
        //kiểm tra quyền truy cập vào camera và bộ nhớ
        KiemTraQuyenTruyCap();
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        isLoggedIn = preferences.contains("tenDangNhap") && preferences.contains("matKhau");
        gridView = findViewById(R.id.gridView);
        Button btnThemBaiViet = findViewById(R.id.btnThemBaiViet);
        if (isLoggedIn) {
            btnThemBaiViet.setVisibility(View.VISIBLE);
            btnThemBaiViet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Mở màn hình thêm bài viết
                    Intent intent = new Intent(TrangChu.this, ThemBaiViet.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            btnThemBaiViet.setVisibility(View.GONE);
        }
        // Lấy danh sách các bài viết từ CSDL
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.layDanhSachBaiViet(new FirebaseHelper.OnGetBaiVietSuccessListener() {
            @Override
            public void onGetBaiVietSuccess(List<BaiViet> listBaiViet) {
                baiVietList = listBaiViet;
                adapter = new CustomGridViewAdapter(TrangChu.this, baiVietList);
                gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onGetBaiVietFailure(String errorMessage) {
                // Xử lý khi lấy danh sách bài viết thất bại ở đây
            }
        });
    }
    private void KiemTraQuyenTruyCap() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, DSQUYEN_BNNGOAI, MA_BNNGOAI);
        }
        else QUYEN_BNNGOAI = true;

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, DSQUYEN_CAMERA, MA_CAMERA);
        }
        else QUYEN_CAMERA = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MA_BNNGOAI:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    QUYEN_BNNGOAI = true;
                } else {
                    Toast.makeText(this,
                            "Lỗi không có quyền thao tác trên tập tin!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case MA_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    QUYEN_CAMERA = true;
                } else {
                    Toast.makeText(this, "Lỗi không có quyền thao tác trên Camera!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem dangNhapMenuItem = menu.findItem(R.id.mnu_dangnhap);
        MenuItem dangKyMenuItem = menu.findItem(R.id.mnu_dangky);
        MenuItem bvctMenuItem = menu.findItem(R.id.mnu_bvct);
        if (isLoggedIn) {
            dangNhapMenuItem.setTitle("Đăng xuất");
            dangKyMenuItem.setVisible(false);
            bvctMenuItem.setVisible(true);
        } else {
            dangNhapMenuItem.setTitle("Đăng nhập");
            dangKyMenuItem.setVisible(true);
            bvctMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_dangnhap:
                if (isLoggedIn) {
                    dangXuat();
                } else {
                    Intent intent = new Intent(TrangChu.this, DangNhap.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            case R.id.mnu_dangky:
                Intent intent1 = new Intent(TrangChu.this, DangKy.class);
                startActivity(intent1);
                finish();
                return true;
            case R.id.mnu_bvct:
                Intent intent2 = new Intent(TrangChu.this, BaiVietNguoiDung.class);
                startActivity(intent2);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void dangXuat() {
        // Xoá thông tin đăng nhập trong SharedPreferences
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("tenDangNhap");
        editor.remove("matKhau");
        editor.remove("tenNguoiDung");
        editor.apply();
        isLoggedIn = false;
        Intent intent = new Intent(TrangChu.this, TrangChu.class);
        startActivity(intent);
        Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_LONG).show();
        finish();
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
