package jerome.rules.web;

import java.io.File;
import java.io.FileInputStream;
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

import jerome.rules.dao.UserDAO;
import jerome.rules.dao.FileDAO;
import jerome.rules.entity.User;
import jerome.rules.entity.FileInf;
import jerome.rules.service.FileService;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Created by Jerome on 2017/2/23.
 */
public class ActionServlet extends HttpServlet {

    private static String UPLOAD_PATH = "";
    private static String ALL_SORT = "";
    public static String DOWNLOAD = "";
    public static String DELETE = "";
    public static String MODIFY = "";
    public static String NEW_SORT = "";

    public void init() throws ServletException {
        super.init();
        UPLOAD_PATH = this.getInitParameter("UPLOAD_PATH");
        ALL_SORT = this.getInitParameter("ALL_SORT");
        DOWNLOAD = this.getInitParameter("DOWNLOAD");
        DELETE = this.getInitParameter("DELETE");
        MODIFY = this.getInitParameter("MODIFY");
        NEW_SORT = this.getInitParameter("NEW_SORT");
    }

    /**
     * Service方法
     */
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        request.setCharacterEncoding("utf-8");
        String uri = request.getRequestURI();
        String action = uri.substring(uri.lastIndexOf("/"), uri.lastIndexOf("."));

        /**
         * 登录
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 修改密码
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
         * 查询用户是否存在
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
         * 添加用户
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
         * 注销用户
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
         * 上传文件
         */
        if ("/upload".equals(action)) {
            //Session验证
            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID == null || sessionID.equalsIgnoreCase(session.getId()) != true) {
                response.sendRedirect("login.jsp");
            }
            DiskFileItemFactory factory = new DiskFileItemFactory();//??????????
            String uploadDir = UPLOAD_PATH;//?????????��??
            ServletFileUpload upload = new ServletFileUpload(factory);//??????????
            //进度管理
            Progress progress = new Progress(session);
            upload.setProgressListener(progress);
            try {
//                List<FileItem> items = upload.parseRequest(request);
//                for (FileItem item : items) {
//                    if (!item.isFormField()) {
//                        //????????
//                        String filename = item.getName();
//                        filename = filename.substring(filename.lastIndexOf("\\") + 1);
//                        //??????????
//                        FileDAO fileDao = new FileDAO();
//                        if (fileDao.getByFname(filename) != null) {
//                            session.setAttribute("uploadState", "200");
//                            return;
//                        }
//                        //????????��??????
//                        int uid = (Integer) session.getAttribute("uid");
//                        fileDao.addFile(filename, uid);
//                        //???????��?????
//                        InputStream in = item.getInputStream();
//
//                        String realPath = this.getServletContext().getRealPath(this.getServletName());
//                        realPath = realPath.substring(0, realPath.lastIndexOf("\\"));
//                        String uploadPath = realPath + "\\upload\\" + uploadDir;
//
//                        File file = new File(uploadPath, filename);
//                        if (!file.exists()) {
//                            file.createNewFile();
//                        }
//                        FileOutputStream fos = new FileOutputStream(file);
//                        int len = -1;
//                        byte[] buffer = new byte[1024];
//                        while ((len = in.read(buffer)) != -1) {
//                            fos.write(buffer, 0, len);
//                        }
//                        fos.close();
//                        in.close();
//                        item.delete();
//                        Thread thread = new Thread(refreshFile);
//                        thread.start();
//                    }
//                }
            } catch (Exception e) {
                session.setAttribute("uploadState", "400");
                e.printStackTrace();
            }
        }

        /**
         * 下载文件
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
                e.printStackTrace();
            }
        }

        /**
         * 获取上传状态
         */
        if ("/uploadstate".equals(action)) {
            PrintWriter out = response.getWriter();
            //Session验证
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

//        if ("/removefile".equals(action)) {
//            PrintWriter out = response.getWriter();
//            //Session???
//            HttpSession session = request.getSession();
//            String sessionID = (String) session.getAttribute("sessionID");
//            if (sessionID.equalsIgnoreCase(session.getId())) {
//                String fname = request.getParameter("fname");
//                FileDAO fileDao = new FileDAO();
//                try {
//                    File file = new File(UPLOAD_PATH + fname);
//                    if (fileDao.removeFile(fname) && file.delete()) {
//                        out.println(1);
//                        Thread thread = new Thread(refreshFile);
//                        thread.start();
//                    }
//                } catch (Exception e) {
//                    out.println(0);
//                    e.printStackTrace();
//                }
//            }
//        }

        /**
         * 显示所有文件
         */
        if ("/showfile".equals(action)) {
            PrintWriter out = response.getWriter();
            //Session验证
            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID.equalsIgnoreCase(session.getId())) {
                String access = (String) session.getAttribute("access");
                String searchcode = request.getParameter("searchcode");
                // ==================================================
                //     新增代码
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
            }
        }


        /**
         * ??????????
         */
        if ("/showview".equals(action)) {
            PrintWriter out = response.getWriter();
            //Session验证
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
                    htmlCode = ALL_SORT;
                } else {
                    List<FileInf> filelist = FileService.getSortList(id);
                    htmlCode = "<a onclick=\"do_my_click(0,0)\">" + ALL_SORT + "</a>";
                    for (FileInf f : filelist) {
                        htmlCode += " &gt; <a onclick=\"do_my_click(" + f.id + ",0)\">" + f.name + "</a>";
                    }
                    FileInf lastInf = FileDAO.getFileInfById(id);
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
            //Session验证
            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID.equalsIgnoreCase(session.getId())) {
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
         * 移除删除文件或分类（放入回收站）
         */
        if ("/removefile".equals(action)) {
            String fileid = request.getParameter("fileid");
            PrintWriter out = response.getWriter();
            //Session验证
            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID.equalsIgnoreCase(session.getId()))
                if (FileDAO.deleteFile(fileid))
                    out.print("1");
            out.print("0");
        }

        /**
         * 彻底删除文件或分类
         */
        if ("/clearfile".equals(action)) {
            String fileid = request.getParameter("fileid");
            PrintWriter out = response.getWriter();
            //Session验证
            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID.equalsIgnoreCase(session.getId())) {
                FileInf fileInf = FileDAO.getFileInfById(fileid);
                if (fileInf.type == 1) {       //如果是文件，直接删除
                    FileDAO.clearFile(fileid);
                    out.print("1");
                    return;
                } else {                       //如果是目录，删除其中所有目录和文件
                    if (deleteOneDir(fileid)) {
                        out.print("1");
                        return;
                    }
                }
            }
            out.print("0");
            return;
        }

    }

    private boolean deleteOneDir(String fileid) {
        // 删除所有子目录
        ArrayList<Integer> subList = FileDAO.showSubDirs(fileid);
        for (int id : subList)
            if (!deleteOneDir(id + ""))
                return false;
        // 删除所有文件
        ArrayList<Integer> subFile = FileDAO.showSubFiles(fileid);
        for (int id : subFile)
            if (!FileDAO.clearFile(id + ""))
                return false;
        // 删除该目录本身
        if (!FileDAO.clearDir(fileid))
            return false;
        return true;
    }

    /**
     * 进度管理
     *
     * @author Jerome
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
