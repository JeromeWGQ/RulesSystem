package jerome.service;

import dao.UserDAO;
import entity.User;
import jerome.dao.FileDAO;
import jerome.entity.FileInf;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jerome on 2017/2/22.
 */
public class FileService {

    public static void showFile(int directory, PrintWriter out, String access, String type) {
        String htmlCode = "";
        List<FileInf> fileList = FileDAO.getAll(directory);
        outloop:
        switch (type) {
            case "0":
                inloop1:
                for (FileInf file : fileList) {
                    if (file.type == 1)
                        continue inloop1;
                    UserDAO userDao = new UserDAO();
                    User user = new User();
                    try {
                        user = userDao.getByUid(file.uid);
                    } catch (Exception e) {
                        out.print("404");
                        e.printStackTrace();
                    }
                    if ("2".equals(access)) {
                        htmlCode += "" + "<tr>"
                                + "<td><div style=\"width: 300px;\" class=\"uk-text-truncate\"><a onclick=\"do_my_click(" + file.id + ",0)\">" + file.name + "</a></div></td>"
                                + "<td>" + user.getUname() + "</td>"
                                + "<td>" + file.createtime + "</td>"
                                + "<td>"
                                + "<a class=\"uk-button uk-button-danger uk-button-mini\" onclick=\"do_delete('" + file.name + "');\">" +
                                "删除" +
                                "</a>"
                                + "<span>&nbsp;&nbsp;&nbsp;</span>"
                                + "</td>"
                                + "</tr>";
                    } else {
                        htmlCode += "" + "<tr>"
                                + "<td><div style=\"width: 300px;\" class=\"uk-text-truncate\"><a onclick=\"do_my_click(" + file.id + ",0)\">" + file.name + "</a></div></td>"
                                + "<td>" + user.getUname() + "</td>"
                                + "<td>" + file.createtime + "</td>"
                                + "<td></td>"
                                + "</tr>";
                    }
                }
                htmlCode += "" + "<tr>"
                        + "<td><div style=\"width: 300px;\" class=\"uk-text-truncate\"><div id=\"new_sort_position\"><a onclick=\"new_sort_dialog()\">添加新分类</a></div></div></td>"
                        + "<td></td><td></td><td></td>"
                        + "</tr>";
                break outloop;
            case "1":
                inloop2:
                for (FileInf file : fileList) {
                    if (file.type == 0)
                        continue inloop2;
                    UserDAO userDao = new UserDAO();
                    User user = new User();
                    try {
                        user = userDao.getByUid(file.uid);
                    } catch (Exception e) {
                        out.print("404");
                        e.printStackTrace();
                    }
                    if ("2".equals(access)) {
                        htmlCode += "" + "<tr>"
                                + "<td><div style=\"width: 300px;\" class=\"uk-text-truncate\"><a onclick=\"do_my_click(" + file.id + ",1)\">" + file.name + "</a></div></td>"
                                + "<td>" + user.getUname() + "</td>"
                                + "<td>" + file.createtime + "</td>"
                                + "<td>"
                                + "<a class=\"uk-button uk-button-danger uk-button-mini\" onclick=\"do_delete('" + file.name + "');\">" +
                                "删除" +
                                "</a>"
                                + "<span>&nbsp;&nbsp;&nbsp;</span>"
                                + "<a class=\"uk-button uk-button-success uk-button-mini\" href=\"download.do?fname=" + file.name + "\">" +
                                "下载" +
                                "</a>"
                                + "</td>"
                                + "</tr>";
                    } else {
                        htmlCode += "" + "<tr>"
                                + "<td><div style=\"width: 300px;\" class=\"uk-text-truncate\"><a onclick=\"do_my_click(" + file.id + ",1)\">" + file.name + "</a></div></td>"
                                + "<td>" + user.getUname() + "</td>"
                                + "<td>" + file.createtime + "</td>"
                                + "<td>"
                                + "<a class=\"uk-button uk-button-success uk-button-mini\" href=\"download.do?fname=" + file.name + "\">" +
                                "下载" +
                                "</a>"
                                + "</td>"
                                + "</tr>";
                    }
                }
                break outloop;
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
        // TODO: 2017/2/22 明早从这里做起，还有FileDao里的两个方法
        List<FileInf> resultList = new ArrayList<>();
        String fId = id;
        while (true) {
            fId = FileDAO.getFatherId(fId);
            if (fId.equals("0"))
                return resultList;
            resultList.add(0, FileDAO.getFileInfById(fId));
        }
    }

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
