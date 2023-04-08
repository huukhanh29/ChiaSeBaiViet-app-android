package thud.chiasebaiviet.xuly;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import thud.chiasebaiviet.dulieu.BaiViet;
import thud.chiasebaiviet.dulieu.NguoiDung;

public class FirebaseHelper {
    private DatabaseReference mDatabase;
    public FirebaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void layBaiVietByMaBV(String maBv, OnGetOneBaiViet listener) {
        Query query = mDatabase.child("BaiViet").orderByChild("maBv").equalTo(maBv);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        BaiViet baiViet = dataSnapshot.getValue(BaiViet.class);
                        listener.onGetBaiVietSuccess(baiViet);
                    }
                } else {
                    listener.onGetBaiVietFailure("Không tìm thấy bài viết");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onGetBaiVietFailure(error.getMessage());
            }
        });
    }
    public interface OnGetOneBaiViet {
        void onGetBaiVietSuccess(BaiViet baiViet);
        void onGetBaiVietFailure(String errorMessage);
    }

    public void layDanhSachBaiVietNguoiDung(String idNguoiDung, final OnGetBaiVietSuccessListener listener) {
        Query query = mDatabase.child("BaiViet").orderByChild("idNguoiDung").equalTo(idNguoiDung);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<BaiViet> listBaiViet = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BaiViet baiViet = snapshot.getValue(BaiViet.class);
                    listBaiViet.add(baiViet);
                }
                listener.onGetBaiVietSuccess(listBaiViet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onGetBaiVietFailure(databaseError.getMessage());
            }
        });
    }


    public void layDanhSachBaiViet(final OnGetBaiVietSuccessListener listener) {
        mDatabase.child("BaiViet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BaiViet> listBaiViet = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BaiViet baiViet = dataSnapshot.getValue(BaiViet.class);
                    listBaiViet.add(baiViet);
                }
                listener.onGetBaiVietSuccess(listBaiViet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onGetBaiVietFailure(error.getMessage());
            }
        });
    }

    public interface OnGetBaiVietSuccessListener {
        void onGetBaiVietSuccess(List<BaiViet> listBaiViet);
        void onGetBaiVietFailure(String errorMessage);
    }

    public void addNguoiDung(NguoiDung nguoiDung) {
        mDatabase.child("NguoiDung").push().setValue(nguoiDung);
    }

    public void ktDangNhap(String tenDangNhap, String matKhau, final OnCheckListener listener) {
        Query query = mDatabase.child("NguoiDung")
                .orderByChild("tenDangNhap")
                .equalTo(tenDangNhap);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = false;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String matKhauDB = child.child("matKhau").getValue(String.class);
                    if (matKhauDB != null && matKhauDB.equals(matKhau)) {
                        exists = true;
                        break;
                    }
                }
                if (listener != null) {
                    listener.onCheck(exists);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle error
            }
        });
    }
    public void checkTenDangNhapTonTai(String tenDangNhap, final OnCheckListener listener) {
        Query query = mDatabase.child("NguoiDung").orderByChild("tenDangNhap").equalTo(tenDangNhap);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = dataSnapshot.exists();

                if (listener != null) {
                    listener.onCheck(exists);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle error
            }
        });
    }
    public interface OnCheckListener {
        void onCheck(boolean exists);
    }

    public void layKeyNguoiDung(String tenDangNhap, final OnGetKeySuccessListener listener) {
        Query query = mDatabase.child("NguoiDung").orderByChild("tenDangNhap").equalTo(tenDangNhap);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String key = dataSnapshot.getChildren().iterator().next().getKey();
                    listener.onGetKeySuccess(key);
                } else {
                    listener.onGetKeyFailure("Khong tim thay nguoi dung");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onGetKeyFailure(databaseError.getMessage());
            }
        });
    }
    public void layKeyBaiViet(String maBv, final OnGetKeySuccessListener listener) {
        Query query = mDatabase.child("BaiViet").orderByChild("maBv").equalTo(maBv);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String key = dataSnapshot.getChildren().iterator().next().getKey();
                    listener.onGetKeySuccess(key);
                } else {
                    listener.onGetKeyFailure("Khong tim thay bai viet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onGetKeyFailure(databaseError.getMessage());
            }
        });
    }

    public interface OnGetKeySuccessListener {
        void onGetKeySuccess(String key);
        void onGetKeyFailure(String errorMessage);
    }
    public void layTenNguoiDung(String idNguoiDung, final OnGetTenSuccessListener listener) {
        Query query = mDatabase.child("NguoiDung").orderByKey().equalTo(idNguoiDung);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String tenNguoiDung = dataSnapshot.child(idNguoiDung)
                            .child("hoTen").getValue(String.class);
                    listener.onGetTenSuccess(tenNguoiDung);
                } else {
                    listener.onGetTenFailure("Khong tim thay nguoi dung");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onGetTenFailure(databaseError.getMessage());
            }
        });
    }

    public interface OnGetTenSuccessListener {
        void onGetTenSuccess(String tenNguoiDung);
        void onGetTenFailure(String errorMessage);
    }


    public void addBaiViet(BaiViet baiViet) {
        mDatabase.child("BaiViet").push().setValue(baiViet);
    }


    public void updateBaiViet(String key, BaiViet baiViet) {
        mDatabase.child("BaiViet").child(key).setValue(baiViet);
    }

    public void deleteBaiViet(String key) {
        mDatabase.child("BaiViet").child(key).removeValue();
    }

    public Query getNguoiDungQuery() {
        return mDatabase.child("NguoiDung").orderByChild("hoTen");
    }

    public Query getBaiVietQuery() {
        return mDatabase.child("BaiViet").orderByChild("timestamp");
    }
}

