package thud.chiasebaiviet.giaodien;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import thud.chiasebaiviet.R;
import thud.chiasebaiviet.dulieu.BaiViet;
import thud.chiasebaiviet.xuly.CustomGridViewAdapter;
import thud.chiasebaiviet.xuly.FirebaseHelper;

public class BaiVietNguoiDung extends AppCompatActivity {
    private GridView gridView;
    private CustomGridViewAdapter adapter;
    private List<BaiViet> baiVietList = new ArrayList<>();
    private boolean isLoggedIn = false;
    private Button btnThemBaiViet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trangchu);
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        isLoggedIn = preferences.contains("tenDangNhap") && preferences.contains("matKhau");
        KhoiTaoView();
        // Tạo adapter và thiết lập cho GridView
        adapter = new CustomGridViewAdapter(this, baiVietList);
        gridView.setAdapter(adapter);
        String idNguoiDung = preferences.getString("idNguoiDung", null);
        // Lấy danh sách các bài viết của người dùng hiện tại từ CSDL
        LayDanhSachBaiVietNguoiDung(idNguoiDung);
        Button btnThemBaiViet = findViewById(R.id.btnThemBaiViet);
        btnThemBaiViet.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        btnThemBaiViet.setOnClickListener(v -> MoManHinhThemBaiViet());

        gridView.setOnItemClickListener((parent, view, position, id)
                -> XuLyClickGridView(position, idNguoiDung));
    }
    private void KhoiTaoView() {
        gridView = findViewById(R.id.gridView);
        btnThemBaiViet = findViewById(R.id.btnThemBaiViet);
        if (isLoggedIn) {
            btnThemBaiViet.setVisibility(View.VISIBLE);
            btnThemBaiViet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Mở màn hình thêm bài viết
                    Intent intent = new Intent(BaiVietNguoiDung.this, ThemBaiViet.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            btnThemBaiViet.setVisibility(View.GONE);
        }
    }
    private void LayDanhSachBaiVietNguoiDung(String idNguoiDung) {
        new Thread(() -> {
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.layDanhSachBaiVietNguoiDung(idNguoiDung, new FirebaseHelper.OnGetBaiVietSuccessListener() {
                @Override
                public void onGetBaiVietSuccess(List<BaiViet> listBaiViet) {
                    runOnUiThread(() -> {
                        baiVietList = listBaiViet;
                        adapter.setData(baiVietList);
                    });
                }
                @Override
                public void onGetBaiVietFailure(String errorMessage) {
                    // Xử lý khi lấy danh sách bài viết thất bại
                    Toast.makeText(BaiVietNguoiDung.this,
                            errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void XuLyClickGridView(int position, String idNguoiDung) {
        // Lấy bài viết được click
        BaiViet baiviet = (BaiViet) gridView.getItemAtPosition(position);

        // Kiểm tra xem bài viết này thuộc về người dùng đang đăng nhập hay không
        if (baiviet.getIdNguoiDung().equals(idNguoiDung)) {
            // Mở màn hình chỉnh sửa bài viết
            Intent intent = new Intent(BaiVietNguoiDung.this, ChinhSuaBaiViet.class);
            intent.putExtra("maBaiViet", baiviet.getMaBv());
            startActivity(intent);
            finish();
        }
    }

    private void MoManHinhThemBaiViet() {
        // Mở màn hình thêm bài viết
        Intent intent = new Intent(BaiVietNguoiDung.this, ThemBaiViet.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BaiVietNguoiDung.this, TrangChu.class);
        startActivity(intent);
        finish();
    }
}
