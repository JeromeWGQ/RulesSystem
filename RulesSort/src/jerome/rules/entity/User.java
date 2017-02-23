package jerome.rules.entity;

/**
 * Created by Jerome on 2017/2/23.
 */
public class User {

    private int uid;            //用户ID
    private String uname;       //用户名
    private String password;    //密码
    private int access;         //权限

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }
}
