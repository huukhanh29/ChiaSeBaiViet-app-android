package thud.chiasebaiviet.giaodien;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trangchu);
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        isLoggedIn = preferences.contains("tenDangNhap") && preferences.contains("matKhau");
        gridView = findViewById(R.id.gridView);
        String idNguoiDung = preferences.getString("idNguoiDung", null);
        // Lấy danh sách các bài viết từ CSDL
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.layDanhSachBaiVietNguoiDung(idNguoiDung, new FirebaseHelper.OnGetBaiVietSuccessListener() {
            @Override
            public void onGetBaiVietSuccess(List<BaiViet> listBaiViet) {
                baiVietList = listBaiViet;
                adapter = new CustomGridViewAdapter(BaiVietNguoiDung.this, baiVietList);
                gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onGetBaiVietFailure(String errorMessage) {
                // Xử lý khi lấy danh sách bài viết thất bại ở đây
            }
        });
        // Tạo adapter và thiết lập cho GridView
        adapter = new CustomGridViewAdapter(this, baiVietList);
        gridView.setAdapter(adapter);
        Button btnThemBaiViet = findViewById(R.id.btnThemBaiViet);
        if (isLoggedIn) {
            btnThemBaiViet.setVisibility(View.VISIBLE);
            btnThemBaiViet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Mở màn hình thêm bài viết
                    Intent intent = new Intent(BaiVietNguoiDung.this, ThemBaiViet.class);
                    startActivity(intent);
                }
            });
        } else {
            btnThemBaiViet.setVisibility(View.GONE);
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy bài viết được click
                BaiViet baiviet = (BaiViet) parent.getItemAtPosition(position);
                // Kiểm tra xem bài viết này thuộc về người dùng đang đăng nhập hay không
                if (baiviet.getIdNguoiDung().equals(idNguoiDung)) {
                    // Mở màn hình chỉnh sửa bài viết
                    Intent intent = new Intent(BaiVietNguoiDung.this, ChinhSuaBaiViet.class);
                    intent.putExtra("maBaiViet", baiviet.getMaBv());
                    startActivity(intent);
                    finish();
                }

            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BaiVietNguoiDung.this, TrangChu.class);
        startActivity(intent);
        finish();
    }
}
