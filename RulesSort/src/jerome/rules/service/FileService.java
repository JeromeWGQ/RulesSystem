package jerome.rules.service;

import jerome.rules.dao.UserDAO;
import jerome.rules.entity.User;
import jerome.rules.dao.FileDAO;
import jerome.rules.entity.FileInf;
import jerome.rules.web.ActionServlet;

import javax.swing.*;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jerome on 2017/2/22.
 */
public class FileService {

    /**
     * 从FileDAO获取文件数据，并通过out写回至前端
     *
     * @param directory
     * @param out
     * @param access
     * @param type
     */
    public static void showFile(int directory, PrintWriter out, String access, String type) {
        String htmlCode = "";
        List<FileInf> fileList = FileDAO.getAll(directory);
        outLoop:
        switch (type) {
            case "0":
                // 0 代表类型为分类
                inLoop1:
                for (FileInf file : fileList) {
                    // 跳过全部文件
                    if (file.type == 1)
                        continue inLoop1;
                    UserDAO userDao = new UserDAO();
                    User user = new User();
                    try {
                        user = userDao.getByUid(file.uid);
                    } catch (Exception e) {
                        out.print("404");
                        e.printStackTrace();
                    }
                    if ("2".equals(access)) {
                        // 权限等级为2，有权限修改分类信息
                        htmlCode += "<tr>\n" +
                                "<td><div style=\"width: 300px;\" class=\"uk-text-truncate\"><a onclick=\"do_my_click(" + file.id + ",0)\">" + file.name + "</a></div></td>\n" +
                                "<td>" + user.getUname() + "</td>\n" +
                                "<td>" + file.createtime + "</td>\n" +
                                "<td><a class=\"uk-button uk-button-danger uk-button-mini\" onclick=\"do_delete('" + file.name + "','" + file.id + "');\">" + ActionServlet.DELETE + "</a><span>&nbsp;&nbsp;&nbsp;</span></td>\n" +
                                "</tr>";
                    } else {
                        // 权限等级为0或1，无权限修改分类信息
                        htmlCode += "<tr>\n" +
                                "<td><div style=\"width: 300px;\" class=\"uk-text-truncate\"><a onclick=\"do_my_click(" + file.id + ",0)\">" + file.name + "</a></div></td>\n" +
                                "<td>" + user.getUname() + "</td>\n" +
                                "<td>" + file.createtime + "</td>\n" +
                                "<td></td>\n" +
                                "</tr>";
                    }
                }
                if ("2".equals(access))
                    htmlCode += "<tr>\n" +
                            "<td><div style = \"width: 300px;\" class = \"uk-text-truncate\"><div id = \"new_sort_position\"><a onclick=\"new_sort_dialog()\">" + ActionServlet.NEW_SORT + "</a></div></div></td>\n" +
                            "<td/><td/><td/>\n" +
                            "</tr>";
                break outLoop;
            case "1":
                // 1 代表类型为文件
                inLoop2:
                for (FileInf file : fileList) {
                    // 跳过全部分类
                    if (file.type == 0)
                        continue inLoop2;
                    UserDAO userDao = new UserDAO();
                    User user = new User();
                    try {
                        user = userDao.getByUid(file.uid);
                    } catch (Exception e) {
                        out.print("404");
                        e.printStackTrace();
                    }
                    if ("2".equals(access)) {
                        // 权限等级为2，有权限浏览、下载、删除、修改文件
                        htmlCode += "<tr>\n" +
                                "<td><div style=\"width: 300px;\" class=\"uk-text-truncate\"><a onclick=\"do_my_click(" + file.id + ",1)\">" + file.name + "</a></div></td>\n" +
                                "<td>" + user.getUname() + "</td>\n" +
                                "<td>" + file.createtime + "</td>\n" +
                                "<td><a class=\"uk-button uk-button-success uk-button-mini\" href=\"download.do?fid=" + file.id + "\">" + ActionServlet.DOWNLOAD + "</a><span>&nbsp</span>\n" +
                                "<a class=\"uk-button uk-button-primary uk-button-mini\" onclick=\"do_delete('" + file.name + "','" + file.id + "');\">" + ActionServlet.MODIFY + "</a><span>&nbsp</span>\n" +
                                "<a class=\"uk-button uk-button-danger uk-button-mini\" onclick=\"do_delete('" + file.name + "','" + file.id + "');\">" + ActionServlet.DELETE + "</a><span>&nbsp;&nbsp;&nbsp;</span></td>\n" +
                                "</tr>";
                    } else {
                        // 权限等级为1或0，有权限浏览、下载文件
                        htmlCode += "<tr>\n" +
                                "<td><div style=\"width: 300px;\" class=\"uk-text-truncate\"><a onclick=\"do_my_click(" + file.id + ",1)\">" + file.name + "</a></div></td>\n" +
                                "<td>" + user.getUname() + "</td>\n" +
                                "<td>" + file.createtime + "</td>\n" +
                                "<td><a class=\"uk-button uk-button-success uk-button-mini\" onclick=\"do_delete('" + file.name + "','" + file.id + "');\">" + ActionServlet.DOWNLOAD + "</a><span>&nbsp;&nbsp;&nbsp;</span></td>\n" +
                                "</tr>";
                    }
                }
                break outLoop;
        }
        out.println(htmlCode);
    }

    /**
     * 获取某一目录的所有父节点列表，不包括其自身和根结点
     *
     * @param id
     * @return
     */
    public static List<FileInf> getSortList(String id) {
        if (id.equals("0"))
            return null;
        List<FileInf> resultList = new ArrayList<>();
        String fId = id;
        while (true) {
            fId = FileDAO.getFatherId(fId);
            if (fId.equals("0"))
                return resultList;
            resultList.add(0, FileDAO.getFileInfById(fId));
        }
    }

    /**
     * 添加用户创建分类
     *
     * @param sortname
     * @param dir
     * @param uid
     * @return
     */
    public static boolean addSort(String sortname, String dir, String uid) {
        //获取系统时间作为文件存储时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateStr = sdf.format(new Date());
        int fileid = FileDAO.addFileRecord(sortname, "0", null, dateStr, uid);
        if (fileid == -1)
            return false;
        FileDAO.addFileConn(dir, fileid + "");
        return true;
    }
}
