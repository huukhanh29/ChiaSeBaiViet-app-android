package thud.chiasebaiviet.dulieu;

public class BaiViet {
    private String maBv;
    private String noiDung;
    private String image;
    private String idNguoiDung;
    private Object timestamp;
    public BaiViet() {}

    public BaiViet(String maBv, String noiDung, String image, String idNguoiDung) {
        this.maBv = maBv;
        this.noiDung = noiDung;
        this.image = image;
        this.idNguoiDung = idNguoiDung;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIdNguoiDung() {
        return idNguoiDung;
    }

    public String getMaBv() {
        return maBv;
    }

    public void setMaBv(String maBv) {
        this.maBv = maBv;
    }

    public void setIdNguoiDung(String idNguoiDung) {
        this.idNguoiDung = idNguoiDung;
    }
}
