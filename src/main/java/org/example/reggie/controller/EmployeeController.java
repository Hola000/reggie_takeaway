package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.example.reggie.entity.Employee;
import org.example.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

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

}
