package web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jerome.service.FileService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import dao.FileDAO;
import dao.UserDAO;
import entity.FileInf;
import entity.User;

/**
 * @author Snail
 */
public class ActionServlet extends HttpServlet {
    /**
     * ??????????????????????????????????????????
     */
    private static List<FileInf> allFileList = null;
    private static Runnable refreshFile = null;    // ?????FileService????????????
    private static String UPLOAD_PATH = "";

    public void init() throws ServletException {
        super.init();
        UPLOAD_PATH = this.getInitParameter("UPLOAD_PATH");
        refreshFile = new RefreshFile();
        Thread thread = new Thread(refreshFile);
        thread.start();
    }

    /**
     * Service?????????
     */
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        request.setCharacterEncoding("utf-8");
        String uri = request.getRequestURI();
        String action = uri.substring(uri.lastIndexOf("/"), uri.lastIndexOf("."));

        /**
         * ???
         */
        if ("/login".equals(action)) {
            PrintWriter out = response.getWriter();
            try {
                String username = (String) request.getParameter("username");
                String password = (String) request.getParameter("password");
                UserDAO userDao = new UserDAO();
                User user = userDao.getByUname(username);
                if (user.getPassword().equals(password)) {
                    HttpSession session = request.getSession();
                    session.setMaxInactiveInterval(24 * 60 * 60);
                    session.setAttribute("sessionID", session.getId());
                    session.setAttribute("access", user.getAccess() + "");
                    session.setAttribute("uid", user.getUid());
                    session.setAttribute("Path", UPLOAD_PATH);
                    out.println("1");
                    Thread thread = new Thread(refreshFile);
                    thread.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * ???????
         */
        if ("/modifypwd".equals(action)) {
            PrintWriter out = response.getWriter();
            try {
                String username = (String) request.getParameter("username");
                String password = (String) request.getParameter("password");
                UserDAO userDao = new UserDAO();
                User user = userDao.getByUname(username);
                if (user != null) {
                    userDao.modifyPasswd(user.getUid(), password);
                    out.println("1");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * ?????????????
         */
        if ("/hasname".equals(action)) {
            PrintWriter out = response.getWriter();
            try {
                String username = (String) request.getParameter("username");
                UserDAO userDAO = new UserDAO();
                User user = userDAO.getByUname(username);
                if (user == null) {
                    out.println("1");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * ???????
         */
        if ("/adduser".equals(action)) {
            PrintWriter out = response.getWriter();
            try {
                String username = (String) request.getParameter("username");
                String password = (String) request.getParameter("password");
                UserDAO userDao = new UserDAO();
                if (userDao.addUser(username, password)) {
                    out.println("1");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * ???
         */
        if ("/logout".equals(action)) {
            try {
                HttpSession session = request.getSession();
                session.invalidate();
                response.sendRedirect("login.jsp");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * ??????
         */
        if ("/upload".equals(action)) {
            //Session???
            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID == null || sessionID.equalsIgnoreCase(session.getId()) != true) {
                response.sendRedirect("login.jsp");
            }
            DiskFileItemFactory factory = new DiskFileItemFactory();//??????????
            String uploadDir = UPLOAD_PATH;//?????????????
            ServletFileUpload upload = new ServletFileUpload(factory);//??????????
            //??????????
            Progress progress = new Progress(session);
            upload.setProgressListener(progress);
            try {
                List<FileItem> items = upload.parseRequest(request);
                for (FileItem item : items) {
                    if (!item.isFormField()) {
                        //????????
                        String filename = item.getName();
                        filename = filename.substring(filename.lastIndexOf("\\") + 1);
                        //??????????
                        FileDAO fileDao = new FileDAO();
                        if (fileDao.getByFname(filename) != null) {
                            session.setAttribute("uploadState", "200");
                            return;
                        }
                        //????????????????
                        int uid = (Integer) session.getAttribute("uid");
                        fileDao.addFile(filename, uid);
                        //??????????????
                        InputStream in = item.getInputStream();

                        String realPath = this.getServletContext().getRealPath(this.getServletName());
                        realPath = realPath.substring(0, realPath.lastIndexOf("\\"));
                        String uploadPath = realPath + "\\upload\\" + uploadDir;

                        File file = new File(uploadPath, filename);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream fos = new FileOutputStream(file);
                        int len = -1;
                        byte[] buffer = new byte[1024];
                        while ((len = in.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                        in.close();
                        item.delete();
                        Thread thread = new Thread(refreshFile);
                        thread.start();
                    }
                }
            } catch (Exception e) {
                session.setAttribute("uploadState", "400");
                Thread thread = new Thread(refreshFile);
                thread.start();
                e.printStackTrace();
            }
        }

        /**
         * ???????
         */
        if ("/download".equals(action)) {
            //Session???
            HttpSession session = request.getSession();
            String Path = (String) session.getAttribute("Path");
            if (Path == "" || Path == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            try {
                response.setCharacterEncoding("utf-8");
                String downpath = Path + request.getParameter("fname");
                File file = new File(downpath);
                InputStream in = new FileInputStream(file);
                OutputStream fos = response.getOutputStream();
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes("gbk"), "iso-8859-1"));
                response.addHeader("Content-Length", file.length() + "");
                response.setContentType("application/octet-stream");
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                in.close();
            } catch (Exception e) {
                //???????????????
            }
        }

        /**
         * ?????????
         */
        if ("/uploadstate".equals(action)) {
            PrintWriter out = response.getWriter();
            //Session???
            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID.equalsIgnoreCase(session.getId())) {
                String msg = (String) session.getAttribute("uploadState");
                if ("100".equals(msg)) {
                    out.println("300");
                    session.setAttribute("uploadState", "0");
                } else {
                    out.println(msg);
                }
            }
        }

        /**
         * ??????
         */
        if ("/removefile".equals(action)) {
            PrintWriter out = response.getWriter();
            //Session???
            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID.equalsIgnoreCase(session.getId())) {
                String fname = request.getParameter("fname");
                FileDAO fileDao = new FileDAO();
                try {
                    File file = new File(UPLOAD_PATH + fname);
                    if (fileDao.removeFile(fname) && file.delete()) {
                        out.println(1);
                        Thread thread = new Thread(refreshFile);
                        thread.start();
                    }
                } catch (Exception e) {
                    out.println(0);
                    e.printStackTrace();
                }
            }
        }

        /**
         * ?????????????
         */
        if ("/showfile".equals(action)) {
            PrintWriter out = response.getWriter();
            //Session???
            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID.equalsIgnoreCase(session.getId())) {
                String access = (String) session.getAttribute("access");
                String searchcode = request.getParameter("searchcode");
//                List<FileInf> fileList = new ArrayList<FileInf>();
//                if ("".equals(searchcode)) {
//                    fileList = allFileList;
//                } else {
//                    fileList = new ArrayList<FileInf>();
//                    for (FileInf file : allFileList) {
//                        if (file.getFname().indexOf(searchcode) != -1 ||
//                                file.getCreatetime().indexOf(searchcode) != -1) {
//                            fileList.add(file);
//                        }
//                    }
//                }
                // ==================================================
                //     ?????
                // ==================================================
                String directory = request.getParameter("directory");
                int dirCode;
                if (directory == null || directory.equals(""))
                    dirCode = 0;
                else
                    dirCode = Integer.parseInt(directory);
                String type = request.getParameter("type");
                FileService.showFile(dirCode, out, access, type);
                return;
                // ==================================================
//                String htmlCode = "";
//                for (FileInf file : fileList) {
//                    UserDAO userDao = new UserDAO();
//                    User user = new User();
//                    try {
//                        user = userDao.getByUid(file.getUid());
//                    } catch (Exception e) {
//                        out.print("404");
//                        e.printStackTrace();
//                    }
//                    if ("2".equals(access)) {
//                        htmlCode += "" + "<tr>"
//                                + "<td><div style=\"width: 400px;\" class=\"uk-text-truncate\">" + file.getFname() + "</div></td>"
//                                + "<td>" + user.getUname() + "</td>"
//                                + "<td>" + file.getCreatetime() + "</td>"
//                                + "<td>"
//                                + "<a class=\"uk-button uk-button-danger uk-button-mini\" onclick=\"do_delete('" + file.getFname() + "');\">???</a>"
//                                + "<span>&nbsp;&nbsp;&nbsp;</span>"
//                                + "<a class=\"uk-button uk-button-success uk-button-mini\" href=\"download.do?fname=" + file.getFname() + "\">????</a>"
//                                + "</td>"
//                                + "</tr>";
//                    } else {
//                        htmlCode += "" + "<tr>"
//                                + "<td><div style=\"width: 400px;\" class=\"uk-text-truncate\">" + file.getFname() + "</div></td>"
//                                + "<td>" + user.getUname() + "</td>"
//                                + "<td>" + file.getCreatetime() + "</td>"
//                                + "<td>"
//                                + "<a class=\"uk-button uk-button-success uk-button-mini\" href=\"download.do?fname=" + file.getFname() + "\">????</a>"
//                                + "</td>"
//                                + "</tr>";
//                    }
//
//                }
//                out.println(htmlCode);
            }
        }

        /**
         * ?????????????(?????)
         */
        if ("/showfilesmall".equals(action)) {
            PrintWriter out = response.getWriter();
            //Session???
            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID.equalsIgnoreCase(session.getId())) {
                String access = (String) session.getAttribute("access");
                String htmlCode = "";
                for (FileInf file : allFileList) {
                    UserDAO userDao = new UserDAO();
                    User user = new User();
                    try {
                        user = userDao.getByUid(file.getUid());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if ("2".equals(access)) {
                        htmlCode += "" + "<tr>"
                                + "<td><div style=\"width: 156px;\" class=\"uk-text-truncate\">" + file.getFname() + "</div></td>"
                                + "<td>"
                                + "<a class=\"uk-button uk-button-danger uk-button-mini\" onclick=\"do_delete('" + file.getFname() + "');\">???</a>"
                                + "<span>&nbsp;&nbsp;&nbsp;</span>"
                                + "<a class=\"uk-button uk-button-success uk-button-mini\" href=\"download.do?fname=" + file.getFname() + "\">????</a>"
                                + "</td>"
                                + "</tr>";
                    } else {
                        htmlCode += "" + "<tr>"
                                + "<td><div style=\"width: 156px;\" class=\"uk-text-truncate\">" + file.getFname() + "</div></td>"
                                + "<td>"
                                + "<a class=\"uk-button uk-button-success uk-button-mini\" href=\"download.do?fname=" + file.getFname() + "\">????</a>"
                                + "</td>"
                                + "</tr>";
                    }

                }
                out.println(htmlCode);
            }
        }

        /**
         * ??????????
         */
        if ("/showview".equals(action)) {
            PrintWriter out = response.getWriter();
            //Session???
            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID.equalsIgnoreCase(session.getId())) {
                String access = (String) session.getAttribute("access");
                out.println(access);
            }
        }

        //============================================
        //    以下为新增代码
        //============================================
        /**
         * 刷新导航栏
         */
        if ("/updatenav".equals(action)) {
            PrintWriter out = response.getWriter();
            //Session验证
            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID.equalsIgnoreCase(session.getId())) {
                String id = request.getParameter("newid");
                String htmlCode = "";
                if (id.equals("0")) {
                    htmlCode = "全部分类";
                } else {
                    List<jerome.entity.FileInf> filelist = FileService.getSortList(id);
                    htmlCode = "<a onclick=\"do_my_click(0,0)\">全部分类</a>";
                    for (jerome.entity.FileInf f : filelist) {
                        htmlCode += " &gt; <a onclick=\"do_my_click(" + f.id + ",0)\">" + f.name + "</a>";
                    }
                    jerome.entity.FileInf lastInf = jerome.dao.FileDAO.getFileInfById(id);
                    htmlCode += " &gt; " + lastInf.name;
                }
                out.println(htmlCode);
            }
        }

        /**
         * 新建分类操作
         */
        if ("/newsort".equals(action)) {
            String sortname = request.getParameter("sortname");
            String dir = request.getParameter("dir");
            HttpSession session = request.getSession();
            Object o = session.getAttribute("uid");
            String uid = o.toString();
            PrintWriter out = response.getWriter();
            if (FileService.addSort(sortname, dir, uid))
                out.print("1");
            else
                out.print("0");
        }

    }

    /**
     * ????????????????
     *
     * @author Snail
     */
    private class RefreshFile implements Runnable {
        public synchronized void run() {
            FileDAO fileDao = new FileDAO();
            try {
                allFileList = fileDao.getAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ?????????
     *
     * @author Snail
     */
    private class Progress implements ProgressListener {
        private HttpSession session;
        long num = 0;

        public Progress(HttpSession session) {
            this.session = session;
        }

        public void update(long bytesRead, long contentLength, int items) {
            long progress = bytesRead * 100 / contentLength;
            if (progress == num) {
                return;
            }
            num = progress;
            session.setAttribute("uploadState", num + "");
        }
    }
}
