package jerome.rules.dao;

import jerome.rules.entity.FileInf;
import jerome.rules.util.DBUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    /**
     * 添加信息至fileconn表
     *
     * @param fatherid
     * @param childid
     * @return
     */
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
        return false;
    }

    /**
     * 将某文件或目录父目录置为-1，不删除数据，即放入回收站
     *
     * @param fileid
     * @return
     */
    public static boolean deleteFile(String fileid) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("update myfileconn set fatherid=-1 where childid=" + fileid);
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return false;
    }

    /**
     * 删除某文件的全部数据
     *
     * @param fileid
     * @return
     */
    public static boolean clearFile(String fileid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("select * from myfile where id=" + fileid);
            rs = ps.executeQuery();
            rs.next();
            String fId = rs.getString("fileid");
            ps = conn.prepareStatement("delete from myfile where id=" + fileid);
            if (ps.executeUpdate() > 0) {
                ps = conn.prepareStatement("delete from myfileconn where childid=" + fileid);
                if (ps.executeUpdate() > 0) {
                    ps = conn.prepareStatement("delete from myfiledata where fileid=" + fId);
                    if (ps.executeUpdate() > 0)
                        return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return false;
    }

    /**
     * 删除某目录的全部数据（仅本身）
     *
     * @param fileid
     * @return
     */
    public static boolean clearDir(String fileid) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("delete from myfile where id=" + fileid);
            if (ps.executeUpdate() > 0) {
                ps = conn.prepareStatement("delete from myfileconn where childid=" + fileid);
                if (ps.executeUpdate() > 0)
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return false;
    }

    /**
     * 列出某目录下的全部子文件
     *
     * @param fileid
     * @return
     */
    public static ArrayList<Integer> showSubFiles(String fileid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("select * from myfileconn where fatherid=" + fileid);
            rs = ps.executeQuery();
            ArrayList<Integer> resultList = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("childid");
                ps = conn.prepareStatement("select * from myfile where id = " + id);
                rs1 = ps.executeQuery();
                rs1.next();
                if (rs1.getInt("type") == 1)
                    resultList.add(id);
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return null;
    }

    /**
     * 列出某目录下的全部子目录
     *
     * @param fileid
     * @return
     */
    public static ArrayList<Integer> showSubDirs(String fileid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("select * from myfileconn where fatherid=" + fileid);
            rs = ps.executeQuery();
            ArrayList<Integer> resultList = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("childid");
                ps = conn.prepareStatement("select * from myfile where id = " + id);
                rs1 = ps.executeQuery();
                rs1.next();
                if (rs1.getInt("type") == 0)
                    resultList.add(id);
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return null;
    }

    /**
     * 根据文件名获取文件信息
     *
     * @param filename
     * @return
     */
    public static FileInf getFileInfByName(String filename) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("select * from myfile where name='" + filename + "'");
            rs = ps.executeQuery();
            if (rs.next()) {
                FileInf file = new FileInf();
                file.id = rs.getInt("id");
                file.name = rs.getString("name");
                file.type = rs.getInt("type");
                file.fileid = rs.getInt("fileid");
                file.createtime = rs.getString("createtime");
                file.uid = rs.getInt("uid");
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return null;
    }

    /**
     * 写入文件数据，并返回写入的id
     *
     * @param in
     * @return
     */
    public static int writeFileData(InputStream in) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            // 写入数据库
            String sql = "insert into myfiledata (filedata) values (?)";
            ps = conn.prepareStatement(sql);
            ps.setBlob(1, in);
            ps.executeUpdate();
            // 获取最大id并返回
            String sql1 = "select max(fileid) from myfiledata";
            ps = conn.prepareStatement(sql1);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return -1;
    }

}
