package thud.chiasebaiviet.dulieu;

public class NguoiDung {
    private String tenDangNhap;
    private String hoTen;
    private String matKhau;

    public NguoiDung(String tenDangNhap,String hoTen, String matKhau) {
        this.tenDangNhap = tenDangNhap;
        this.hoTen = hoTen;
        this.matKhau = matKhau;
    }
    public NguoiDung(String tenDangNhap,String hoTen) {
        this.tenDangNhap = tenDangNhap;
        this.hoTen = hoTen;
    }
    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }


    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
}
