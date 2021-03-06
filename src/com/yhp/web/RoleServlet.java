package com.yhp.web;

import com.yhp.bean.Menu;
import com.yhp.bean.Role;
import com.yhp.service.RoleService;
import com.yhp.service.impl.RoleServiceImpl;
import com.yhp.util.PageUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/power/role/roles"})
public class RoleServlet extends HttpServlet {
    private RoleService roleService = new RoleServiceImpl();


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method.equals("select")) {
            select(req, resp);
        } else if (method.equals("add")) {
            add(req, resp);
        } else if (method.equals("save")) {
            save(req, resp);
        } else if (method.equals("modify")) {
            modify(req, resp);
        }else if(method.equals("delete")){
            delete(req,resp);
        }
    }

    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String rid = req.getParameter("rid");
        int roleId = Integer.parseInt(rid);
        int j = roleService.removeMid(roleId);
        int k = roleService.removeRole(roleId);
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter writer = resp.getWriter();
        if (k > 0) {
            writer.println("<script>alert('删除成功');location.href='/power/role/roles?method=select'</script>");
        } else {
            writer.println("<script>alert('删除失败');location.href='power/role/roles?method=select'</script>");
        }

    }

    protected void select(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String index = req.getParameter("index");
        int pageIndex = (index == null || index.length() == 0) ? 1 : Integer.parseInt(index);

        PageUtil pageUtil = new PageUtil();
        List<Role> usersList = roleService.getAllRole(pageIndex, pageUtil.getPageSize());
        int total = roleService.total();
        pageUtil.setTotal(total);
        pageUtil.setPageIndex(pageIndex);
        pageUtil.setDataList(usersList);

        req.setAttribute("pi", pageUtil);
        req.getRequestDispatcher("list.jsp").forward(req, resp);

    }

    protected void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Menu> all = roleService.getAllMenu();
        List<Menu> newAll = new ArrayList<>();
        for (Menu menu : all) {
            if (menu.getUpMenuId() == 0) {
                List<Menu> secMenu = new ArrayList<>();
                for (Menu secondMenu : all) {
                    if (menu.getMenuId() == secondMenu.getUpMenuId()) {
                        secMenu.add(secondMenu);
                    }
                }
                menu.setSecondMenu(secMenu);
                newAll.add(menu);
            }
        }
        req.setAttribute("all", newAll);
        req.getRequestDispatcher("add.jsp").forward(req, resp);
    }

    protected void save(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Role roles = new Role();
        String rid = req.getParameter("rid");
        String roleName = req.getParameter("name");
        int state = Integer.parseInt(req.getParameter("state"));
        String[] menuId = req.getParameterValues("menu");

        resp.setContentType("text/html;charset=utf-8");
        PrintWriter writer = resp.getWriter();
        if (rid == null) {
            int i = roleService.insertRole(null,roleName, state, menuId);
            if (i > 0) {
                writer.println("<script>alert('新增成功');location.href='/power/role/roles?method=select'</script>");
            } else {
                writer.println("<script>alert('新增失败');location.href='power/role/roles?method=add'</script>");
            }

        }else {
            int roleId = Integer.parseInt(rid);
            int j = roleService.removeMid(roleId);
            int k = roleService.removeRole(roleId);
            int i = roleService.insertRole(rid,roleName, state, menuId);
            if (i > 0 ) {
                writer.println("<script>alert('修改成功');location.href='/power/role/roles?method=select'</script>");
            } else {
                writer.println("<script>alert('修改失败');location.href='power/role/roles?method=modify&rid='"+rid+"</script>");
            }
        }

    }

    protected void modify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int uid = Integer.parseInt(req.getParameter("rid"));
        String act = req.getParameter("act");
        Role role = roleService.findRole(uid);
        List<Menu> all = roleService.getAllMenu();
        List<Menu> newAll = new ArrayList<>();
        for (Menu menu : role.getMenuList()) {
            int menuId = menu.getMenuId();
            for (Menu menu1 : all) {
                if (menu1.getMenuId() == menuId) {
                    menu1.setUse(1);
                }
            }
        }
        for (Menu menu : all) {
            if (menu.getUpMenuId() == 0) {
                List<Menu> secMenu = new ArrayList<>();
                for (Menu secondMenu : all) {
                    if (menu.getMenuId() == secondMenu.getUpMenuId()) {
                        secMenu.add(secondMenu);
                    }
                }
                menu.setSecondMenu(secMenu);
                newAll.add(menu);
            }
        }
        req.setAttribute("all", newAll);
        req.setAttribute("role", role);
        if(act.equals("edit")){
            req.getRequestDispatcher("edit.jsp").forward(req, resp);
        }else {
            req.getRequestDispatcher("info.jsp").forward(req, resp);
        }
    }

}
