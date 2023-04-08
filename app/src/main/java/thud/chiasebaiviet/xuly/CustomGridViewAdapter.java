package thud.chiasebaiviet.xuly;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.squareup.picasso.Picasso;

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
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_gridview, null);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.imageView);
            holder.textNoiDung = view.findViewById(R.id.textNoiDung);
            holder.textHoTen = view.findViewById(R.id.textHoten);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        BaiViet baiViet = baiVietList.get(position);
        //lấy họ tên người dùng
        String idNguoiDung = baiViet.getIdNguoiDung();
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.layTenNguoiDung(idNguoiDung, new FirebaseHelper.OnGetTenSuccessListener() {
            @Override
            public void onGetTenSuccess(String tenNguoiDung) {
                holder.textHoTen.setText(tenNguoiDung);
            }

            @Override
            public void onGetTenFailure(String errorMessage) {
                Log.e(TAG, "Error getting tenNguoiDung: " + errorMessage);
            }
        });

        holder.textNoiDung.setText(baiViet.getNoiDung());
        //lấy ảnh từ FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(baiViet.getImage());

        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Error loading image", exception);
            }
        });

        return view;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textNoiDung, textHoTen;
    }
}



