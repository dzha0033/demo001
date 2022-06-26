package com.yhp.dao;

import com.yhp.bean.Menu;
import com.yhp.bean.Role;

import java.util.List;

public interface RoleDao {

    public List<Role> getAllRole(int pageIndex, int pageSize);

    public int total();

    public List<Menu> getAllMenu();

    int insertRol(Role role);
}
