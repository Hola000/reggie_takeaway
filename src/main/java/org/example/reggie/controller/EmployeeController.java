package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.reggie.common.R;
import org.example.reggie.entity.Employee;
import org.example.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping(value = "/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1 encode the password with md5 algorithm
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2 search for the username in the database
        //mybatis-plus:LambdaQueryWrapper
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper); //username is unique

        //3.1 no results -> login failed
        if (emp == null) {
            return R.error("no user, login failed");
        }

        //3.2 password not correct
        if (!emp.getPassword().equals(password)) {
            return R.error("wrong password, login failed");
        }

        //3.3 user not available
        if (emp.getStatus() == 0) {
            return R.error("The account is not available");
        }

        //3.4 login successfully
        //save userid to session
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping(value = "/logout")
    public R<String> logout(HttpServletRequest request) {
        // remove user id in the session
        request.getSession().removeAttribute("employee");
        return R.success("logout successfully");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        String password = "123456";
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        employee.setPassword(password);

        // get the user id
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employee.setCreateTime(LocalDate.now());
        employee.setUpdateTime(LocalDate.now());

        //try {
            employeeService.save(employee);
        //} catch (Exception exception) {
        //    return R.error("fail to add an employee");
        //}

        return R.success("add an employee successfully");
    }

// mybatis-plus-pagination
    @GetMapping("/page")
        public R<Page> page(int page, int pageSize, String name) {
            log.info("page={},pagesizee={},name={}",page,pageSize,name);
            Page pageInfo = new Page(page, pageSize);
            LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
            queryWrapper.orderByDesc(Employee::getUpdateTime);
            employeeService.page(pageInfo, queryWrapper);
            return R.success(pageInfo);
        }





}
