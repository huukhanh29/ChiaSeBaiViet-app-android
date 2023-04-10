package thud.chiasebaiviet.xuly;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.List;

import thud.chiasebaiviet.R;
import thud.chiasebaiviet.dulieu.BaiViet;


public class CustomGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<BaiViet> baiVietList;

    public CustomGridViewAdapter(Context context, List<BaiViet> baiVietList) {
        this.context = context;
        this.baiVietList = baiVietList;
    }
    public void setData(List<BaiViet> baiVietList) {
        this.baiVietList = baiVietList;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return baiVietList.size();
    }

    @Override
    public Object getItem(int position) {
        return baiVietList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_gridview, null);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.textNoiDung = convertView.findViewById(R.id.textNoiDung);
            holder.textHoTen = convertView.findViewById(R.id.textHoten);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BaiViet baiViet = baiVietList.get(position);
        //đặt ảnh mặc định khi ảnh bài viết chưa tải xong
        holder.imageView.setImageResource(R.drawable.gallery);
        setTenNguoiDung(holder.textHoTen, baiViet.getIdNguoiDung());
        setAnhBaiViet(holder.imageView, baiViet.getImage());
        holder.textNoiDung.setText(baiViet.getNoiDung());
        return convertView;
    }

    private void setTenNguoiDung(final TextView textView, String idNguoiDung) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.layTenNguoiDung(idNguoiDung, new FirebaseHelper.OnGetTenSuccessListener() {
            @Override
            public void onGetTenSuccess(String tenNguoiDung) {
                textView.setText(tenNguoiDung);
            }
            @Override
            public void onGetTenFailure(String errorMessage) {
                Log.e(TAG, "Error getting tenNguoiDung: " + errorMessage);
            }
        });
    }

    private void setAnhBaiViet(final ImageView imageView, String image) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(image);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Error loading image", exception);
                imageView.setImageResource(R.drawable.gallery);
            }
        });
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textNoiDung, textHoTen;
    }
}



