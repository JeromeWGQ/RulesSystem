package jerome.dao;

import jerome.entity.FileInf;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerome on 2017/2/22.
 */
public class FileDAO {

    /**
     * 获取某一目录下所有文件
     *
     * @param directory 和数据库myfileconn表中保持一致，0代表根目录
     */
    public static List<FileInf> getAll(int directory) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<FileInf> allFiles = null;
        try {
            allFiles = new ArrayList<FileInf>();
            conn = DBUtil.getConnection();
            // 获取要显示的文件列表
            ps = conn.prepareStatement("select * from myfile,myfileconn where myfile.id = myfileconn.childid and myfileconn.fatherid = " + directory);
            rs = ps.executeQuery();
            while (rs.next()) {
                FileInf file = new FileInf();
                file.id = rs.getInt("id");
                file.name = rs.getString("name");
                file.type = rs.getInt("type");
                file.fileid = rs.getInt("fileid");
                file.createtime = rs.getString("createtime");
                file.uid = rs.getInt("uid");
                allFiles.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return allFiles;
    }

    /**
     * 获取某一文件或目录的父目录id
     *
     * @param id
     * @return
     */
    public static String getFatherId(String id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            // 获取要显示的文件列表
            ps = conn.prepareStatement("select * from myfileconn where childid = " + id);
            rs = ps.executeQuery();
            rs.next();
            return rs.getString("fatherid");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return "0";
    }

    /**
     * 根据文件id获取文件信息
     *
     * @param id
     * @return
     */
    public static FileInf getFileInfById(String id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            // 获取要显示的文件列表
            ps = conn.prepareStatement("select * from myfile where id = " + id);
            rs = ps.executeQuery();
            rs.next();
            FileInf file = new FileInf();
            file.id = rs.getInt("id");
            file.name = rs.getString("name");
            file.type = rs.getInt("type");
            file.fileid = rs.getInt("fileid");
            file.createtime = rs.getString("createtime");
            file.uid = rs.getInt("uid");
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return null;
    }

    /**
     * 向myfile表中添加文件或分类信息
     *
     * @param name       文件或目录名
     * @param type       0代表分类，1代表文件
     * @param fileid     文件存储信息的id，目录则置null
     * @param createtime 创建时间
     * @param uid        创建用户的id
     * @return
     */
    public static int addFileRecord(String name, String type, String fileid, String createtime, String uid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("INSERT INTO myfile (name,type,fileid,createtime,uid) VALUES (?,?,?,?,?)");
            ps.setString(1, name);
            ps.setInt(2, Integer.parseInt(type));
            ps.setInt(3, fileid == null ? 0 : Integer.parseInt(fileid));
            ps.setString(4, createtime);
            ps.setInt(5, Integer.parseInt(uid));
            if (ps.executeUpdate() > 0) {
                ps = conn.prepareStatement("select * from myfile where name = '" + name + "' and createtime = '" + createtime + "' and uid = '" + uid + "'");
                rs = ps.executeQuery();
                rs.next();
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return -1;
    }

    public static boolean addFileConn(String fatherid, String childid) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("INSERT INTO myfileconn (fatherid,childid) VALUES (?,?)");
            ps.setInt(1, Integer.parseInt(fatherid));
            ps.setInt(2, Integer.parseInt(childid));
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return true;
    }

}
